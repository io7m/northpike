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


package com.io7m.northpike.assignments;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;

/**
 * An assignment was created but not yet started.
 *
 * @param timeCreated The time created
 * @param execution   The assignment execution
 */

public record NPAssignmentExecutionStateCreated(
  OffsetDateTime timeCreated,
  NPAssignmentExecution execution)
  implements NPAssignmentExecutionStateCreatedType
{
  /**
   * An assignment was created but not yet started.
   *
   * @param timeCreated The time created
   * @param execution   The assignment
   */

  public NPAssignmentExecutionStateCreated
  {
    Objects.requireNonNull(execution, "execution");
    Objects.requireNonNull(timeCreated, "timeCreated");
  }


  @Override
  public NPAssignmentExecutionRequest request()
  {
    return this.execution.request();
  }

  @Override
  public String name()
  {
    return "Created";
  }

  @Override
  public Map<String, String> asAttributes()
  {
    final var assignment = this.execution.assignment();
    final var plan = assignment.plan();
    return Map.ofEntries(
      entry("Assignment", this.request().assignment().toString()),
      entry("AssignmentExecutionID", this.execution.id().toString()),
      entry("CommitID", this.request().commit().toString()),
      entry("PlanID", plan.name().toString()),
      entry("PlanVersion", Long.toUnsignedString(plan.version())),
      entry("Status", this.name()),
      entry("TimeCreated", this.timeCreated.toString())
    );
  }
}