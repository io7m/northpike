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


package com.io7m.northpike.model;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * A commit summary.
 *
 * @param id             The commit ID
 * @param timeCreated    The commit creation time
 * @param timeReceived   The time the commit was received by this server
 * @param messageSubject The commit message
 */

public record NPCommitSummary(
  NPCommitID id,
  OffsetDateTime timeCreated,
  OffsetDateTime timeReceived,
  String messageSubject)
{
  /**
   * A commit summary.
   *
   * @param id             The commit ID
   * @param timeCreated    The commit creation time
   * @param timeReceived   The time the commit was received by this server
   * @param messageSubject The commit message
   */

  public NPCommitSummary
  {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(timeCreated, "timeCreated");
    Objects.requireNonNull(timeReceived, "timeReceived");
    Objects.requireNonNull(messageSubject, "messageSubject");
  }
}
