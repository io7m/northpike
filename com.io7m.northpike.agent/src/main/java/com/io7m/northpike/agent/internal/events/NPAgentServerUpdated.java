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


package com.io7m.northpike.agent.internal.events;

import com.io7m.northpike.model.NPDocumentation;
import com.io7m.northpike.model.agents.NPAgentServerID;
import com.io7m.northpike.telemetry.api.NPEventSeverity;

import java.util.Map;
import java.util.Objects;

/**
 * A server was updated.
 *
 * @param serverID The server ID
 */

@NPDocumentation("A server definition was updated.")
public record NPAgentServerUpdated(
  NPAgentServerID serverID)
  implements NPAgentEventType
{
  /**
   * A server was updated.
   *
   * @param serverID The server ID
   */

  public NPAgentServerUpdated
  {
    Objects.requireNonNull(serverID, "serverID");
  }

  @Override
  public NPEventSeverity severity()
  {
    return NPEventSeverity.INFO;
  }

  @Override
  public String name()
  {
    return "ServerUpdated";
  }

  @Override
  public String message()
  {
    return "Server updated";
  }

  @Override
  public Map<String, String> asAttributes()
  {
    return Map.ofEntries(
      Map.entry("ServerID", this.serverID.toString())
    );
  }
}
