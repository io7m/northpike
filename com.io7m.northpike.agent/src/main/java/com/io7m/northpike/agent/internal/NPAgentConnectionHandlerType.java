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


package com.io7m.northpike.agent.internal;

import com.io7m.northpike.agent.api.NPAgentException;
import com.io7m.northpike.protocol.agent.NPAMessageType;

import java.util.Optional;

/**
 * The type of handlers for sending and receiving versioned messages.
 */

public interface NPAgentConnectionHandlerType
  extends AutoCloseable
{
  /**
   * Send a message.
   *
   * @param message The message
   *
   * @throws NPAgentException On errors
   */

  void send(NPAMessageType message)
    throws NPAgentException;

  /**
   * Check to see if a message is available.
   *
   * @return The message, if any
   *
   * @throws NPAgentException On errors
   */

  Optional<NPAMessageType> receive()
    throws NPAgentException;

  @Override
  void close()
    throws NPAgentException;
}