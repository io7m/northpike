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
import com.io7m.northpike.protocol.api.NPProtocolMessageValidatorType;
import com.io7m.northpike.protocol.user.NPUCommandRepositoryPut;
import com.io7m.northpike.protocol.user.cb.NPU1CommandRepositoryPut;

import static com.io7m.northpike.protocol.user.cb.internal.NPUVRepositoryDescription.REPOSITORY_DESCRIPTION;

/**
 * A validator.
 */

public enum NPUVCommandRepositoryPut
  implements NPProtocolMessageValidatorType<NPUCommandRepositoryPut, NPU1CommandRepositoryPut>
{
  /**
   * A validator.
   */

  COMMAND_REPOSITORY_PUT;

  @Override
  public NPU1CommandRepositoryPut convertToWire(
    final NPUCommandRepositoryPut message)
  {
    return new NPU1CommandRepositoryPut(
      new CBUUID(message.messageID()),
      REPOSITORY_DESCRIPTION.convertToWire(message.repository())
    );
  }

  @Override
  public NPUCommandRepositoryPut convertFromWire(
    final NPU1CommandRepositoryPut message)
  {
    return new NPUCommandRepositoryPut(
      message.fieldMessageId().value(),
      REPOSITORY_DESCRIPTION.convertFromWire(message.fieldRepository())
    );
  }
}
