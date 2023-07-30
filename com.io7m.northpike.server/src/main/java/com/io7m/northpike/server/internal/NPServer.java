/*
 * Copyright © 2023 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.northpike.server.internal;


import com.io7m.idstore.model.IdName;
import com.io7m.jmulticlose.core.CloseableCollection;
import com.io7m.jmulticlose.core.CloseableCollectionType;
import com.io7m.medrina.api.MSubject;
import com.io7m.northpike.database.api.NPDatabaseException;
import com.io7m.northpike.database.api.NPDatabaseQueriesUsersType;
import com.io7m.northpike.database.api.NPDatabaseTelemetry;
import com.io7m.northpike.database.api.NPDatabaseType;
import com.io7m.northpike.model.NPErrorCode;
import com.io7m.northpike.model.NPUser;
import com.io7m.northpike.server.api.NPServerConfiguration;
import com.io7m.northpike.server.api.NPServerException;
import com.io7m.northpike.server.api.NPServerType;
import com.io7m.northpike.server.internal.metrics.NPMetricsService;
import com.io7m.northpike.server.internal.metrics.NPMetricsServiceType;
import com.io7m.northpike.server.internal.security.NPSecurityPolicy;
import com.io7m.northpike.server.internal.telemetry.NPTelemetryNoOp;
import com.io7m.northpike.server.internal.telemetry.NPTelemetryServiceFactoryType;
import com.io7m.northpike.server.internal.telemetry.NPTelemetryServiceType;
import com.io7m.northpike.strings.NPStrings;
import com.io7m.repetoir.core.RPServiceDirectory;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.io7m.northpike.database.api.NPDatabaseRole.NORTHPIKE;
import static java.lang.Integer.toUnsignedString;

/**
 * The basic server frontend.
 */

public final class NPServer implements NPServerType
{
  private final NPServerConfiguration configuration;
  private final AtomicBoolean stopped;
  private CloseableCollectionType<NPServerException> resources;
  private NPTelemetryServiceType telemetry;
  private NPDatabaseType database;

  /**
   * The basic server frontend.
   *
   * @param inConfiguration The server configuration
   */

  public NPServer(
    final NPServerConfiguration inConfiguration)
  {
    this.configuration =
      Objects.requireNonNull(inConfiguration, "configuration");
    this.resources =
      createResourceCollection();
    this.stopped =
      new AtomicBoolean(true);
  }

  private static CloseableCollectionType<NPServerException> createResourceCollection()
  {
    return CloseableCollection.create(
      () -> {
        return new NPServerException(
          "Server creation failed.",
          new NPErrorCode("server-creation"),
          Map.of(),
          Optional.empty()
        );
      }
    );
  }

  @Override
  public void start()
    throws NPServerException
  {
    try {
      if (this.stopped.compareAndSet(true, false)) {
        this.resources = createResourceCollection();
        this.telemetry = this.createTelemetry();

        final var startupSpan =
          this.telemetry.tracer()
            .spanBuilder("NPServer.start")
            .setSpanKind(SpanKind.INTERNAL)
            .startSpan();

        try {
          this.database =
            this.resources.add(
              this.createDatabase(
                new NPDatabaseTelemetry(
                  this.telemetry.isNoOp(),
                  this.telemetry.meter(),
                  this.telemetry.tracer()
                )
              )
            );

          final var services =
            this.resources.add(this.createServiceDirectory(this.database));

        } catch (final NPDatabaseException e) {
          startupSpan.recordException(e);

          try {
            this.close();
          } catch (final NPServerException ex) {
            e.addSuppressed(ex);
          }
          throw new NPServerException(
            e.getMessage(),
            e,
            new NPErrorCode("database"),
            e.attributes(),
            e.remediatingAction()
          );
        } catch (final Exception e) {
          startupSpan.recordException(e);

          try {
            this.close();
          } catch (final NPServerException ex) {
            e.addSuppressed(ex);
          }
          throw new NPServerException(
            e.getMessage(),
            e,
            new NPErrorCode("startup"),
            Map.of(),
            Optional.empty()
          );
        } finally {
          startupSpan.end();
        }
      }
    } catch (final Throwable e) {
      this.close();
      throw e;
    }
  }

  private RPServiceDirectoryType createServiceDirectory(
    final NPDatabaseType newDatabase)
  {
    final var services = new RPServiceDirectory();
    final var strings = this.configuration.strings();
    services.register(NPStrings.class, strings);

    services.register(NPTelemetryServiceType.class, this.telemetry);
    services.register(NPDatabaseType.class, newDatabase);

    final var metrics = new NPMetricsService(this.telemetry);
    services.register(NPMetricsServiceType.class, metrics);
    return services;
  }

  private NPDatabaseType createDatabase(
    final NPDatabaseTelemetry dbTelemetry)
    throws NPDatabaseException
  {
    return this.configuration.databases()
      .open(
        this.configuration.databaseConfiguration(),
        dbTelemetry,
        event -> {

        });
  }

  private NPTelemetryServiceType createTelemetry()
  {
    return this.configuration.openTelemetry()
      .flatMap(config -> {
        final var loader =
          ServiceLoader.load(NPTelemetryServiceFactoryType.class);
        return loader.findFirst().map(f -> f.create(config));
      }).orElseGet(NPTelemetryNoOp::noop);
  }

  @Override
  public NPDatabaseType database()
  {
    if (this.stopped.get()) {
      throw new IllegalStateException("Server is not started.");
    }

    return this.database;
  }

  @Override
  public NPServerConfiguration configuration()
  {
    return this.configuration;
  }

  @Override
  public boolean isClosed()
  {
    return this.stopped.get();
  }

  @Override
  public void setUserAsAdmin(
    final UUID adminId,
    final IdName adminName)
    throws NPServerException
  {
    Objects.requireNonNull(adminId, "adminId");
    Objects.requireNonNull(adminName, "adminName");

    final var newTelemetry =
      this.createTelemetry();

    final var dbTelemetry =
      new NPDatabaseTelemetry(
        newTelemetry.isNoOp(),
        newTelemetry.meter(),
        newTelemetry.tracer()
      );

    final var dbConfiguration =
      this.configuration.databaseConfiguration()
        .withoutUpgradeOrCreate();

    try (var newDatabase =
           this.configuration.databases()
             .open(dbConfiguration, dbTelemetry, event -> {

             })) {

      final var span =
        newTelemetry.tracer()
          .spanBuilder("SetUserAsAdmin")
          .startSpan();

      try (var ignored = span.makeCurrent()) {
        setUserAsAdminSpan(newDatabase, adminId, adminName);
      } catch (final Exception e) {
        span.recordException(e);
        span.setStatus(StatusCode.ERROR);
        throw e;
      } finally {
        span.end();
      }
    } catch (final NPDatabaseException e) {
      throw new NPServerException(
        e.getMessage(),
        e,
        e.errorCode(),
        e.attributes(),
        e.remediatingAction()
      );
    }
  }

  private static void setUserAsAdminSpan(
    final NPDatabaseType database,
    final UUID adminId,
    final IdName adminName)
    throws NPServerException
  {
    try (var connection =
           database.openConnection(NORTHPIKE)) {
      try (var transaction =
             connection.openTransaction()) {
        final var put =
          transaction.queries(NPDatabaseQueriesUsersType.PutType.class);

        transaction.setUserId(adminId);
        put.execute(
          new NPUser(
            adminId,
            adminName,
            new MSubject(NPSecurityPolicy.ROLES_ALL)
          )
        );

        transaction.commit();
      }
    } catch (final NPDatabaseException e) {
      throw new NPServerException(
        e.getMessage(),
        e,
        e.errorCode(),
        e.attributes(),
        e.remediatingAction()
      );
    }
  }

  @Override
  public void close()
    throws NPServerException
  {
    if (this.stopped.compareAndSet(false, true)) {
      this.resources.close();
    }
  }

  @Override
  public String toString()
  {
    return "[NPServer 0x%s]".formatted(toUnsignedString(this.hashCode(), 16));
  }
}
