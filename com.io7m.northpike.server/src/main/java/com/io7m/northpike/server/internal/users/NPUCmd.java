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


package com.io7m.northpike.server.internal.users;

import com.io7m.northpike.model.NPException;
import com.io7m.northpike.protocol.user.NPUCommandDisconnect;
import com.io7m.northpike.protocol.user.NPUCommandLogin;
import com.io7m.northpike.protocol.user.NPUCommandRepositoryGet;
import com.io7m.northpike.protocol.user.NPUCommandRepositoryPut;
import com.io7m.northpike.protocol.user.NPUCommandType;
import com.io7m.northpike.protocol.user.NPUMessageType;
import com.io7m.northpike.protocol.user.NPUResponseType;

import static com.io7m.northpike.model.NPStandardErrorCodes.errorProtocol;
import static com.io7m.northpike.strings.NPStringConstants.ERROR_PROTOCOL;

/**
 * @see NPUMessageType
 */

public final class NPUCmd
{
  /**
   * @see NPUMessageType
   */

  public NPUCmd()
  {

  }

  /**
   * Execute a message.
   *
   * @param context The command context
   * @param message The message
   *
   * @return The result of executing the message
   *
   * @throws NPException On errors
   */

  public NPUResponseType execute(
    final NPUserCommandContextType context,
    final NPUMessageType message)
    throws NPException
  {
    if (message instanceof final NPUCommandType<?> command) {
      if (command instanceof final NPUCommandLogin c) {
        return new NPUCmdLogin().execute(context, c);
      }
      if (command instanceof final NPUCommandDisconnect c) {
        return new NPUCmdDisconnect().execute(context, c);
      }
      if (command instanceof final NPUCommandRepositoryPut c) {
        return new NPUCmdRepositoryPut().execute(context, c);
      }
      if (command instanceof final NPUCommandRepositoryGet c) {
        return new NPUCmdRepositoryGet().execute(context, c);
      }
    }
    throw context.fail(ERROR_PROTOCOL, errorProtocol());
  }
}
