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


package com.io7m.northpike.server.internal.users;

import com.io7m.northpike.database.api.NPAssignmentsPagedType;
import com.io7m.northpike.model.NPException;
import com.io7m.northpike.model.security.NPSecAction;
import com.io7m.northpike.model.security.NPSecObject;
import com.io7m.northpike.protocol.user.NPUCommandAssignmentSearchNext;
import com.io7m.northpike.protocol.user.NPUResponseAssignmentSearch;
import com.io7m.northpike.server.internal.security.NPSecurity;

import java.util.UUID;

import static com.io7m.northpike.model.NPStandardErrorCodes.errorApiMisuse;
import static com.io7m.northpike.strings.NPStringConstants.ERROR_SEARCH_NOT_STARTED;

/**
 * @see NPUCommandAssignmentSearchNext
 */

public final class NPUCmdAssignmentSearchNext
  implements NPUserCommandExecutorType<NPUResponseAssignmentSearch, NPUCommandAssignmentSearchNext>
{
  /**
   * @see NPUCommandAssignmentSearchNext
   */

  public NPUCmdAssignmentSearchNext()
  {

  }

  @Override
  public NPUResponseAssignmentSearch execute(
    final NPUserCommandContextType context,
    final NPUCommandAssignmentSearchNext command)
    throws NPException
  {
    final var user = context.onAuthenticationRequire();
    NPSecurity.check(
      user.userId(),
      user.subject(),
      NPSecObject.ASSIGNMENTS.object(),
      NPSecAction.READ.action()
    );

    try (var connection = context.databaseConnection()) {
      try (var transaction = connection.openTransaction()) {
        final var paged =
          context.property(NPAssignmentsPagedType.class)
            .orElseThrow(() -> {
              return context.fail(ERROR_SEARCH_NOT_STARTED, errorApiMisuse());
            });

        return new NPUResponseAssignmentSearch(
          UUID.randomUUID(),
          command.messageID(),
          paged.pageNext(transaction)
        );
      }
    }
  }
}