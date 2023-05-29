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

import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.arguments.standard.StringArgument.StringMode;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import me.moros.hermes.Hermes;
import me.moros.hermes.HermesUtil;
import me.moros.hermes.User;
import me.moros.hermes.locale.Message;
import me.moros.hermes.registry.Registries;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class HermesCommand {
  private final Hermes plugin;
  private final CommandManager manager;
  private final MinecraftHelp<CommandSender> help;

  HermesCommand(Hermes plugin, CommandManager manager) {
    this.plugin = plugin;
    this.manager = manager;
    this.help = MinecraftHelp.createNative("/hermes help", manager);
    this.help.setMaxResultsPerPage(8);
    construct();
  }

  private void construct() {
    var builder = manager.commandBuilder("hermes")
      .meta(CommandMeta.DESCRIPTION, "Base command for hermes");
    var spyArg = BooleanArgument
      .<CommandSender>builder("state").withLiberal(true).asOptional();
    manager.command(builder.handler(c -> help.queryCommands("", c.getSender())))
      .command(builder.literal("socialspy", "spy")
        .meta(CommandMeta.DESCRIPTION, "Toggles if you can see message commands in chat")
        .permission(CommandPermissions.SPY)
        .argument(spyArg)
        .senderType(Player.class)
        .handler(c -> onSpy(c.getSender(), c.getOrDefault("state", null)))
      ).command(builder.literal("reload")
        .meta(CommandMeta.DESCRIPTION, "Reload the plugin")
        .permission(CommandPermissions.RELOAD)
        .handler(c -> onReload(c.getSender()))
      ).command(builder.literal("version")
        .meta(CommandMeta.DESCRIPTION, "View version info about Hermes")
        .permission(CommandPermissions.VERSION)
        .handler(c -> onVersion(c.getSender()))
      ).command(builder.literal("help", "h")
        .meta(CommandMeta.DESCRIPTION, "View info about a command")
        .permission(CommandPermissions.HELP)
        .argument(StringArgument.optional("query", StringMode.GREEDY))
        .handler(c -> help.queryCommands(c.getOrDefault("query", ""), c.getSender()))
      );
  }

  private void onReload(CommandSender sender) {
    plugin.translationManager().reload();
    HermesUtil.refreshHeaderFooter();
    Bukkit.getOnlinePlayers().forEach(HermesUtil::refreshListName);
    Message.RELOAD.send(sender);
  }

  private void onVersion(CommandSender user) {
    String link = "https://github.com/PrimordialMoros/Hermes";
    Component version = Message.brand(Component.text("Version: ", NamedTextColor.DARK_AQUA))
      .append(Component.text(plugin.version(), NamedTextColor.GREEN))
      .hoverEvent(HoverEvent.showText(Message.VERSION_COMMAND_HOVER.build(plugin.author(), link)))
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
}
