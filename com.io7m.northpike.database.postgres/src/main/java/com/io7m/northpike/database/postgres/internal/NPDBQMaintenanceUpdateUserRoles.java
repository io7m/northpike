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

package com.io7m.northpike.database.postgres.internal;

import com.io7m.lanark.core.RDottedName;
import com.io7m.medrina.api.MRoleName;
import com.io7m.northpike.database.api.NPDatabaseQueriesMaintenanceType.UpdateUserRolesType;
import com.io7m.northpike.database.api.NPDatabaseUnit;
import com.io7m.northpike.database.postgres.internal.NPDBQueryProviderType.Service;
import com.io7m.northpike.model.security.NPSecRole;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.io7m.northpike.database.api.NPDatabaseUnit.UNIT;
import static com.io7m.northpike.database.postgres.internal.Tables.USERS;

/**
 * A query to run maintenance.
 */

public final class NPDBQMaintenanceUpdateUserRoles
  extends NPDBQAbstract<NPDatabaseUnit, NPDatabaseUnit>
  implements UpdateUserRolesType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(NPDBQMaintenanceUpdateUserRoles.class);

  private static final Service<NPDatabaseUnit, NPDatabaseUnit, UpdateUserRolesType> SERVICE =
    new Service<>(
      UpdateUserRolesType.class,
      NPDBQMaintenanceUpdateUserRoles::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public NPDBQMaintenanceUpdateUserRoles(
    final NPDatabaseTransaction transaction)
  {
    super(transaction);
  }

  /**
   * @return A query provider
   */

  public static NPDBQueryProviderType provider()
  {
    return () -> SERVICE;
  }

  @Override
  protected NPDatabaseUnit onExecute(
    final DSLContext context,
    final NPDatabaseUnit parameters)
  {
    final var roleNamesWithLogin =
      NPSecRole.allRoles()
        .stream()
        .map(NPSecRole::role)
        .map(MRoleName::value)
        .map(RDottedName::value)
        .toList();

    final var rolesArrayWithLogin =
      new String[roleNamesWithLogin.size()];
    roleNamesWithLogin.toArray(rolesArrayWithLogin);

    final var adminRoleName =
      NPSecRole.ADMINISTRATOR.role().value().value();
    final var adminRoleNameA =
      new String[]{adminRoleName};

    final var loginRoleName =
      NPSecRole.LOGIN.role().value().value();
    final var loginRoleNameA =
      new String[]{loginRoleName};

    final var updated =
      context.update(USERS)
        .set(USERS.U_ROLES, rolesArrayWithLogin)
        .where(USERS.U_ROLES.contains(adminRoleNameA)
                 .and(USERS.U_ROLES.contains(loginRoleNameA)))
        .execute();

    LOG.debug(
      "Updated {} users with admin roles.",
      Integer.valueOf(updated)
    );
    return UNIT;
  }

}
