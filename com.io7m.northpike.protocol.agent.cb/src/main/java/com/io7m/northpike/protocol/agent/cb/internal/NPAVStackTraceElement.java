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

package com.io7m.northpike.protocol.agent.cb.internal;

import com.io7m.northpike.model.NPStoredStackTraceElement;
import com.io7m.northpike.protocol.agent.cb.NPA1StackTraceElement;
import com.io7m.northpike.protocol.api.NPProtocolMessageValidatorType;

import static com.io7m.cedarbridge.runtime.api.CBCore.string;
import static com.io7m.cedarbridge.runtime.api.CBCore.unsigned32;

/**
 * A validator.
 */

public enum NPAVStackTraceElement
  implements NPProtocolMessageValidatorType<NPStoredStackTraceElement, NPA1StackTraceElement>
{
  /**
   * A validator.
   */

  STACK_TRACE_ELEMENT;

  @Override
  public NPA1StackTraceElement convertToWire(
    final NPStoredStackTraceElement message)
  {
    return new NPA1StackTraceElement(
      string(message.classLoader()),
      string(message.moduleName()),
      string(message.moduleVersion()),
      string(message.declaringClass()),
      string(message.methodName()),
      string(message.fileName()),
      unsigned32(message.lineNumber())
    );
  }

  @Override
  public NPStoredStackTraceElement convertFromWire(
    final NPA1StackTraceElement message)
  {
    return new NPStoredStackTraceElement(
      message.fieldClassLoader().value(),
      message.fieldModuleName().value(),
      message.fieldModuleVersion().value(),
      message.fieldDeclaringClass().value(),
      message.fieldMethodName().value(),
      message.fieldFileName().value(),
      (int) message.fieldLineNumber().value()
    );
  }
}
