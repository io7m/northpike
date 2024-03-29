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


package com.io7m.northpike.tools.maven;

import com.io7m.northpike.model.NPToolDescription;
import com.io7m.northpike.model.NPToolExecutionName;
import com.io7m.northpike.model.NPToolName;
import com.io7m.northpike.strings.NPStrings;
import com.io7m.northpike.tools.api.NPToolFactoryType;
import com.io7m.northpike.tools.api.NPToolType;
import com.io7m.northpike.tools.maven.internal.NPTM3;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import com.io7m.verona.core.Version;
import com.io7m.verona.core.VersionRange;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A tool factory for Maven 3.*.
 */

public final class NPTMFactory3 implements NPToolFactoryType
{
  private static final NPToolName PACKAGE_NAME =
    NPToolName.of("org.apache.maven");

  private static final String DESCRIPTION_FORMATTED =
    "Tool service for %s (Apache Maven 3.*).".formatted(PACKAGE_NAME);

  private static final VersionRange PACKAGE_VERSIONS =
    new VersionRange(
      Version.of(3, 9, 1),
      true,
      Version.of(4, 0, 0),
      false
    );

  private static final Map<NPToolExecutionName, List<String>> DEFAULT_EXECUTIONS;
  private static final NPToolDescription DESCRIPTION;

  static {
    final var m =
      new HashMap<NPToolExecutionName, List<String>>();

    m.put(
      NPToolExecutionName.of("clean-verify"),
      List.of("-C", "-e", "-U", "clean", "verify")
    );
    m.put(
      NPToolExecutionName.of("clean-verify-site"),
      List.of("-C", "-e", "-U", "clean", "verify", "site")
    );
    m.put(
      NPToolExecutionName.of("deploy"),
      List.of("-C", "-e", "-U", "deploy")
    );

    DEFAULT_EXECUTIONS = Map.copyOf(m);

    DESCRIPTION = new NPToolDescription(
      PACKAGE_NAME,
      DESCRIPTION_FORMATTED,
      PACKAGE_VERSIONS,
      DEFAULT_EXECUTIONS
    );
  }

  /**
   * A tool factory for Maven 3.*.
   */

  public NPTMFactory3()
  {

  }

  @Override
  public String toString()
  {
    return "[NPTMFactory3 %s %s]"
      .formatted(this.toolName(), this.toolVersions());
  }

  @Override
  public NPToolDescription toolDescription()
  {
    return DESCRIPTION;
  }

  @Override
  public NPToolType createTool(
    final RPServiceDirectoryType services,
    final Version version,
    final Path directory)
  {
    return new NPTM3(
      this,
      services.requireService(NPStrings.class),
      version,
      directory
    );
  }

  @Override
  public String description()
  {
    return DESCRIPTION_FORMATTED;
  }
}
