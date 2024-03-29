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


package com.io7m.northpike.shell.commons;


import com.io7m.lanark.core.RDottedNamePatterns;
import com.io7m.northpike.agent.workexec.api.NPAWorkExecName;
import com.io7m.quarrel.core.QValueConverterType;

/**
 * @see NPAWorkExecName
 */

public final class NPAWorkExecNameConverter
  implements QValueConverterType<NPAWorkExecName>
{
  /**
   * @see NPAWorkExecName
   */

  public NPAWorkExecNameConverter()
  {

  }

  @Override
  public NPAWorkExecName convertFromString(
    final String text)
  {
    return NPAWorkExecName.of(text);
  }

  @Override
  public String convertToString(
    final NPAWorkExecName value)
  {
    return value.toString();
  }

  @Override
  public NPAWorkExecName exampleValue()
  {
    return NPAWorkExecName.of("com.io7m.example");
  }

  @Override
  public String syntax()
  {
    return RDottedNamePatterns.dottedName().pattern();
  }

  @Override
  public Class<NPAWorkExecName> convertedClass()
  {
    return NPAWorkExecName.class;
  }
}
