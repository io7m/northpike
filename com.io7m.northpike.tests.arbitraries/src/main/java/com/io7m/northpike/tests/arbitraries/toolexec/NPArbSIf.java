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


package com.io7m.northpike.tests.arbitraries.toolexec;

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.northpike.tests.arbitraries.NPArbAbstract;
import com.io7m.northpike.toolexec.model.NPTXExpressionType;
import com.io7m.northpike.toolexec.model.NPTXSEnvironmentClear;
import com.io7m.northpike.toolexec.model.NPTXSEnvironmentPass;
import com.io7m.northpike.toolexec.model.NPTXSIf;
import com.io7m.northpike.toolexec.model.NPTXStatementType;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Combinators;

public final class NPArbSIf
  extends NPArbAbstract<NPTXSIf>
{
  public NPArbSIf()
  {
    super(
      NPTXSIf.class,
      () -> {
        return Combinators.combine(
            Arbitraries.defaultFor(LexicalPosition.class),
            Arbitraries.defaultFor(NPTXExpressionType.class),
            Arbitraries.defaultFor(NPTXSEnvironmentClear.class)
              .map(NPTXStatementType.class::cast)
              .list()
              .ofMaxSize(8),
            Arbitraries.defaultFor(NPTXSEnvironmentPass.class)
              .map(NPTXStatementType.class::cast)
              .list()
              .ofMaxSize(8)
          ).as(NPTXSIf::new);
      }
    );
  }
}
