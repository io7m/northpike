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

package com.io7m.northpike.agent.shell.internal;

import com.io7m.northpike.agent.console_client.api.NPAConsoleClientType;
import com.io7m.northpike.model.agents.NPAgentServerID;
import com.io7m.northpike.protocol.agent_console.NPACCommandServerDelete;
import com.io7m.northpike.shell.commons.NPShellCmdAbstractConfirmationRequired;
import com.io7m.northpike.shell.commons.NPShellConfirmationRequest;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * "server-delete"
 */

public final class NPAShellCmdServerDelete
  extends NPShellCmdAbstractConfirmationRequired
{
  private static final QParameterNamed1<NPAgentServerID> ID =
    new QParameterNamed1<>(
      "--server",
      List.of(),
      new QConstant(
        "The server ID."),
      Optional.empty(),
      NPAgentServerID.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The services
   */

  public NPAShellCmdServerDelete(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "server-delete",
        new QConstant("Delete a server."),
        Optional.empty()
      )
    );
  }

  @Override
  protected Collection<? extends QParameterNamedType<?>> onListNamedParametersConfirmed()
  {
    return List.of(ID);
  }

  @Override
  protected NPShellConfirmationRequest onRequireConfirmation(
    final QCommandContextType context)
  {
    return new NPShellConfirmationRequest(
      "server-delete-confirm",
      () -> {
        final var services =
          this.services();
        final var client =
          services.requireService(NPAConsoleClientType.class);

        client.execute(new NPACCommandServerDelete(
          UUID.randomUUID(),
          context.parameterValue(ID)
        ));
      }
    );
  }
}
