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

import com.io7m.cedarbridge.runtime.api.CBString;
import com.io7m.northpike.model.agents.NPAgentLabelSearchParameters;
import com.io7m.northpike.protocol.api.NPProtocolException;
import com.io7m.northpike.protocol.api.NPProtocolMessageValidatorType;
import com.io7m.northpike.protocol.user.cb.NPU1AgentLabelSearchParameters;

import static com.io7m.cedarbridge.runtime.api.CBCore.unsigned32;
import static com.io7m.northpike.protocol.user.cb.internal.NPUVStrings.STRINGS;

/**
 * A validator.
 */

public enum NPUVAgentLabelSearchParameters
  implements NPProtocolMessageValidatorType<
  NPAgentLabelSearchParameters, NPU1AgentLabelSearchParameters>
{
  /**
   * A validator.
   */

  AGENT_LABEL_SEARCH_PARAMETERS;

  private static final NPUVComparisonsFuzzy<String, CBString> FUZZY_VALIDATOR =
    new NPUVComparisonsFuzzy<>(STRINGS);

  @Override
  public NPU1AgentLabelSearchParameters convertToWire(
    final NPAgentLabelSearchParameters message)
    throws NPProtocolException
  {
    return new NPU1AgentLabelSearchParameters(
      FUZZY_VALIDATOR.convertToWire(message.name()),
      FUZZY_VALIDATOR.convertToWire(message.description()),
      unsigned32(message.pageSize())
    );
  }

  @Override
  public NPAgentLabelSearchParameters convertFromWire(
    final NPU1AgentLabelSearchParameters message)
    throws NPProtocolException
  {
    return new NPAgentLabelSearchParameters(
      FUZZY_VALIDATOR.convertFromWire(message.fieldName()),
      FUZZY_VALIDATOR.convertFromWire(message.fieldDescription()),
      message.fieldPageSize().value()
    );
  }
}
