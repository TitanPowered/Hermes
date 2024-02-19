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

package me.moros.hermes.command;

import me.moros.hermes.locale.Message;
import me.moros.hermes.model.User;
import me.moros.hermes.registry.Registries;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.parser.standard.StringParser;

record MsgCommand(Commander commander) {
  private void construct() {
    commander().manager().command(commander().manager().commandBuilder("msg")
      .required("player", PlayerParser.playerParser())
      .required("message", StringParser.greedyStringParser())
      .commandDescription(RichDescription.of(Message.MSG_CMD_DESC.build()))
      .permission(CommandPermissions.MSG)
      .senderType(Player.class)
      .handler(c -> onMsg(c.sender(), c.get("player"), c.get("message")))
    );
  }

  private void onMsg(CommandSender commandSender, Player player, String msg) {
    User sender = Registries.USERS.user((Player) commandSender);
    User receiver = Registries.USERS.user(player);
    commander().plugin().herald().handleMessage(sender, receiver, msg);
  }

  public static void register(Commander commander) {
    new MsgCommand(commander).construct();
  }
}
