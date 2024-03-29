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


package com.io7m.northpike.protocol.agent_console;

import com.io7m.northpike.agent.workexec.api.NPAWorkExecName;

import java.util.Objects;
import java.util.UUID;

/**
 * Get a supported work executor.
 *
 * @param messageID The ID of this message
 * @param name      The name
 */

public record NPACCommandWorkExecGet(
  UUID messageID,
  NPAWorkExecName name)
  implements NPACCommandType<NPACResponseWorkExecGet>
{
  /**
   * Get a supported work executor.
   *
   * @param messageID The ID of this message
   * @param name      The name
   */

  public NPACCommandWorkExecGet
  {
    Objects.requireNonNull(messageID, "messageID");
    Objects.requireNonNull(name, "name");
  }

  @Override
  public Class<NPACResponseWorkExecGet> responseClass()
  {
    return NPACResponseWorkExecGet.class;
  }
}
