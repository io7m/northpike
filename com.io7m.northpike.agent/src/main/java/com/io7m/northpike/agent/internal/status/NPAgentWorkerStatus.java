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

import static com.io7m.northpike.model.agents.NPAgentStatusHealth.UNHEALTHY;

/**
 * The status of an agent.
 *
 * @param connection   The connection status
 * @param workExecutor The work executor status
 */

public record NPAgentWorkerStatus(
  NPAgentConnectionStatusType connection,
  NPAgentWorkExecutorStatusType workExecutor)
{
  /**
   * The status of an agent.
   *
   * @param connection   The connection status
   * @param workExecutor The work executor status
   */

  public NPAgentWorkerStatus
  {
    Objects.requireNonNull(connection, "connection");
    Objects.requireNonNull(workExecutor, "workExecutor");
  }

  /**
   * Format this status as a string.
   *
   * @param strings The string resources
   *
   * @return The string
   */

  public String format(
    final NPStrings strings)
  {
    return "%s. %s.".formatted(
      this.connection.format(strings),
      this.workExecutor.format(strings)
    );
  }

  /**
   * @return The health
   */

  public NPAgentStatusHealth health()
  {
    return switch (this.connection.health()) {
      case HEALTHY -> this.workExecutor.health();
      case UNHEALTHY -> UNHEALTHY;
    };
  }
}
