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

import com.io7m.cedarbridge.runtime.api.CBUUID;
import com.io7m.northpike.protocol.api.NPProtocolException;
import com.io7m.northpike.protocol.api.NPProtocolMessageValidatorType;
import com.io7m.northpike.protocol.user.NPUCommandAuditSearchBegin;
import com.io7m.northpike.protocol.user.cb.NPU1CommandAuditSearchBegin;

import static com.io7m.northpike.protocol.user.cb.internal.NPUVAuditSearchParameters.AUDIT_SEARCH_PARAMETERS;

/**
 * A validator.
 */

public enum NPUVCommandAuditSearchBegin
  implements NPProtocolMessageValidatorType<
  NPUCommandAuditSearchBegin,
  NPU1CommandAuditSearchBegin>
{
  /**
   * A validator.
   */

  COMMAND_AUDIT_SEARCH_BEGIN;

  @Override
  public NPU1CommandAuditSearchBegin convertToWire(
    final NPUCommandAuditSearchBegin message)
    throws NPProtocolException
  {
    return new NPU1CommandAuditSearchBegin(
      new CBUUID(message.messageID()),
      AUDIT_SEARCH_PARAMETERS.convertToWire(message.parameters())
    );
  }

  @Override
  public NPUCommandAuditSearchBegin convertFromWire(
    final NPU1CommandAuditSearchBegin message)
    throws NPProtocolException
  {
    return new NPUCommandAuditSearchBegin(
      message.fieldMessageId().value(),
      AUDIT_SEARCH_PARAMETERS.convertFromWire(message.fieldParameters())
    );
  }
}
