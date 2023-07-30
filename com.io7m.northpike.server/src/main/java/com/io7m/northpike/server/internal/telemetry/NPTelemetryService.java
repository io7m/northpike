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


package com.io7m.northpike.server.internal.telemetry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.logs.Logger;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;

import java.util.Objects;

/**
 * An OpenTelemetry service.
 */

public final class NPTelemetryService
  implements NPTelemetryServiceType
{
  private final Tracer tracer;
  private final Meter meter;
  private final Logger logger;
  private final OpenTelemetry openTelemetry;

  /**
   * An OpenTelemetry service.
   *
   * @param inOpenTelemetry The OpenTelemetry instance
   * @param inTracer        The tracer instance
   * @param inMeter         The meter
   * @param inLogger        The logger
   */

  public NPTelemetryService(
    final OpenTelemetry inOpenTelemetry,
    final Tracer inTracer,
    final Meter inMeter,
    final Logger inLogger)
  {
    this.openTelemetry =
      Objects.requireNonNull(inOpenTelemetry, "inOpenTelemetry");
    this.tracer =
      Objects.requireNonNull(inTracer, "tracer");
    this.meter =
      Objects.requireNonNull(inMeter, "meter");
    this.logger =
      Objects.requireNonNull(inLogger, "logger");
  }

  @Override
  public String toString()
  {
    return "[NPTelemetryService 0x%s]"
      .formatted(Long.toUnsignedString(this.hashCode(), 16));
  }

  @Override
  public String description()
  {
    return "Server OpenTelemetry service.";
  }

  @Override
  public Tracer tracer()
  {
    return this.tracer;
  }

  @Override
  public Meter meter()
  {
    return this.meter;
  }

  @Override
  public Logger logger()
  {
    return this.logger;
  }

  @Override
  public boolean isNoOp()
  {
    return false;
  }

  @Override
  public OpenTelemetry openTelemetry()
  {
    return this.openTelemetry;
  }
}
