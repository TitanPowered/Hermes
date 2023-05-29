/*
 * Copyright 2021-2023 Moros
 *
 * This file is part of Hermes.
 *
 * Hermes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Hermes is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Hermes. If not, see <https://www.gnu.org/licenses/>.
 */

package me.moros.hermes.command;

import cloud.commandframework.permission.CommandPermission;
import cloud.commandframework.permission.Permission;

public final class CommandPermissions {
  private CommandPermissions() {
  }

  public static final CommandPermission HELP = create("help");
  public static final CommandPermission SPY = create("socialspy");
  public static final CommandPermission MSG = create("msg");
  public static final CommandPermission REPLY = create("reply");
  public static final CommandPermission VERSION = create("version");
  public static final CommandPermission RELOAD = create("reload");

  private static Permission create(String node) {
    return Permission.of("hermes.command." + node);
  }
}
