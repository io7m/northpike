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
import com.io7m.northpike.database.api.NPDatabaseQueriesAgentsType.AgentGetByKeyType;
import com.io7m.northpike.database.postgres.internal.NPDBQueryProviderType.Service;
import com.io7m.northpike.model.agents.NPAgentDescription;
import com.io7m.northpike.model.agents.NPAgentID;
import com.io7m.northpike.model.agents.NPAgentLabel;
import com.io7m.northpike.model.agents.NPAgentLabelName;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.io7m.northpike.database.postgres.internal.NPDBQAgentGet.A_ENVIRONMENT;
import static com.io7m.northpike.database.postgres.internal.NPDBQAgentGet.A_PROPERTIES;
import static com.io7m.northpike.database.postgres.internal.Tables.AGENT_LABELS;
import static com.io7m.northpike.database.postgres.internal.Tables.AGENT_LABEL_DEFINITIONS;
import static com.io7m.northpike.database.postgres.internal.tables.Agents.AGENTS;

/**
 * Retrieve an agent.
 */

public final class NPDBQAgentGetByKey
  extends NPDBQAbstract<AgentGetByKeyType.Parameters, Optional<NPAgentDescription>>
  implements AgentGetByKeyType
{
  private static final Service<AgentGetByKeyType.Parameters, Optional<NPAgentDescription>, AgentGetByKeyType> SERVICE =
    new Service<>(AgentGetByKeyType.class, NPDBQAgentGetByKey::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public NPDBQAgentGetByKey(
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
  protected Optional<NPAgentDescription> onExecute(
    final DSLContext context,
    final AgentGetByKeyType.Parameters parameters)
    throws NPDatabaseException
  {
    final Condition deletedCondition;
    if (parameters.includeDeleted()) {
      deletedCondition = DSL.trueCondition();
    } else {
      deletedCondition = AGENTS.A_DELETED.eq(Boolean.FALSE);
    }

    final var query =
      context.select(
          AGENTS.A_ID,
          AGENTS.A_NAME,
          A_ENVIRONMENT,
          A_PROPERTIES,
          AGENTS.A_PUBLIC_KEY_DATA,
          AGENTS.A_PUBLIC_KEY_ALGO,
          AGENT_LABEL_DEFINITIONS.ALD_NAME,
          AGENT_LABEL_DEFINITIONS.ALD_DESCRIPTION
        ).from(AGENTS)
        .leftOuterJoin(AGENT_LABELS)
        .on(AGENTS.A_ID.eq(AGENT_LABELS.AL_AGENT))
        .leftOuterJoin(AGENT_LABEL_DEFINITIONS)
        .on(AGENT_LABEL_DEFINITIONS.ALD_ID.eq(AGENT_LABELS.AL_LABEL))
        .where(
          AGENTS.A_PUBLIC_KEY_DATA.eq(parameters.agentKey().asText())
            .and(deletedCondition)
        );

    recordQuery(query);
    final var result = query.fetch();
    if (result.isEmpty()) {
      return Optional.empty();
    }

    String name = null;
    UUID id = null;

    final var environment =
      new HashMap<String, String>();
    final var properties =
      new HashMap<String, String>();
    final var labels =
      new HashMap<NPAgentLabelName, NPAgentLabel>();

    boolean first = true;
    for (final var record : result) {
      if (first) {
        id = record.get(AGENTS.A_ID);
        name = record.get(AGENTS.A_NAME);
        first = false;
      }

      environment.putAll(
        record.get(A_ENVIRONMENT).data());
      properties.putAll(
        record.get(A_PROPERTIES).data());
      final var lName =
        record.get(AGENT_LABEL_DEFINITIONS.ALD_NAME);
      final var lDesc =
        record.get(AGENT_LABEL_DEFINITIONS.ALD_DESCRIPTION);

      if (lName != null) {
        final var label = new NPAgentLabel(NPAgentLabelName.of(lName), lDesc);
        labels.put(label.name(), label);
      }
    }

    return Optional.of(
      new NPAgentDescription(
        new NPAgentID(id),
        name,
        parameters.agentKey(),
        Map.copyOf(environment),
        Map.copyOf(properties),
        Map.copyOf(labels)
      )
    );
  }
}
