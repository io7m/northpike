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


package com.io7m.northpike.tests.server.archives;

import com.io7m.ervilla.test_extension.ErvillaConfiguration;
import com.io7m.ervilla.test_extension.ErvillaExtension;
import com.io7m.northpike.database.api.NPDatabaseConfiguration;
import com.io7m.northpike.database.api.NPDatabaseConnectionType;
import com.io7m.northpike.database.api.NPDatabaseCreate;
import com.io7m.northpike.database.api.NPDatabaseFactoryType;
import com.io7m.northpike.database.api.NPDatabaseQueriesArchivesType;
import com.io7m.northpike.database.api.NPDatabaseTransactionType;
import com.io7m.northpike.database.api.NPDatabaseType;
import com.io7m.northpike.database.api.NPDatabaseUpgrade;
import com.io7m.northpike.database.postgres.NPPGDatabases;
import com.io7m.northpike.model.NPArchive;
import com.io7m.northpike.model.NPCommitID;
import com.io7m.northpike.model.NPHash;
import com.io7m.northpike.model.NPToken;
import com.io7m.northpike.server.api.NPServerAgentConfiguration;
import com.io7m.northpike.server.api.NPServerArchiveConfiguration;
import com.io7m.northpike.server.api.NPServerConfiguration;
import com.io7m.northpike.server.api.NPServerDirectoryConfiguration;
import com.io7m.northpike.server.api.NPServerIdstoreConfiguration;
import com.io7m.northpike.server.internal.archives.NPArchiveService;
import com.io7m.northpike.server.internal.archives.NPArchiveServiceType;
import com.io7m.northpike.server.internal.clock.NPClock;
import com.io7m.northpike.server.internal.clock.NPClockServiceType;
import com.io7m.northpike.server.internal.configuration.NPConfigurationServiceType;
import com.io7m.northpike.server.internal.events.NPEventService;
import com.io7m.northpike.server.internal.metrics.NPMetricsServiceType;
import com.io7m.northpike.server.internal.telemetry.NPTelemetryNoOp;
import com.io7m.northpike.strings.NPStrings;
import com.io7m.northpike.telemetry.api.NPEventServiceType;
import com.io7m.northpike.telemetry.api.NPTelemetryServiceType;
import com.io7m.northpike.tests.NPEventInterceptingService;
import com.io7m.repetoir.core.RPServiceDirectory;
import com.io7m.zelador.test_extension.CloseableResourcesType;
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.io7m.northpike.tls.NPTLSDisabled.TLS_DISABLED;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(disabledIfUnsupported = true)
public final class NPArchiveServiceTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(NPArchiveServiceTest.class);

  private RPServiceDirectory services;
  private NPClockServiceType clock;
  private NPConfigurationServiceType configuration;
  private NPStrings strings;
  private NPTelemetryNoOp telemetry;
  private NPEventInterceptingService events;
  private NPMetricsServiceType metrics;
  private NPArchiveServiceType service;
  private NPDatabaseType database;
  private NPDatabaseFactoryType databases;
  private NPDatabaseConnectionType connection;
  private NPDatabaseTransactionType transaction;

  @BeforeEach
  public void setup(
    final CloseableResourcesType closeables,
    final @TempDir Path reposDirectory,
    final @TempDir Path archiveDirectory)
    throws Exception
  {
    this.services =
      new RPServiceDirectory();
    this.clock =
      new NPClock(Clock.systemUTC());
    this.configuration =
      Mockito.mock(NPConfigurationServiceType.class);
    this.strings =
      NPStrings.create(Locale.ROOT);
    this.telemetry =
      NPTelemetryNoOp.noop();
    this.events =
      new NPEventInterceptingService(
        NPEventService.create(NPTelemetryNoOp.noop())
      );
    this.metrics =
      Mockito.mock(NPMetricsServiceType.class);
    this.database =
      Mockito.mock(NPDatabaseType.class);
    this.databases =
      Mockito.mock(NPDatabaseFactoryType.class);
    this.connection =
      Mockito.mock(NPDatabaseConnectionType.class);
    this.transaction =
      Mockito.mock(NPDatabaseTransactionType.class);

    Mockito.when(this.database.openConnection(any()))
      .thenReturn(this.connection);
    Mockito.when(this.connection.openTransaction())
      .thenReturn(this.transaction);

    this.services.register(
      NPClockServiceType.class, this.clock);
    this.services.register(
      NPConfigurationServiceType.class, this.configuration);
    this.services.register(
      NPStrings.class, this.strings);
    this.services.register(
      NPTelemetryServiceType.class, this.telemetry);
    this.services.register(
      NPEventServiceType.class, this.events);
    this.services.register(
      NPMetricsServiceType.class, this.metrics);
    this.services.register(
      NPDatabaseType.class, this.database);

    Mockito.when(this.configuration.configuration())
      .thenReturn(
        new NPServerConfiguration(
          Locale.ROOT,
          Clock.systemUTC(),
          this.strings,
          new NPPGDatabases(),
          new NPDatabaseConfiguration(
            "x",
            "x",
            "x",
            Optional.empty(),
            "localhost",
            15432,
            "northpike",
            NPDatabaseCreate.CREATE_DATABASE,
            NPDatabaseUpgrade.UPGRADE_DATABASE,
            true,
            "english",
            Clock.systemUTC(),
            this.strings
          ),
          new NPServerDirectoryConfiguration(
            reposDirectory,
            archiveDirectory
          ),
          new NPServerIdstoreConfiguration(
            URI.create("http://example.com:30000/"),
            URI.create("http://example.com:30000/")
          ),
          new NPServerAgentConfiguration(
            InetAddress.getLocalHost(),
            40000,
            TLS_DISABLED,
            1_000_000
          ),
          new NPServerArchiveConfiguration(
            InetAddress.getLocalHost(),
            40001,
            TLS_DISABLED,
            URI.create("https://archives.example.com/")
          ),
          Optional.empty()
        )
      );

    this.service =
      NPArchiveService.create(this.services);
  }

  @AfterEach
  public void tearDown()
    throws Exception
  {
    this.service.close();
  }

  /**
   * Archives can be missing.
   *
   * @throws Exception On errors
   */

  @Test
  public void testArchiveNotFound()
    throws Exception
  {
    final var get =
      Mockito.mock(NPDatabaseQueriesArchivesType.GetType.class);

    Mockito.when(get.execute(any()))
      .thenReturn(Optional.empty());

    Mockito.when(
        this.transaction.queries(NPDatabaseQueriesArchivesType.GetType.class))
      .thenReturn(get);

    this.service.start().get(1L, TimeUnit.SECONDS);

    final var client =
      HttpClient.newHttpClient();
    final var request =
      HttpRequest.newBuilder(URI.create(
          "http://localhost:40001/454349E422F05297191EAD13E21D3DB520E5ABEF52055E4964B82FB213F593A1"))
        .build();
    final var response =
      client.send(request, HttpResponse.BodyHandlers.ofString());

    LOG.debug("{}", response.body());
    assertEquals(404, response.statusCode());
  }

  /**
   * Archive files can be missing.
   *
   * @throws Exception On errors
   */

  @Test
  public void testArchiveFileMissing()
    throws Exception
  {
    final var get =
      Mockito.mock(NPDatabaseQueriesArchivesType.GetType.class);

    final var archive =
      new NPArchive(
        new NPToken(
          "454349E422F05297191EAD13E21D3DB520E5ABEF52055E4964B82FB213F593A1"),
        new NPCommitID(
          UUID.randomUUID(),
          "432a852395d4d587440e508a9da46f3d05ab67dd6b784d7bef84a50ce25e9e16"
        ),
        new NPHash(
          "SHA-256",
          "6B7050550B90418DD615C9C7182ECD31189BC6CC3226F56D90A5A2A70EEE03A2"
        ),
        OffsetDateTime.now().withNano(0)
      );

    Mockito.when(get.execute(any()))
      .thenReturn(Optional.of(archive));

    Mockito.when(
        this.transaction.queries(NPDatabaseQueriesArchivesType.GetType.class))
      .thenReturn(get);

    this.service.start().get(1L, TimeUnit.SECONDS);

    final var client =
      HttpClient.newHttpClient();
    final var request =
      HttpRequest.newBuilder(URI.create(
          "http://localhost:40001/454349E422F05297191EAD13E21D3DB520E5ABEF52055E4964B82FB213F593A1"))
        .build();
    final var response =
      client.send(request, HttpResponse.BodyHandlers.ofString());

    LOG.debug("{}", response.body());
    assertEquals(500, response.statusCode());
  }

  /**
   * Archive files are served correctly.
   *
   * @throws Exception On errors
   */

  @Test
  public void testArchiveOK()
    throws Exception
  {
    final var data = new byte[80000];
    SecureRandom.getInstanceStrong()
      .nextBytes(data);

    Files.write(
      this.configuration.configuration()
        .directories()
        .archiveDirectory()
        .resolve(
          "454349E422F05297191EAD13E21D3DB520E5ABEF52055E4964B82FB213F593A1.tgz"),
      data
    );

    final var get =
      Mockito.mock(NPDatabaseQueriesArchivesType.GetType.class);

    final var archive =
      new NPArchive(
        new NPToken(
          "454349E422F05297191EAD13E21D3DB520E5ABEF52055E4964B82FB213F593A1"),
        new NPCommitID(
          UUID.randomUUID(),
          "432a852395d4d587440e508a9da46f3d05ab67dd6b784d7bef84a50ce25e9e16"
        ),
        new NPHash(
          "SHA-256",
          "6B7050550B90418DD615C9C7182ECD31189BC6CC3226F56D90A5A2A70EEE03A2"
        ),
        OffsetDateTime.now().withNano(0)
      );

    Mockito.when(get.execute(any()))
      .thenReturn(Optional.of(archive));

    Mockito.when(
        this.transaction.queries(NPDatabaseQueriesArchivesType.GetType.class))
      .thenReturn(get);

    this.service.start().get(1L, TimeUnit.SECONDS);

    final var client =
      HttpClient.newHttpClient();
    final var request =
      HttpRequest.newBuilder(URI.create(
          "http://localhost:40001/454349E422F05297191EAD13E21D3DB520E5ABEF52055E4964B82FB213F593A1"))
        .build();
    final var response =
      client.send(request, HttpResponse.BodyHandlers.ofByteArray());

    assertArrayEquals(data, response.body());
    assertEquals(200, response.statusCode());
  }
}