/*
 * Copyright 2021-2022 Moros
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

import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.meta.CommandMeta;
import me.moros.hermes.Hermes;
import me.moros.hermes.Recipient;
import me.moros.hermes.User;
import me.moros.hermes.locale.Message;
import me.moros.hermes.registry.Registries;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;


public final class ReplyCommand {
  private final CommandManager manager;

  ReplyCommand(@NonNull CommandManager manager) {
    this.manager = manager;
    construct();
  }

  private void construct() {
    manager.command(manager.commandBuilder("reply", "r")
      .meta(CommandMeta.DESCRIPTION, "Quickly reply to the last player to message you")
      .permission("hermes.command.reply")
      .argument(StringArgument.greedy("msg"))
      .senderType(Player.class)
      .handler(c -> onReply(c.getSender(), c.getOrDefault("msg", "")))
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
    Hermes.herald().handleMessage(sender, receiver, msg);
  }
}
