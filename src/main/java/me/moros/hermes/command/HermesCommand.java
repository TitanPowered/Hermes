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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.ImmutableMinecraftHelp;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.parser.standard.BooleanParser;
import org.incendo.cloud.parser.standard.StringParser;

record HermesCommand(Commander commander, MinecraftHelp<CommandSender> help) {
  private HermesCommand(Commander commander) {
    this(commander, createHelp(commander.manager()));
  }

  private void construct() {
    var builder = commander().manager().commandBuilder("hermes");
    commander().manager().command(builder.handler(c -> help.queryCommands("", c.sender())))
      .command(builder.literal("socialspy", "spy")
        .optional("state", BooleanParser.booleanParser(true))
        .commandDescription(RichDescription.of(Message.SPY_CMD_DESC.build()))
        .permission(CommandPermissions.SPY)
        .senderType(Player.class)
        .handler(c -> onSpy(c.sender(), c.getOrDefault("state", null)))
      ).command(builder.literal("version")
        .commandDescription(RichDescription.of(Message.VERSION_CMD_DESC.build()))
        .permission(CommandPermissions.VERSION)
        .handler(c -> onVersion(c.sender()))
      ).command(builder.literal("help", "h")
        .optional("query", StringParser.greedyStringParser())
        .commandDescription(RichDescription.of(Message.HELP_CMD_DESC.build()))
        .permission(CommandPermissions.HELP)
        .handler(c -> help.queryCommands(c.getOrDefault("query", ""), c.sender()))
      );
  }

  private void onVersion(CommandSender user) {
    String link = "https://github.com/PrimordialMoros/Hermes";
    Component version = Message.brand(Component.text("Version: ", NamedTextColor.DARK_AQUA))
      .append(Component.text(commander().plugin().version(), NamedTextColor.GREEN))
      .hoverEvent(HoverEvent.showText(Message.VERSION_COMMAND_HOVER.build(commander().plugin().author(), link)))
      .clickEvent(ClickEvent.openUrl(link));
    user.sendMessage(version);
  }

  private void onSpy(CommandSender sender, @Nullable Boolean value) {
    if (sender instanceof Player player) {
      User user = Registries.USERS.user(player);
      boolean enabled = user.socialSpy(value == null ? !user.socialSpy() : value);
      if (enabled) {
        Message.SPY_ON.send(user);
      } else {
        Message.SPY_OFF.send(user);
      }
    }
  }

  private static MinecraftHelp<CommandSender> createHelp(CommandManager<CommandSender> manager) {
    return ImmutableMinecraftHelp.copyOf(MinecraftHelp.createNative("/hermes help", manager))
      .withMaxResultsPerPage(8);
  }

  public static void register(Commander commander) {
    new HermesCommand(commander).construct();
  }
}
