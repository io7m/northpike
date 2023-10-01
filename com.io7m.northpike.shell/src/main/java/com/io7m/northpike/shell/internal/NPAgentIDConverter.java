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


package com.io7m.northpike.shell.internal;

import com.io7m.northpike.model.NPAgentID;
import com.io7m.quarrel.core.QValueConverterType;

import java.util.UUID;

/**
 * @see NPAgentID
 */

public final class NPAgentIDConverter
  implements QValueConverterType<NPAgentID>
{
  static final String UUID_SYNTAX =
    "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

  /**
   * @see NPAgentID
   */

  public NPAgentIDConverter()
  {

  }

  @Override
  public NPAgentID convertFromString(
    final String text)
  {
    return new NPAgentID(UUID.fromString(text));
  }

  @Override
  public String convertToString(
    final NPAgentID value)
  {
    return value.toString();
  }

  @Override
  public NPAgentID exampleValue()
  {
    return new NPAgentID(UUID.randomUUID());
  }

  @Override
  public String syntax()
  {
    return UUID_SYNTAX;
  }

  @Override
  public Class<NPAgentID> convertedClass()
  {
    return NPAgentID.class;
  }
}
