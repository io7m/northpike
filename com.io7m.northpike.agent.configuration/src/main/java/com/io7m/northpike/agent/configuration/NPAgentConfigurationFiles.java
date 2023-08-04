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


package com.io7m.northpike.agent.configuration;

import com.io7m.anethum.api.ParseSeverity;
import com.io7m.anethum.api.ParseStatus;
import com.io7m.anethum.api.ParsingException;
import com.io7m.blackthorne.api.BTException;
import com.io7m.blackthorne.api.BTParseError;
import com.io7m.blackthorne.api.BTParseErrorType;
import com.io7m.blackthorne.jxe.BlackthorneJXE;
import com.io7m.northpike.agent.api.NPAgentConfiguration;
import com.io7m.northpike.agent.configuration.internal.v1.NPAv1ConfigurationParser;
import com.io7m.northpike.strings.NPStrings;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.io7m.northpike.agent.configuration.internal.v1.NPAv1.element;

/**
 * A parser of configurations.
 */

public final class NPAgentConfigurationFiles implements Closeable
{
  private final NPStrings strings;
  private final URI source;
  private final InputStream stream;
  private final Consumer<ParseStatus> statusConsumer;

  /**
   * A parser of configurations.
   *
   * @param inStrings        The string resources
   * @param inSource         The source
   * @param inStream         The stream
   * @param inStatusConsumer A status consumer
   */

  private NPAgentConfigurationFiles(
    final NPStrings inStrings,
    final URI inSource,
    final InputStream inStream,
    final Consumer<ParseStatus> inStatusConsumer)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.source =
      Objects.requireNonNull(inSource, "source");
    this.stream =
      Objects.requireNonNull(inStream, "stream");
    this.statusConsumer =
      Objects.requireNonNull(inStatusConsumer, "statusConsumer");
  }

  private static ParseStatus mapParseError(
    final BTParseError error)
  {
    return ParseStatus.builder("parse-error", error.message())
      .withSeverity(mapSeverity(error.severity()))
      .withLexical(error.lexical())
      .build();
  }

  private static ParseSeverity mapSeverity(
    final BTParseErrorType.Severity severity)
  {
    return switch (severity) {
      case ERROR -> ParseSeverity.PARSE_ERROR;
      case WARNING -> ParseSeverity.PARSE_WARNING;
    };
  }

  /**
   * Open the given configuration file.
   *
   * @param strings        The string resources
   * @param file           The file
   * @param statusConsumer A consumer of error messages
   *
   * @return A parser
   *
   * @throws IOException On errors
   */

  public static NPAgentConfigurationFiles open(
    final NPStrings strings,
    final Path file,
    final Consumer<ParseStatus> statusConsumer)
    throws IOException
  {
    return open(
      strings,
      Files.newInputStream(file),
      file.toUri(),
      statusConsumer
    );
  }

  /**
   * Open the given configuration file.
   *
   * @param strings        The string resources
   * @param statusConsumer A consumer of error messages
   * @param uri            The URI
   * @param inputStream    The input stream
   *
   * @return A parser
   */

  public static NPAgentConfigurationFiles open(
    final NPStrings strings,
    final InputStream inputStream,
    final URI uri,
    final Consumer<ParseStatus> statusConsumer)
  {
    return new NPAgentConfigurationFiles(
      strings,
      uri,
      inputStream,
      statusConsumer
    );
  }

  /**
   * Execute the parser.
   *
   * @return A configuration
   *
   * @throws ParsingException On errors
   */

  public NPAgentConfiguration execute()
    throws ParsingException
  {
    try {
      final NPAgentConfiguration configuration =
        BlackthorneJXE.parse(
          this.source,
          this.stream,
          Map.ofEntries(
            Map.entry(
              element("Configuration"),
              context -> {
                return new NPAv1ConfigurationParser(
                  this.strings
                );
              }
            )
          ),
          NPAgentConfigurationSchemas.schemas()
        );

      return configuration;
    } catch (final BTException e) {
      final var statuses =
        e.errors()
          .stream()
          .map(NPAgentConfigurationFiles::mapParseError)
          .collect(Collectors.toList());

      for (final var status : statuses) {
        this.statusConsumer.accept(status);
      }

      throw new ParsingException(e.getMessage(), List.copyOf(statuses));
    }
  }

  @Override
  public void close()
    throws IOException
  {
    this.stream.close();
  }
}
