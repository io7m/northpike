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

import com.io7m.northpike.model.agents.NPAgentID;

import java.util.Objects;
import java.util.UUID;

/**
 * A user or an agent ID.
 */

public sealed interface NPAuditOwnerType
{
  /**
   * The owner of the operation is the server itself.
   */

  record Server()
    implements NPAuditOwnerType
  {

  }

  /**
   * The owner of the operation is a user.
   *
   * @param id The ID
   */

  record User(UUID id)
    implements NPAuditOwnerType
  {
    /**
     * The owner of the operation is a user.
     */

    public User
    {
      Objects.requireNonNull(id, "id");
    }
  }

  /**
   * The owner of the operation is an agent.
   *
   * @param id The ID
   */

  record Agent(NPAgentID id)
    implements NPAuditOwnerType
  {
    /**
     * The owner of the operation is an agent.
     */

    public Agent
    {
      Objects.requireNonNull(id, "id");
    }
  }
}
