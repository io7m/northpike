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


package com.io7m.northpike.model.assignments;

import com.io7m.northpike.model.NPPageSizes;
import com.io7m.northpike.model.NPRepositoryID;
import com.io7m.northpike.model.NPSearchParametersType;
import com.io7m.northpike.model.comparisons.NPComparisonExactType;
import com.io7m.northpike.model.comparisons.NPComparisonFuzzyType;
import com.io7m.northpike.model.plans.NPPlanIdentifier;

import java.util.Objects;

/**
 * The parameters required to list assignments.
 *
 * @param repositoryId Filter assignments using the given repository
 * @param plan         Filter assignments using the given plan
 * @param pageSize     The page size
 * @param nameQuery    The query used to search assignment names
 */

public record NPAssignmentSearchParameters(
  NPComparisonExactType<NPRepositoryID> repositoryId,
  NPComparisonExactType<NPPlanIdentifier> plan,
  NPComparisonFuzzyType<String> nameQuery,
  long pageSize)
  implements NPSearchParametersType
{
  /**
   * The parameters required to list assignments.
   *
   * @param repositoryId Filter assignments using the given repository
   * @param plan         Filter assignments using the given plan
   * @param pageSize     The page size
   * @param nameQuery    The query used to search assignment names
   */

  public NPAssignmentSearchParameters
  {
    Objects.requireNonNull(repositoryId, "repositoryId");
    Objects.requireNonNull(plan, "plan");
    Objects.requireNonNull(nameQuery, "nameQuery");

    pageSize = NPPageSizes.clampPageSize(pageSize);
  }
}
