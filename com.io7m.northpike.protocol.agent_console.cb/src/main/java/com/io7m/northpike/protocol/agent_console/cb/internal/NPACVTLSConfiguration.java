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


package com.io7m.northpike.protocol.agent_console.cb.internal;

import com.io7m.northpike.model.tls.NPTLSConfigurationType;
import com.io7m.northpike.model.tls.NPTLSDisabled;
import com.io7m.northpike.model.tls.NPTLSEnabledClientAnonymous;
import com.io7m.northpike.model.tls.NPTLSEnabledExplicit;
import com.io7m.northpike.protocol.agent_console.cb.NPAC1TLSConfiguration;
import com.io7m.northpike.protocol.api.NPProtocolMessageValidatorType;

import static com.io7m.northpike.protocol.agent_console.cb.internal.NPACVTLSStore.TLS_STORE;

/**
 * A validator.
 */

public enum NPACVTLSConfiguration
  implements NPProtocolMessageValidatorType<NPTLSConfigurationType, NPAC1TLSConfiguration>
{
  /**
   * A validator.
   */

  TLS_CONFIGURATION;

  @Override
  public NPAC1TLSConfiguration convertToWire(
    final NPTLSConfigurationType message)
  {
    return switch (message) {
      case final NPTLSDisabled d -> {
        yield new NPAC1TLSConfiguration.NPAC1TLSDisabled();
      }

      case final NPTLSEnabledExplicit e -> {
        yield new NPAC1TLSConfiguration.NPAC1TLSEnabledExplicit(
          TLS_STORE.convertToWire(e.keyStore()),
          TLS_STORE.convertToWire(e.trustStore())
        );
      }

      case final NPTLSEnabledClientAnonymous d -> {
        yield new NPAC1TLSConfiguration.NPAC1TLSEnabledClientAnonymous();
      }
    };
  }

  @Override
  public NPTLSConfigurationType convertFromWire(
    final NPAC1TLSConfiguration message)
  {
    return switch (message) {
      case final NPAC1TLSConfiguration.NPAC1TLSDisabled ignored -> {
        yield NPTLSDisabled.TLS_DISABLED;
      }

      case final NPAC1TLSConfiguration.NPAC1TLSEnabledExplicit e -> {
        yield new NPTLSEnabledExplicit(
          TLS_STORE.convertFromWire(e.fieldKeyStore()),
          TLS_STORE.convertFromWire(e.fieldTrustStore())
        );
      }

      case final NPAC1TLSConfiguration.NPAC1TLSEnabledClientAnonymous ignored -> {
        yield new NPTLSEnabledClientAnonymous();
      }
    };
  }
}
