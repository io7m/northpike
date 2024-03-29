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

import com.io7m.northpike.database.api.NPDatabaseException;
import com.io7m.northpike.database.api.NPDatabaseQueriesPlansType.PlanDeleteType;
import com.io7m.northpike.database.api.NPDatabaseUnit;
import com.io7m.northpike.model.plans.NPPlanIdentifier;
import org.jooq.DSLContext;

import static com.io7m.northpike.database.api.NPDatabaseUnit.UNIT;
import static com.io7m.northpike.database.postgres.internal.tables.Plans.PLANS;
import static com.io7m.northpike.strings.NPStringConstants.PLAN;
import static com.io7m.northpike.strings.NPStringConstants.PLAN_VERSION;
import static java.lang.Long.toUnsignedString;
import static java.util.Map.entry;

/**
 * Delete a plan.
 */

public final class NPDBQPlanDelete
  extends NPDBQAbstract<NPPlanIdentifier, NPDatabaseUnit>
  implements PlanDeleteType
{
  private static final NPDBQueryProviderType.Service<NPPlanIdentifier, NPDatabaseUnit, PlanDeleteType> SERVICE =
    new NPDBQueryProviderType.Service<>(PlanDeleteType.class, NPDBQPlanDelete::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public NPDBQPlanDelete(
    final NPDatabaseTransaction transaction)
  {
    super(transaction);
  }

  @Override
  protected NPDatabaseUnit onExecute(
    final DSLContext context,
    final NPPlanIdentifier identifier)
    throws NPDatabaseException
  {
    this.setAttribute(PLAN, identifier.name().toString());
    this.setAttribute(PLAN_VERSION, toUnsignedString(identifier.version()));

    context.deleteFrom(PLANS)
      .where(
        PLANS.P_NAME.eq(identifier.name().toString())
          .and(PLANS.P_VERSION.eq(Long.valueOf(identifier.version())))
      ).execute();

    this.auditEventPut(
      context,
      "PLAN_DELETE",
      entry("PLAN", identifier.name().toString()),
      entry("PLAN_VERSION", toUnsignedString(identifier.version()))
    );

    return UNIT;
  }

  /**
   * @return A query provider
   */

  public static NPDBQueryProviderType provider()
  {
    return () -> SERVICE;
  }

}
