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


import com.io7m.jqpage.core.JQField;
import com.io7m.jqpage.core.JQKeysetRandomAccessPageDefinition;
import com.io7m.jqpage.core.JQKeysetRandomAccessPagination;
import com.io7m.jqpage.core.JQKeysetRandomAccessPaginationParameters;
import com.io7m.jqpage.core.JQOrder;
import com.io7m.jqpage.core.JQSelectDistinct;
import com.io7m.northpike.database.api.NPDatabaseException;
import com.io7m.northpike.database.api.NPDatabaseQueriesPlansType;
import com.io7m.northpike.database.api.NPPlansPagedType;
import com.io7m.northpike.database.postgres.internal.NPDBQueryProviderType.Service;
import com.io7m.northpike.model.NPPage;
import com.io7m.northpike.plans.NPPlanIdentifier;
import com.io7m.northpike.plans.NPPlanName;
import com.io7m.northpike.plans.NPPlanSearchParameters;
import com.io7m.northpike.plans.NPPlanSummary;
import io.opentelemetry.api.trace.Span;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import java.util.List;

import static com.io7m.northpike.database.postgres.internal.NPDatabaseExceptions.handleDatabaseException;
import static com.io7m.northpike.database.postgres.internal.Tables.PLANS;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.DB_STATEMENT;

/**
 * Search for plans.
 */

public final class NPDBQPlanSearch
  extends NPDBQAbstract<NPPlanSearchParameters, NPPlansPagedType>
  implements NPDatabaseQueriesPlansType.SearchType
{
  private static final Service<NPPlanSearchParameters, NPPlansPagedType, SearchType> SERVICE =
    new Service<>(SearchType.class, NPDBQPlanSearch::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public NPDBQPlanSearch(
    final NPDatabaseTransaction transaction)
  {
    super(transaction);
  }

  @Override
  protected NPPlansPagedType onExecute(
    final DSLContext context,
    final NPPlanSearchParameters parameters)
    throws NPDatabaseException
  {
    final Condition condition;
    if (parameters.query().isEmpty()) {
      condition = DSL.trueCondition();
    } else {
      final var nameCondition =
        DSL.condition(
          "PLANS.P_NAME_SEARCH @@ websearch_to_tsquery(?)",
          DSL.inline(parameters.query())
        );

      final var descriptionCondition =
        DSL.condition(
          "PLANS.P_DESCRIPTION_SEARCH @@ websearch_to_tsquery(?)",
          DSL.inline(parameters.query())
        );

      condition = nameCondition.or(descriptionCondition);
    }
    
    final var pageParameters =
      JQKeysetRandomAccessPaginationParameters.forTable(PLANS)
        .addSortField(new JQField(PLANS.P_NAME, JQOrder.ASCENDING))
        .addSortField(new JQField(PLANS.P_VERSION, JQOrder.ASCENDING))
        .addWhereCondition(condition)
        .setDistinct(JQSelectDistinct.SELECT_DISTINCT)
        .setPageSize(100L)
        .setStatementListener(statement -> {
          Span.current().setAttribute(DB_STATEMENT, statement.toString());
        }).build();

    final var pages =
      JQKeysetRandomAccessPagination.createPageDefinitions(
        context, pageParameters);

    return new NPPlanList(pages);
  }

  /**
   * @return A query provider
   */

  public static NPDBQueryProviderType provider()
  {
    return () -> SERVICE;
  }

  static final class NPPlanList
    extends NPAbstractSearch<NPPlanSummary>
    implements NPPlansPagedType
  {
    NPPlanList(
      final List<JQKeysetRandomAccessPageDefinition> pages)
    {
      super(pages);
    }

    @Override
    protected NPPage<NPPlanSummary> page(
      final NPDatabaseTransaction transaction,
      final JQKeysetRandomAccessPageDefinition page)
      throws NPDatabaseException
    {
      final var context =
        transaction.createContext();
      final var querySpan =
        transaction.createQuerySpan(
          "NPPlanList.page");

      try {
        final var query =
          page.queryFields(context, List.of(
            PLANS.P_NAME,
            PLANS.P_VERSION,
            PLANS.P_DESCRIPTION
          ));

        querySpan.setAttribute(DB_STATEMENT, query.toString());

        final var items =
          query.fetch().map(record -> {
            return new NPPlanSummary(
              new NPPlanIdentifier(
                NPPlanName.of(record.get(PLANS.P_NAME)),
                record.get(PLANS.P_VERSION).longValue()
              ),
              record.get(PLANS.P_DESCRIPTION)
            );
          });

        return new NPPage<>(
          items,
          (int) page.index(),
          this.pageCount(),
          page.firstOffset()
        );
      } catch (final DataAccessException e) {
        querySpan.recordException(e);
        throw handleDatabaseException(transaction, e);
      } finally {
        querySpan.end();
      }
    }
  }
}