/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
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


package com.io7m.northpike.agent.internal.status;

import com.io7m.northpike.model.agents.NPAgentStatusHealth;
import com.io7m.northpike.strings.NPStrings;

import java.util.Objects;

import static com.io7m.northpike.strings.NPStringConstants.AGENT_STATUS_CONNECTION_CONNECTING;

/**
 * The agent is connecting to the server.
 *
 * @param hostName The server hostname
 */

public record NPAgentConnectionStatusConnecting(
  String hostName)
  implements NPAgentConnectionStatusType
{
  /**
   * The agent is connecting to the server.
   *
   * @param hostName The server hostname
   */

  public NPAgentConnectionStatusConnecting
  {
    Objects.requireNonNull(hostName, "hostName");
  }

  @Override
  public String format(
    final NPStrings strings)
  {
    return strings.format(
      AGENT_STATUS_CONNECTION_CONNECTING,
      this.hostName
    );
  }

  @Override
  public NPAgentStatusHealth health()
  {
    return NPAgentStatusHealth.UNHEALTHY;
  }
}
