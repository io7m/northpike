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

package com.io7m.northpike.server.api;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Configuration information for directories used by the server.
 *
 * @param repositoryDirectory The directory used to store cloned repositories
 * @param archiveDirectory The directory used to store archives for builds
 */

public record NPServerDirectoryConfiguration(
  Path repositoryDirectory, Path archiveDirectory)
{
  /**
   * Configuration information for {@code idstore}.
   *
   * @param repositoryDirectory The directory used to store cloned repositories
   * @param archiveDirectory The directory used to store archives for builds
   */

  public NPServerDirectoryConfiguration
  {
    Objects.requireNonNull(repositoryDirectory, "repositoryDirectory");
    Objects.requireNonNull(archiveDirectory, "archiveDirectory");

    repositoryDirectory = repositoryDirectory.toAbsolutePath().normalize();
    archiveDirectory = archiveDirectory.toAbsolutePath().normalize();
  }
}
