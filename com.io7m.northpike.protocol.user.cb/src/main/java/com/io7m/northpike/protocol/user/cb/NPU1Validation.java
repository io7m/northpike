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

package com.io7m.northpike.protocol.user.cb;


import com.io7m.northpike.protocol.api.NPProtocolException;
import com.io7m.northpike.protocol.api.NPProtocolMessageValidatorType;
import com.io7m.northpike.protocol.user.NPUCommandDisconnect;
import com.io7m.northpike.protocol.user.NPUCommandLogin;
import com.io7m.northpike.protocol.user.NPUCommandType;
import com.io7m.northpike.protocol.user.NPUEventType;
import com.io7m.northpike.protocol.user.NPUMessageType;
import com.io7m.northpike.protocol.user.NPUResponseError;
import com.io7m.northpike.protocol.user.NPUResponseOK;
import com.io7m.northpike.protocol.user.NPUResponseType;

import static com.io7m.northpike.protocol.user.cb.internal.NPUVCommandDisconnect.COMMAND_DISCONNECT;
import static com.io7m.northpike.protocol.user.cb.internal.NPUVCommandLogin.COMMAND_LOGIN;
import static com.io7m.northpike.protocol.user.cb.internal.NPUVResponseError.RESPONSE_ERROR;
import static com.io7m.northpike.protocol.user.cb.internal.NPUVResponseOK.RESPONSE_OK;

/**
 * Functions to translate between the core command set and the Agent v1
 * Cedarbridge encoding command set.
 */

public final class NPU1Validation
  implements NPProtocolMessageValidatorType<NPUMessageType, ProtocolNPUv1Type>
{
  /**
   * Functions to translate between the core command set and the Agent v1
   * Cedarbridge encoding command set.
   */

  public NPU1Validation()
  {

  }

  @Override
  public ProtocolNPUv1Type convertToWire(
    final NPUMessageType message)
    throws NPProtocolException
  {
    if (message instanceof final NPUCommandType<?> command) {
      return toWireCommand(command);
    }
    if (message instanceof final NPUResponseType response) {
      return toWireResponse(response);
    }
    if (message instanceof final NPUEventType event) {
      return toWireEvent(event);
    }
    throw new IllegalStateException();
  }

  private static ProtocolNPUv1Type toWireEvent(
    final NPUEventType event)
  {
    throw new IllegalStateException();
  }

  private static ProtocolNPUv1Type toWireResponse(
    final NPUResponseType response)
  {
    if (response instanceof final NPUResponseOK r) {
      return RESPONSE_OK.convertToWire(r);
    }
    if (response instanceof final NPUResponseError r) {
      return RESPONSE_ERROR.convertToWire(r);
    }

    throw new IllegalStateException("Unrecognized response: " + response);
  }

  private static ProtocolNPUv1Type toWireCommand(
    final NPUCommandType<?> command)
  {
    if (command instanceof final NPUCommandLogin c) {
      return COMMAND_LOGIN.convertToWire(c);
    }
    if (command instanceof final NPUCommandDisconnect c) {
      return COMMAND_DISCONNECT.convertToWire(c);
    }

    throw new IllegalStateException("Unrecognized command: " + command);
  }

  @Override
  public NPUMessageType convertFromWire(
    final ProtocolNPUv1Type message)
  {
    if (message instanceof final NPU1CommandLogin c) {
      return COMMAND_LOGIN.convertFromWire(c);
    }
    if (message instanceof final NPU1CommandDisconnect c) {
      return COMMAND_DISCONNECT.convertFromWire(c);
    }

    if (message instanceof final NPU1ResponseError r) {
      return RESPONSE_ERROR.convertFromWire(r);
    }
    if (message instanceof final NPU1ResponseOK r) {
      return RESPONSE_OK.convertFromWire(r);
    }

    throw new IllegalStateException("Unrecognized message: " + message);
  }
}