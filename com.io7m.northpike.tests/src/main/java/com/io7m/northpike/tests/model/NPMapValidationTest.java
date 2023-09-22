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


package com.io7m.northpike.tests.model;

import com.io7m.lanark.core.RDottedName;
import com.io7m.northpike.model.NPAgentLabel;
import com.io7m.northpike.model.NPMapValidation;
import com.io7m.northpike.model.NPValidityException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class NPMapValidationTest
{
  /**
   * The label must match the key.
   */

  @Test
  public void testInvalid()
  {
    final var label =
      new NPAgentLabel(
        new RDottedName("x.y"),
        "X Y"
      );

    final var m =
      Map.of(
        new RDottedName("a.b"),
        label
      );

    assertThrows(NPValidityException.class, () -> {
      NPMapValidation.check(m, NPAgentLabel::name);
    });
  }
}