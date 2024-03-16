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

package com.io7m.northpike.shell.internal;

import com.io7m.northpike.shell.commons.NPShellCmdAbstractConfirmation;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.util.Optional;

/**
 * "plan-delete-confirm"
 */

public final class NPShellCmdPlanDeleteConfirm
  extends NPShellCmdAbstractConfirmation
{
  /**
   * Construct a command.
   *
   * @param inServices The service directory
   */

  public NPShellCmdPlanDeleteConfirm(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "plan-delete-confirm",
        new QConstant("Confirm deletion of a plan."),
        Optional.empty()
      )
    );
  }

  @Override
  public boolean isHidden()
  {
    return true;
  }
}
