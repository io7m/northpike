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


package com.io7m.northpike.protocol.user.cb.internal;

import com.io7m.northpike.model.NPToolExecutionIdentifier;
import com.io7m.northpike.model.NPToolExecutionName;
import com.io7m.northpike.protocol.api.NPProtocolMessageValidatorType;
import com.io7m.northpike.protocol.user.cb.NPU1ToolExecutionIdentifier;

import static com.io7m.cedarbridge.runtime.api.CBCore.string;
import static com.io7m.cedarbridge.runtime.api.CBCore.unsigned64;

/**
 * A validator.
 */

public enum NPUVToolExecutionIdentifier
  implements NPProtocolMessageValidatorType<NPToolExecutionIdentifier, NPU1ToolExecutionIdentifier>
{
  /**
   * A validator.
   */

  TOOL_EXECUTION_IDENTIFIER;

  @Override
  public NPU1ToolExecutionIdentifier convertToWire(
    final NPToolExecutionIdentifier message)
  {
    return new NPU1ToolExecutionIdentifier(
      string(message.name().toString()),
      unsigned64(message.version())
    );
  }

  @Override
  public NPToolExecutionIdentifier convertFromWire(
    final NPU1ToolExecutionIdentifier message)
  {
    return new NPToolExecutionIdentifier(
      NPToolExecutionName.of(message.fieldName().value()),
      message.fieldVersion().value()
    );
  }
}
