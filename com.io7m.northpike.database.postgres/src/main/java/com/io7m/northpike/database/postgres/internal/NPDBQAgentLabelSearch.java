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
import com.io7m.jqpage.core.JQSelectDistinct;
import com.io7m.northpike.database.api.NPAgentLabelsPagedType;
import com.io7m.northpike.database.api.NPDatabaseException;
import com.io7m.northpike.database.api.NPDatabaseQueriesAgentsType.AgentLabelSearchType;
import com.io7m.northpike.database.postgres.internal.NPDBQueryProviderType.Service;
import com.io7m.northpike.model.NPPage;
import com.io7m.northpike.model.agents.NPAgentLabel;
import com.io7m.northpike.model.agents.NPAgentLabelName;
import com.io7m.northpike.model.agents.NPAgentLabelSearchParameters;
import io.opentelemetry.api.trace.Span;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import java.util.List;

import static com.io7m.jqpage.core.JQOrder.ASCENDING;
import static com.io7m.northpike.database.postgres.internal.NPDatabaseExceptions.handleDatabaseException;
import static com.io7m.northpike.database.postgres.internal.Tables.AGENT_LABEL_DEFINITIONS;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.DB_STATEMENT;

/**
 * List labels.
 */

public final class NPDBQAgentLabelSearch
  extends NPDBQAbstract<NPAgentLabelSearchParameters, NPAgentLabelsPagedType>
  implements AgentLabelSearchType
{
  private static final Service<
    NPAgentLabelSearchParameters,
    NPAgentLabelsPagedType,
    AgentLabelSearchType> SERVICE =
    new Service<>(AgentLabelSearchType.class, NPDBQAgentLabelSearch::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public NPDBQAgentLabelSearch(
    final NPDatabaseTransaction transaction)
  {
    super(transaction);
  }

  @Override
  protected NPAgentLabelsPagedType onExecute(
    final DSLContext context,
    final NPAgentLabelSearchParameters parameters)
    throws NPDatabaseException
  {
    final var nameCondition =
      NPDBComparisons.createFuzzyMatchQuery(
        parameters.name(),
        AGENT_LABEL_DEFINITIONS.ALD_NAME,
        "AGENT_LABEL_DEFINITIONS.ALD_NAME_SEARCH"
      );

    final var descCondition =
      NPDBComparisons.createFuzzyMatchQuery(
        parameters.description(),
        AGENT_LABEL_DEFINITIONS.ALD_DESCRIPTION,
        "AGENT_LABEL_DEFINITIONS.ALD_DESCRIPTION_SEARCH"
      );

    final var allConditions =
      DSL.and(nameCondition, descCondition);

    final var sortField =
      new JQField(AGENT_LABEL_DEFINITIONS.ALD_NAME, ASCENDING);

    final var pageParameters =
      JQKeysetRandomAccessPaginationParameters.forTable(AGENT_LABEL_DEFINITIONS)
        .addWhereCondition(allConditions)
        .addSortField(sortField)
        .setDistinct(JQSelectDistinct.SELECT_DISTINCT)
        .setPageSize(parameters.pageSize())
        .setStatementListener(statement -> {
          Span.current().setAttribute(DB_STATEMENT, statement.toString());
        }).build();

    final var pages =
      JQKeysetRandomAccessPagination.createPageDefinitions(
        context, pageParameters);

    return new NPAgentLabelSearch(pages);
  }

  /**
   * @return A query provider
   */

  public static NPDBQueryProviderType provider()
  {
    return () -> SERVICE;
  }

  static final class NPAgentLabelSearch
    extends NPAbstractSearch<NPAgentLabel>
    implements NPAgentLabelsPagedType
  {
    NPAgentLabelSearch(
      final List<JQKeysetRandomAccessPageDefinition> pages)
    {
      super(pages);
    }

    @Override
    protected NPPage<NPAgentLabel> page(
      final NPDatabaseTransaction transaction,
      final JQKeysetRandomAccessPageDefinition page)
      throws NPDatabaseException
    {
      final var context =
        transaction.createContext();
      final var querySpan =
        transaction.createQuerySpan(
          "NPAgentLabelSearch.page");

      try {
        final var query =
          page.queryFields(context, List.of(
            AGENT_LABEL_DEFINITIONS.ALD_NAME,
            AGENT_LABEL_DEFINITIONS.ALD_DESCRIPTION
          ));

        querySpan.setAttribute(DB_STATEMENT, query.toString());

        final var items =
          query.fetch().map(record -> {
            return new NPAgentLabel(
              NPAgentLabelName.of(record.get(AGENT_LABEL_DEFINITIONS.ALD_NAME)),
              record.get(AGENT_LABEL_DEFINITIONS.ALD_DESCRIPTION)
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
