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


package com.io7m.northpike.scm_repository.spi;

import com.io7m.northpike.strings.NPStringConstantType;

import java.util.Objects;
import java.util.UUID;

/**
 * A task completed successfully.
 *
 * @param taskId The task ID
 * @param name   The task name
 */

public record NPSCMTaskStarted(
  UUID taskId,
  NPStringConstantType name)
  implements NPSCMEventTaskType
{
  /**
   * A task completed successfully.
   *
   * @param taskId The task ID
   * @param name   The task name
   */

  public NPSCMTaskStarted
  {
    Objects.requireNonNull(taskId, "taskId");
    Objects.requireNonNull(name, "message");
  }
}
