/*
 * Copyright 2021-2024 Moros
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

package me.moros.hermes;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.moros.hermes.command.CommandPermissions;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

final class PermissionInitializer {
  PermissionInitializer() {
    init();
  }

  void init() {
    initPlayerNodes();
    initAdminNodes();
  }

  private void initPlayerNodes() {
    var children = Stream.of(CommandPermissions.HELP, CommandPermissions.VERSION,
        CommandPermissions.MSG, CommandPermissions.REPLY)
      .map(org.incendo.cloud.permission.Permission::permissionString).collect(Collectors.toSet());
    register("hermes.player", children, TriState.TRUE);
  }

  private void initAdminNodes() {
    var decorations = Arrays.stream(TextDecoration.values())
      .map(d -> "hermes.format." + d.name()).collect(Collectors.toSet());
    register("hermes.format.decorations", decorations, TriState.NOT_SET);

    var children = Stream.of(CommandPermissions.SPY)
      .map(org.incendo.cloud.permission.Permission::permissionString).collect(Collectors.toSet());
    children.add("hermes.player");
    children.add("hermes.format.color");
    children.add("hermes.format.translatable");
    children.add("hermes.format.decorations");
    children.add("hermes.format.gradient");
    children.add("hermes.format.rainbow");
    children.add("hermes.format.reset");
    children.add("hermes.format.newline");
    register("hermes.admin", children, TriState.NOT_SET);
  }

  private void register(String node, Collection<String> children, TriState def) {
    var permDef = switch (def) {
      case TRUE -> PermissionDefault.TRUE;
      case NOT_SET -> PermissionDefault.OP;
      case FALSE -> PermissionDefault.FALSE;
    };
    var map = children.stream().collect(Collectors.toMap(Function.identity(), v -> true));
    Bukkit.getPluginManager().addPermission(new Permission(node, permDef, map));
  }
}
