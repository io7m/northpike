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

import com.io7m.cedarbridge.runtime.convenience.CBMaps;
import com.io7m.cedarbridge.runtime.time.CBOffsetDateTime;
import com.io7m.northpike.model.NPAuditEvent;
import com.io7m.northpike.protocol.api.NPProtocolMessageValidatorType;
import com.io7m.northpike.protocol.user.cb.NPU1AuditEvent;

import static com.io7m.cedarbridge.runtime.api.CBCore.string;
import static com.io7m.cedarbridge.runtime.api.CBCore.unsigned64;
import static com.io7m.northpike.protocol.user.cb.internal.NPUVAuditOwner.AUDIT_OWNER;

/**
 * A validator.
 */

public enum NPUVAuditEvent
  implements NPProtocolMessageValidatorType<NPAuditEvent, NPU1AuditEvent>
{
  /**
   * A validator.
   */

  AUDIT_EVENT;

  @Override
  public NPU1AuditEvent convertToWire(
    final NPAuditEvent message)
  {
    return new NPU1AuditEvent(
      unsigned64(message.id()),
      new CBOffsetDateTime(message.time()),
      AUDIT_OWNER.convertToWire(message.owner()),
      string(message.type()),
      CBMaps.ofMapString(message.data())
    );
  }

  @Override
  public NPAuditEvent convertFromWire(
    final NPU1AuditEvent message)
  {
    return new NPAuditEvent(
      message.fieldId().value(),
      message.fieldTime().value(),
      AUDIT_OWNER.convertFromWire(message.fieldOwner()),
      message.fieldType().value(),
      CBMaps.toMapString(message.fieldData())
    );
  }
}
