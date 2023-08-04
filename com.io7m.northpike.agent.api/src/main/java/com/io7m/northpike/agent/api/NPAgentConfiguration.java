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

package com.io7m.northpike.agent.api;

import com.io7m.northpike.model.NPKey;
import com.io7m.northpike.strings.NPStrings;

import java.net.InetAddress;
import java.util.Objects;

/**
 * The agent configuration.
 *
 * @param strings          The string resources
 * @param remoteAddress    The remote server address
 * @param remotePort       The remote server port
 * @param accessKey        The access key for the agent
 * @param useTLS           {@code true} if TLS should be used to secure the connection
 * @param messageSizeLimit The size limit for received messages
 */

public record NPAgentConfiguration(
  NPStrings strings,
  InetAddress remoteAddress,
  int remotePort,
  boolean useTLS,
  NPKey accessKey,
  int messageSizeLimit)
{
  /**
   * The agent configuration.
   *
   * @param strings          The string resources
   * @param remoteAddress    The remote server address
   * @param remotePort       The remote server port
   * @param accessKey        The access key for the agent
   * @param useTLS           {@code true} if TLS should be used to secure the connection
   * @param messageSizeLimit The size limit for received messages
   */

  public NPAgentConfiguration
  {
    Objects.requireNonNull(strings, "strings");
    Objects.requireNonNull(remoteAddress, "remoteAddress");
    Objects.requireNonNull(accessKey, "accessKey");
  }
}
