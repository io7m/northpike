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

/**
 * The type of commands.
 *
 * @param <R> The type of responses
 */

public sealed interface NPACCommandType<R extends NPACResponseType>
  extends NPACMessageType
  permits NPACCommandAgentCreate,
  NPACCommandAgentDelete,
  NPACCommandAgentGet,
  NPACCommandAgentList,
  NPACCommandAgentServerAssign,
  NPACCommandAgentWorkExecPut,
  NPACCommandDisconnect,
  NPACCommandLogin,
  NPACCommandServerDelete,
  NPACCommandServerGet,
  NPACCommandServerList,
  NPACCommandServerPut,
  NPACCommandWorkExecGet,
  NPACCommandWorkExecSupported
{
  /**
   * @return The response class
   */

  Class<R> responseClass();
}
