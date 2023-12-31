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


package com.io7m.northpike.server.internal.agents;

import com.io7m.northpike.model.NPException;
import com.io7m.northpike.model.agents.NPAgentDescription;
import com.io7m.northpike.protocol.agent.NPACommandCEnvironmentInfo;
import com.io7m.northpike.protocol.agent.NPAResponseOK;

import static com.io7m.northpike.model.NPStandardErrorCodes.errorNonexistent;
import static com.io7m.northpike.strings.NPStringConstants.ERROR_NONEXISTENT;

/**
 * @see NPACommandCEnvironmentInfo
 */

public final class NPACmdEnvironmentInfo
  extends NPACmdAbstract<NPAResponseOK, NPACommandCEnvironmentInfo>
{
  /**
   * @see NPACommandCEnvironmentInfo
   */

  public NPACmdEnvironmentInfo()
  {
    super(NPACommandCEnvironmentInfo.class);
  }

  @Override
  public NPAResponseOK execute(
    final NPAgentCommandContextType context,
    final NPACommandCEnvironmentInfo command)
    throws NPException
  {
    final var agentId =
      context.onAuthenticationRequire();
    final var agentOpt =
      context.agentFindForId(agentId);

    if (agentOpt.isEmpty()) {
      throw context.fail(ERROR_NONEXISTENT, errorNonexistent());
    }

    final var agentExisting =
      agentOpt.get();

    final var agentNew =
      new NPAgentDescription(
        agentExisting.id(),
        agentExisting.name(),
        agentExisting.publicKey(),
        command.environmentVariables(),
        command.systemProperties(),
        agentExisting.labels()
      );

    context.agentUpdate(agentNew);
    return NPAResponseOK.createCorrelated(command);
  }
}
