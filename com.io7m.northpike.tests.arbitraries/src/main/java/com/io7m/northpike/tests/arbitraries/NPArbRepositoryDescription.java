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


package com.io7m.northpike.tests.arbitraries;

import com.io7m.lanark.core.RDottedName;
import com.io7m.northpike.model.NPRepositoryCredentialsType;
import com.io7m.northpike.model.NPRepositoryDescription;
import com.io7m.northpike.model.NPRepositoryID;
import com.io7m.northpike.model.NPRepositorySigningPolicy;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Combinators;

import java.net.URI;
import java.util.UUID;

public final class NPArbRepositoryDescription
  extends NPArbAbstract<NPRepositoryDescription>
{
  public NPArbRepositoryDescription()
  {
    super(
      NPRepositoryDescription.class,
      () -> {
        return Combinators.combine(
          Arbitraries.defaultFor(RDottedName.class),
          Arbitraries.defaultFor(NPRepositoryID.class),
          Arbitraries.create(UUID::randomUUID)
            .map(u -> "urn:uuid:" + u)
            .map(s -> URI.create(s)),
          Arbitraries.defaultFor(NPRepositoryCredentialsType.class),
          Arbitraries.defaultFor(NPRepositorySigningPolicy.class)
        ).as(NPRepositoryDescription::new);
      }
    );
  }
}
