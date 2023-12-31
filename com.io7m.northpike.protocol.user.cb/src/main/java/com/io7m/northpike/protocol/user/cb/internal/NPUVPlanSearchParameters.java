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
import com.io7m.northpike.model.NPToolExecutionIdentifier;
import com.io7m.northpike.model.plans.NPPlanSearchParameters;
import com.io7m.northpike.protocol.api.NPProtocolException;
import com.io7m.northpike.protocol.api.NPProtocolMessageValidatorType;
import com.io7m.northpike.protocol.user.cb.NPU1PlanSearchParameters;
import com.io7m.northpike.protocol.user.cb.NPU1ToolExecutionIdentifier;

import static com.io7m.cedarbridge.runtime.api.CBCore.unsigned32;
import static com.io7m.northpike.protocol.user.cb.internal.NPUVStrings.STRINGS;
import static com.io7m.northpike.protocol.user.cb.internal.NPUVToolExecutionIdentifier.TOOL_EXECUTION_IDENTIFIER;

/**
 * A validator.
 */

public enum NPUVPlanSearchParameters
  implements NPProtocolMessageValidatorType<
  NPPlanSearchParameters,
  NPU1PlanSearchParameters>
{
  /**
   * A validator.
   */

  PLAN_SEARCH_PARAMETERS;

  private static final NPUVComparisonsFuzzy<String, CBString> FUZZY_VALIDATOR =
    new NPUVComparisonsFuzzy<>(STRINGS);
  private static final NPUVComparisonsSet<NPToolExecutionIdentifier, NPU1ToolExecutionIdentifier> ID_VALIDATOR =
    new NPUVComparisonsSet<>(TOOL_EXECUTION_IDENTIFIER);

  @Override
  public NPU1PlanSearchParameters convertToWire(
    final NPPlanSearchParameters message)
    throws NPProtocolException
  {
    return new NPU1PlanSearchParameters(
      FUZZY_VALIDATOR.convertToWire(message.name()),
      FUZZY_VALIDATOR.convertToWire(message.description()),
      ID_VALIDATOR.convertToWire(message.toolExecutions()),
      unsigned32(message.pageSize())
    );
  }

  @Override
  public NPPlanSearchParameters convertFromWire(
    final NPU1PlanSearchParameters message)
    throws NPProtocolException
  {
    return new NPPlanSearchParameters(
      FUZZY_VALIDATOR.convertFromWire(message.fieldName()),
      FUZZY_VALIDATOR.convertFromWire(message.fieldDescription()),
      ID_VALIDATOR.convertFromWire(message.fieldToolExecutions()),
      message.fieldPageSize().value()
    );
  }
}
