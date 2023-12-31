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


package com.io7m.northpike.connections;

import com.io7m.jmulticlose.core.CloseableType;
import com.io7m.northpike.model.NPException;

import java.io.IOException;
import java.util.Optional;

/**
 * The type of handlers for sending and receiving versioned messages.
 *
 * @param <M> The type of messages
 */

public interface NPMessageConnectionHandlerType<M>
  extends CloseableType
{
  /**
   * Receive a message, if one is available.
   *
   * @return The message
   *
   * @throws NPException On errors
   * @throws IOException On errors
   */

  Optional<M> receive()
    throws NPException, IOException;

  /**
   * Send a message.
   *
   * @param message The message
   *
   * @throws NPException On errors
   * @throws IOException On errors
   */

  void send(M message)
    throws NPException, IOException;

  @Override
  void close()
    throws IOException;
}
