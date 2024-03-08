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


package com.io7m.northpike.toolexec.api;

import com.io7m.northpike.model.NPFormatName;
import com.io7m.northpike.toolexec.program.api.NPTPVariableType;
import com.io7m.repetoir.core.RPServiceType;

import java.util.List;

/**
 * The evaluation service interface.
 */

public interface NPTEvaluationServiceType
  extends RPServiceType
{
  /**
   * Create a new evaluable object.
   *
   * @param formatName The format of the given program text
   * @param variables  The program variables
   * @param program    The program text
   *
   * @return A new evaluable object
   *
   * @throws NPTException On errors
   */

  NPTEvaluableType create(
    NPFormatName formatName,
    List<NPTPVariableType> variables,
    String program)
    throws NPTException;
}
