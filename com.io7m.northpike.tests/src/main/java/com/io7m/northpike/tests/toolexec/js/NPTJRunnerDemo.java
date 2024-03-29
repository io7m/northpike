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


package com.io7m.northpike.tests.toolexec.js;

import com.io7m.northpike.toolexec.api.NPTException;
import com.io7m.northpike.toolexec.js.NPTJSLanguageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public final class NPTJRunnerDemo
{
  private static final Logger LOG =
    LoggerFactory.getLogger(NPTJRunnerDemo.class);

  private NPTJRunnerDemo()
  {

  }

  public static void main(
    final String[] args)
    throws IOException, NPTException
  {
    final var runners =
      new NPTJSLanguageProvider();

    final var runner =
      runners.create(
        List.of(),
        """
(x => x(x))(x => x(x))
          """
      );

    try {
      final var eval = runner.execute();
      LOG.debug("{}", eval);
    } catch (final NPTException ex) {
      LOG.error("", ex);
      for (final var e : ex.errors()) {
        LOG.error("{}", e);
      }
    }
  }
}
