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

package com.io7m.northpike.agent.api;

import com.io7m.jmulticlose.core.CloseableType;

/**
 * An agent host.
 */

public interface NPAgentHostType extends CloseableType
{
  /**
   * Start the agent host. The method returns when the agent host is fully started up.
   *
   * @throws NPAgentException     On errors
   * @throws InterruptedException On interruption
   */

  void start()
    throws NPAgentException, InterruptedException;

  /**
   * Stop the agent host.
   *
   * @throws NPAgentException     On errors
   * @throws InterruptedException On interruption
   */

  void stop()
    throws NPAgentException, InterruptedException;

  @Override
  void close()
    throws NPAgentException;
}
