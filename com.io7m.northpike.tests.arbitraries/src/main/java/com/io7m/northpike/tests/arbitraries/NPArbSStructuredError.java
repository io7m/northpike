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


package com.io7m.northpike.tests.arbitraries;

import com.io7m.seltzer.api.SStructuredError;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Combinators;

import java.util.Optional;

public final class NPArbSStructuredError extends NPArbAbstract<SStructuredError>
{
  public NPArbSStructuredError()
  {
    super(
      SStructuredError.class,
      () -> Combinators.combine(
        Arbitraries.strings().alpha(),
        Arbitraries.strings().alpha(),
        Arbitraries.maps(
          Arbitraries.strings().alpha(),
          Arbitraries.strings().alpha()
        ),
        Arbitraries.strings().alpha().optional()
      ).as((s0, s1, s2, s3) -> {
        return new SStructuredError<>(s0, s1, s2, s3, Optional.empty());
      })
    );
  }
}
