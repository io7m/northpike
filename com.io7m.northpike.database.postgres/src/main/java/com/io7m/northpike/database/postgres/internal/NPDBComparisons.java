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

import com.io7m.northpike.model.comparisons.NPComparisonExactType;
import com.io7m.northpike.model.comparisons.NPComparisonFuzzyType;
import org.jooq.Condition;
import org.jooq.TableField;
import org.jooq.impl.DSL;

/**
 * Functions to perform comparisons over fields/columns.
 */

public final class NPDBComparisons
{
  private NPDBComparisons()
  {

  }

  /**
   * Create a fuzzy match expression.
   *
   * @param query       The query
   * @param fieldExact  The "exact" field
   * @param fieldSearch The search field
   * @param <T>         The type of compared values
   *
   * @return A fuzzy match condition
   */

  public static <T> Condition createFuzzyMatchQuery(
    final NPComparisonFuzzyType<T> query,
    final TableField<org.jooq.Record, T> fieldExact,
    final String fieldSearch)
  {
    if (query instanceof NPComparisonFuzzyType.Anything<T>) {
      return DSL.trueCondition();
    }

    if (query instanceof final NPComparisonFuzzyType.IsEqualTo<T> isEqualTo) {
      return fieldExact.equal(isEqualTo.value());
    }

    if (query instanceof final NPComparisonFuzzyType.IsNotEqualTo<T> isNotEqualTo) {
      return fieldExact.notEqual(isNotEqualTo.value());
    }

    if (query instanceof final NPComparisonFuzzyType.IsSimilarTo<T> isSimilarTo) {
      return DSL.condition(
        "%s @@ websearch_to_tsquery(?)".formatted(fieldSearch),
        isSimilarTo.value()
      );
    }

    if (query instanceof final NPComparisonFuzzyType.IsNotSimilarTo<T> isNotSimilarTo) {
      return DSL.condition(
        "NOT (%s @@ websearch_to_tsquery(?))".formatted(fieldSearch),
        isNotSimilarTo.value()
      );
    }

    throw new IllegalStateException(
      "Unrecognized name query: %s".formatted(query)
    );
  }

  /**
   * Create an exact match expression.
   *
   * @param query       The query
   * @param fieldExact  The "exact" field
   * @param <T>         The type of compared values
   *
   * @return An exact match condition
   */

  public static <T> Condition createExactMatchQuery(
    final NPComparisonExactType<T> query,
    final TableField<org.jooq.Record, T> fieldExact)
  {
    if (query instanceof NPComparisonExactType.Anything<T>) {
      return DSL.trueCondition();
    }

    if (query instanceof final NPComparisonExactType.IsEqualTo<T> isEqualTo) {
      return fieldExact.equal(isEqualTo.value());
    }

    if (query instanceof final NPComparisonExactType.IsNotEqualTo<T> isNotEqualTo) {
      return fieldExact.notEqual(isNotEqualTo.value());
    }

    throw new IllegalStateException(
      "Unrecognized name query: %s".formatted(query)
    );
  }
}