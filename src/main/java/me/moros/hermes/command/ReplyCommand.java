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
import me.moros.hermes.model.Recipient;
import me.moros.hermes.model.User;
import me.moros.hermes.registry.Registries;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.parser.standard.StringParser;

record ReplyCommand(Commander commander) {
  private void construct() {
    commander().manager().command(commander().manager().commandBuilder("reply", "r")
      .required("msg", StringParser.greedyStringParser())
      .commandDescription(RichDescription.of(Message.REPLY_CMD_DESC.build()))
      .senderType(Player.class)
      .permission(CommandPermissions.REPLY)
      .handler(c -> onReply(c.sender(), c.get("msg")))
    );
  }

  private void onReply(CommandSender commandSender, String msg) {
    User sender = Registries.USERS.user((Player) commandSender);
    Recipient last = sender.lastRecipient();
    if (last == null) {
      Message.NO_RECIPIENT.send(sender);
      return;
    }
    User receiver = Registries.USERS.user(last.uuid());
    if (receiver == null) {
      Message.OFFLINE_RECIPIENT.send(sender, last.name());
      return;
    }
    commander().plugin().herald().handleMessage(sender, receiver, msg);
  }

  public static void register(Commander commander) {
    new ReplyCommand(commander).construct();
  }
}
