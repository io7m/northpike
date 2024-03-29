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


package com.io7m.northpike.server.internal.agents;

import com.io7m.northpike.model.tls.NPTLSDisabled;
import com.io7m.northpike.model.tls.NPTLSEnabledClientAnonymous;
import com.io7m.northpike.model.tls.NPTLSEnabledExplicit;
import com.io7m.northpike.server.api.NPServerAgentConfiguration;
import com.io7m.northpike.server.api.NPServerException;
import com.io7m.northpike.server.internal.NPServerExceptions;
import com.io7m.northpike.strings.NPStrings;
import com.io7m.northpike.tls.NPTLSContext;
import com.io7m.northpike.tls.NPTLSContextServiceType;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.GeneralSecurityException;
import java.util.Objects;

/**
 * The socket service for agents.
 */

public final class NPAgentServerSocketService
  implements NPAgentServerSocketServiceType
{
  private final ServerSocketFactory sockets;

  private NPAgentServerSocketService(
    final ServerSocketFactory inSockets)
  {
    this.sockets =
      Objects.requireNonNull(inSockets, "sockets");
  }

  /**
   * The socket service for agents.
   *
   * @param strings       The string resources
   * @param tlsService    The TLS service
   * @param configuration The configuration
   *
   * @return A socket service
   *
   * @throws NPServerException On errors
   */

  public static NPAgentServerSocketServiceType create(
    final NPStrings strings,
    final NPTLSContextServiceType tlsService,
    final NPServerAgentConfiguration configuration)
    throws NPServerException
  {
    Objects.requireNonNull(configuration, "configuration");

    return switch (configuration.tls()) {
      case final NPTLSDisabled ignored -> {
        yield new NPAgentServerSocketService(ServerSocketFactory.getDefault());
      }

      case final NPTLSEnabledExplicit enabled -> {
        final NPTLSContext tlsContext;
        try {
          tlsContext = tlsService.create(
            "AgentService",
            enabled.keyStore(),
            enabled.trustStore()
          );
        } catch (final GeneralSecurityException e) {
          throw NPServerExceptions.errorSecurity(e);
        } catch (final IOException e) {
          throw NPServerExceptions.errorIO(strings, e);
        }
        yield new NPAgentServerSocketService(
          tlsContext.context().getServerSocketFactory()
        );
      }

      case final NPTLSEnabledClientAnonymous ignored -> {
        throw new IllegalStateException(
          "Server sockets must use explicit TLS configurations.");
      }
    };
  }

  @Override
  public ServerSocket createSocket()
    throws IOException
  {
    return this.sockets.createServerSocket();
  }

  @Override
  public String description()
  {
    return "Agent server socket service.";
  }
}
