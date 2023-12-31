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


package com.io7m.northpike.protocol.agent;

import com.io7m.northpike.model.agents.NPAgentWorkItem;

import java.util.Objects;
import java.util.UUID;

/**
 * The server is sending a work item to the client.
 *
 * @param messageID The message ID
 * @param workItem  The work item
 */

public record NPACommandSWorkSent(
  UUID messageID,
  NPAgentWorkItem workItem)
  implements NPACommandS2CType<NPAResponseWorkSent>
{
  /**
   * The server is sending a work item to the client.
   *
   * @param messageID The message ID
   * @param workItem  The work item
   */

  public NPACommandSWorkSent
  {
    Objects.requireNonNull(messageID, "messageID");
    Objects.requireNonNull(workItem, "workItem");
  }

  @Override
  public Class<NPAResponseWorkSent> responseClass()
  {
    return NPAResponseWorkSent.class;
  }
}
