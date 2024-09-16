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
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.minecraft.signed.SignedGreedyStringParser;
import org.incendo.cloud.minecraft.signed.SignedString;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.parser.standard.BooleanParser;

record HermesCommand(Commander commander) {
  private void construct() {
    commander().manager()
      .command(commander().manager()
        .commandBuilder("msg")
        .required("player", PlayerParser.playerParser())
        .required("message", SignedGreedyStringParser.signedGreedyStringParser())
        .commandDescription(RichDescription.of(Message.MSG_CMD_DESC.build()))
        .permission(CommandPermissions.MSG)
        .senderType(PlayerSource.class)
        .handler(c -> onMsg(c.sender(), c.get("player"), c.get("message")))
      ).command(commander().manager()
        .commandBuilder("reply", "r")
        .required("message", SignedGreedyStringParser.signedGreedyStringParser())
        .commandDescription(RichDescription.of(Message.REPLY_CMD_DESC.build()))
        .permission(CommandPermissions.REPLY)
        .senderType(PlayerSource.class)
        .handler(c -> onReply(c.sender(), c.get("message")))
      ).command(commander().manager()
        .commandBuilder("socialspy", "spy")
        .optional("state", BooleanParser.booleanParser(true))
        .commandDescription(RichDescription.of(Message.SPY_CMD_DESC.build()))
        .permission(CommandPermissions.SPY)
        .senderType(PlayerSource.class)
        .handler(c -> onSpy(c.sender(), c.getOrDefault("state", null)))
      );
  }

  private void onMsg(PlayerSource source, Player target, SignedString msg) {
    User sender = Registries.USERS.user(source.source());
    User receiver = Registries.USERS.user(target);
    commander().herald().handleMessage(sender, receiver, msg);
  }

  private void onReply(PlayerSource source, SignedString msg) {
    User sender = Registries.USERS.user(source.source());
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
    commander().herald().handleMessage(sender, receiver, msg);
  }

  private void onSpy(PlayerSource source, @Nullable Boolean value) {
    User user = Registries.USERS.user(source.source());
    boolean enabled = user.socialSpy(value == null ? !user.socialSpy() : value);
    if (enabled) {
      Message.SPY_ON.send(user);
    } else {
      Message.SPY_OFF.send(user);
    }
  }

  public static void register(Commander commander) {
    new HermesCommand(commander).construct();
  }
}
