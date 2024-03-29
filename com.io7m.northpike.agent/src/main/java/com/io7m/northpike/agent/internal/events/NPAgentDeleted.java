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
import com.io7m.northpike.model.agents.NPAgentLocalName;
import com.io7m.northpike.telemetry.api.NPEventSeverity;

import java.util.Map;
import java.util.Objects;

/**
 * An agent was deleted.
 *
 * @param agentName The agent name
 */

@NPDocumentation("An agent was deleted.")
public record NPAgentDeleted(
  NPAgentLocalName agentName)
  implements NPAgentEventType
{
  /**
   * An agent was deleted.
   *
   * @param agentName The agent name
   */

  public NPAgentDeleted
  {
    Objects.requireNonNull(agentName, "agentName");
  }

  @Override
  public NPEventSeverity severity()
  {
    return NPEventSeverity.INFO;
  }

  @Override
  public String name()
  {
    return "AgentDeleted";
  }

  @Override
  public String message()
  {
    return "Agent deleted";
  }

  @Override
  public Map<String, String> asAttributes()
  {
    return Map.ofEntries(
      Map.entry("AgentName", this.agentName.toString())
    );
  }
}
