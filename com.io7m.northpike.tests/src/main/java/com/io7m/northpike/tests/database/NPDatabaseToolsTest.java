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

package com.io7m.northpike.tests.database;

import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.test_extension.ErvillaCloseAfterSuite;
import com.io7m.ervilla.test_extension.ErvillaConfiguration;
import com.io7m.ervilla.test_extension.ErvillaExtension;
import com.io7m.northpike.database.api.NPDatabaseConnectionType;
import com.io7m.northpike.database.api.NPDatabaseQueriesToolsType.GetExecutionDescriptionType;
import com.io7m.northpike.database.api.NPDatabaseQueriesToolsType.PutExecutionDescriptionType;
import com.io7m.northpike.database.api.NPDatabaseQueriesToolsType.SearchExecutionDescriptionType;
import com.io7m.northpike.database.api.NPDatabaseTransactionType;
import com.io7m.northpike.database.api.NPDatabaseType;
import com.io7m.northpike.model.NPToolExecutionDescription;
import com.io7m.northpike.model.NPToolExecutionDescriptionSearchParameters;
import com.io7m.northpike.model.NPToolExecutionDescriptionSummary;
import com.io7m.northpike.model.NPToolExecutionIdentifier;
import com.io7m.northpike.model.NPToolExecutionName;
import com.io7m.northpike.model.NPToolName;
import com.io7m.northpike.tests.containers.NPTestContainerInstances;
import com.io7m.northpike.tests.containers.NPTestContainers;
import com.io7m.northpike.toolexec.NPTXFormats;
import com.io7m.zelador.test_extension.CloseableResourcesType;
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.io7m.northpike.database.api.NPDatabaseRole.NORTHPIKE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(projectName = "com.io7m.northpike", disabledIfUnsupported = true)
public final class NPDatabaseToolsTest
{
  private static NPTestContainers.NPDatabaseFixture DATABASE_FIXTURE;
  private NPDatabaseConnectionType connection;
  private NPDatabaseTransactionType transaction;
  private NPDatabaseType database;

  @BeforeAll
  public static void setupOnce(
    final @ErvillaCloseAfterSuite EContainerSupervisorType containers)
    throws Exception
  {
    DATABASE_FIXTURE = NPTestContainerInstances.database(containers);
  }

  @BeforeEach
  public void setup(
    final CloseableResourcesType closeables)
    throws Exception
  {
    DATABASE_FIXTURE.reset();

    this.database =
      closeables.addPerTestResource(DATABASE_FIXTURE.createDatabase());
    this.connection =
      closeables.addPerTestResource(this.database.openConnection(NORTHPIKE));
    this.transaction =
      closeables.addPerTestResource(this.connection.openTransaction());

    this.transaction.setOwner(
      NPTestContainers.NPDatabaseFixture.createUser(
        this.transaction,
        UUID.randomUUID()
      )
    );
  }

  /**
   * Creating tool execution descriptions works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testToolExecCreate0()
    throws Exception
  {
    final var get =
      this.transaction.queries(GetExecutionDescriptionType.class);
    final var put =
      this.transaction.queries(PutExecutionDescriptionType.class);

    final var tool =
      new NPToolExecutionDescription(
        new NPToolExecutionIdentifier(
          NPToolExecutionName.of("com.io7m.example"),
          23L
        ),
        NPToolName.of("com.io7m.tool"),
        "A description.",
        NPTXFormats.nptx1(),
        "Data."
      );

    put.execute(tool);
    assertEquals(tool, get.execute(tool.identifier()).orElseThrow());
  }

  /**
   * Nonexistent tool execution descriptions are nonexistent.
   *
   * @throws Exception On errors
   */

  @Test
  public void testToolExecGet0()
    throws Exception
  {
    final var get =
      this.transaction.queries(GetExecutionDescriptionType.class);

    assertEquals(
      Optional.empty(),
      get.execute(new NPToolExecutionIdentifier(
        NPToolExecutionName.of("com.io7m.example"),
        23L
      ))
    );
  }

  /**
   * Searching for tool execution descriptions works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testToolExecSearch0()
    throws Exception
  {
    final var put =
      this.transaction.queries(PutExecutionDescriptionType.class);
    final var search =
      this.transaction.queries(SearchExecutionDescriptionType.class);

    for (int toolIndex = 0; toolIndex < 3; ++toolIndex) {
      for (long version = 1L; version <= 3L; ++version) {
        final var tool =
          new NPToolExecutionDescription(
            new NPToolExecutionIdentifier(
              NPToolExecutionName.of(
                "com.io7m.example_%d"
                  .formatted(Integer.valueOf(toolIndex))
              ),
              version
            ),
            NPToolName.of("com.io7m.tool_%d"
                            .formatted(Integer.valueOf(toolIndex))),
            "A description.",
            NPTXFormats.nptx1(),
            "Data."
          );
        put.execute(tool);
      }
    }

    final var paged =
      search.execute(new NPToolExecutionDescriptionSearchParameters(
        Optional.of(NPToolName.of("com.io7m.tool_2")), 1000L)
      );

    final var p = paged.pageCurrent(this.transaction);
    assertEquals(1, p.pageIndex());
    assertEquals(1, p.pageCount());
    assertEquals(0L, p.pageFirstOffset());
    assertEquals(
      List.of(
        new NPToolExecutionDescriptionSummary(
          new NPToolExecutionIdentifier(
            NPToolExecutionName.of("com.io7m.example_2"),
            1L
          ),
          NPToolName.of("com.io7m.tool_2"),
          "A description."
        ),
        new NPToolExecutionDescriptionSummary(
          new NPToolExecutionIdentifier(
            NPToolExecutionName.of("com.io7m.example_2"),
            2L
          ),
          NPToolName.of("com.io7m.tool_2"),
          "A description."
        ),
        new NPToolExecutionDescriptionSummary(
          new NPToolExecutionIdentifier(
            NPToolExecutionName.of("com.io7m.example_2"),
            3L
          ),
          NPToolName.of("com.io7m.tool_2"),
          "A description."
        )
      ),
      p.items()
    );
  }

  /**
   * Searching for tool execution descriptions works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testToolExecSearch1()
    throws Exception
  {
    final var put =
      this.transaction.queries(PutExecutionDescriptionType.class);
    final var search =
      this.transaction.queries(SearchExecutionDescriptionType.class);

    for (int toolIndex = 0; toolIndex < 3; ++toolIndex) {
      for (long version = 1L; version <= 3L; ++version) {
        final var tool =
          new NPToolExecutionDescription(
            new NPToolExecutionIdentifier(
              NPToolExecutionName.of(
                "com.io7m.example_%d"
                  .formatted(Integer.valueOf(toolIndex))
              ),
              version
            ),
            NPToolName.of("com.io7m.tool_%d"
                            .formatted(Integer.valueOf(toolIndex))),
            "A description.",
            NPTXFormats.nptx1(),
            "Data."
          );
        put.execute(tool);
      }
    }

    this.transaction.commit();

    final var paged =
      search.execute(new NPToolExecutionDescriptionSearchParameters(
        Optional.empty(), 1000L)
      );

    final var p = paged.pageCurrent(this.transaction);
    assertEquals(1, p.pageIndex());
    assertEquals(1, p.pageCount());
    assertEquals(0L, p.pageFirstOffset());
    assertEquals(
      List.of(
        new NPToolExecutionDescriptionSummary(
          new NPToolExecutionIdentifier(
            NPToolExecutionName.of("com.io7m.example_0"),
            1L
          ),
          NPToolName.of("com.io7m.tool_0"),
          "A description."
        ),
        new NPToolExecutionDescriptionSummary(
          new NPToolExecutionIdentifier(
            NPToolExecutionName.of("com.io7m.example_0"),
            2L
          ),
          NPToolName.of("com.io7m.tool_0"),
          "A description."
        ),
        new NPToolExecutionDescriptionSummary(
          new NPToolExecutionIdentifier(
            NPToolExecutionName.of("com.io7m.example_0"),
            3L
          ),
          NPToolName.of("com.io7m.tool_0"),
          "A description."
        ),
        new NPToolExecutionDescriptionSummary(
          new NPToolExecutionIdentifier(
            NPToolExecutionName.of("com.io7m.example_1"),
            1L
          ),
          NPToolName.of("com.io7m.tool_1"),
          "A description."
        ),
        new NPToolExecutionDescriptionSummary(
          new NPToolExecutionIdentifier(
            NPToolExecutionName.of("com.io7m.example_1"),
            2L
          ),
          NPToolName.of("com.io7m.tool_1"),
          "A description."
        ),
        new NPToolExecutionDescriptionSummary(
          new NPToolExecutionIdentifier(
            NPToolExecutionName.of("com.io7m.example_1"),
            3L
          ),
          NPToolName.of("com.io7m.tool_1"),
          "A description."
        ),
        new NPToolExecutionDescriptionSummary(
          new NPToolExecutionIdentifier(
            NPToolExecutionName.of("com.io7m.example_2"),
            1L
          ),
          NPToolName.of("com.io7m.tool_2"),
          "A description."
        ),
        new NPToolExecutionDescriptionSummary(
          new NPToolExecutionIdentifier(
            NPToolExecutionName.of("com.io7m.example_2"),
            2L
          ),
          NPToolName.of("com.io7m.tool_2"),
          "A description."
        ),
        new NPToolExecutionDescriptionSummary(
          new NPToolExecutionIdentifier(
            NPToolExecutionName.of("com.io7m.example_2"),
            3L
          ),
          NPToolName.of("com.io7m.tool_2"),
          "A description."
        )
      ),
      p.items()
    );
  }
}
