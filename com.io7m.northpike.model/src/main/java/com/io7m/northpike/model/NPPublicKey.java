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
import java.util.Optional;
import java.util.Set;

/**
 * A public key.
 *
 * @param userIDs     The key's user IDs
 * @param fingerprint The key's unique fingerprint
 * @param timeCreated The key's creation date
 * @param timeExpires The key's expiration date, if the key expires
 * @param encodedForm The ASCII-armoured form of the key
 */

public record NPPublicKey(
  Set<String> userIDs,
  NPFingerprint fingerprint,
  OffsetDateTime timeCreated,
  Optional<OffsetDateTime> timeExpires,
  String encodedForm)
{
  /**
   * A public key.
   *
   * @param userIDs     The key's user IDs
   * @param fingerprint The key's unique fingerprint
   * @param timeCreated The key's creation date
   * @param timeExpires The key's expiration date, if the key expires
   * @param encodedForm The ASCII-armoured form of the key
   */

  public NPPublicKey
  {
    Objects.requireNonNull(encodedForm, "encodedForm");
    Objects.requireNonNull(fingerprint, "fingerprint");
    Objects.requireNonNull(userIDs, "primaryUserID");
    Objects.requireNonNull(timeCreated, "timeCreated");
    Objects.requireNonNull(timeExpires, "expires");

    if (userIDs.isEmpty()) {
      throw new NPValidityException("Keys must have at least one user ID");
    }
  }
}
