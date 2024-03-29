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


import com.io7m.northpike.model.NPAuditOwnerType;
import com.io7m.northpike.model.NPAuditSearchParameters;
import com.io7m.northpike.model.NPTimeRange;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Combinators;

public final class NPArbAuditSearchParameters
  extends NPArbAbstract<NPAuditSearchParameters>
{
  public NPArbAuditSearchParameters()
  {
    super(
      NPAuditSearchParameters.class,
      () -> {
        return Combinators.combine(
          Arbitraries.defaultFor(NPAuditOwnerType.class).optional(),
          NPArbComparisons.exact(Arbitraries.strings().alpha()),
          Arbitraries.defaultFor(NPTimeRange.class),
          Arbitraries.longs()
        ).as(NPAuditSearchParameters::new);
      }
    );
  }
}
