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


package com.io7m.northpike.tests.containers;

import com.io7m.ervilla.api.EReadyCheckType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class NPIdstoreHealthcheck implements EReadyCheckType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(NPIdstoreHealthcheck.class);

  private final String address;
  private final int port;

  public NPIdstoreHealthcheck(
    final String inAddress,
    final int inPort)
  {
    this.address = inAddress;
    this.port = inPort;
  }

  @Override
  public boolean isReady()
    throws Exception
  {
    final var client =
      HttpClient.newHttpClient();

    final var endpoint =
      URI.create(
        String.format(
          "http://%s:%d/health",
          this.address,
          Integer.valueOf(this.port)
        )
      );

    LOG.debug("Checking {}", endpoint);

    final var request =
      HttpRequest.newBuilder(endpoint)
        .build();

    final var response =
      client.send(request, HttpResponse.BodyHandlers.ofString());

    final var body =
      response.body()
        .trim();

    LOG.debug(
      "Server said: {} {}",
      Integer.valueOf(response.statusCode()),
      body
    );

    return response.statusCode() == 200 && "OK".equals(body);
  }
}
