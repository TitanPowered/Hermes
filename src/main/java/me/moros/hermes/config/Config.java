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

package me.moros.hermes.config;

import me.clip.placeholderapi.PlaceholderAPI;
import me.moros.hermes.HermesUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;

public final class Config {
  private final Component separator;
  private final Component joinPrefix;
  private final Component quitPrefix;
  private final String nameFormat;
  private final String namePrefix;
  private final String nameSuffix;

  private final Component msgJoiner;
  private final Component msgPrefix;
  private final Component spyMsgPrefix;
  private final Component selfMsg;

  private final String header;
  private final String footer;
  private final String playerPrefix;

  Config(CommentedConfigurationNode rootNode) {
    CommentedConfigurationNode chat = rootNode.node("chat");
    separator = parse(chat.node("separator").getString("<gray> » </gray>"));
    joinPrefix = parse(chat.node("join-prefix").getString("<dark_gray>[<green>+</green>] </dark_gray>"));
    quitPrefix = parse(chat.node("quit-prefix").getString("<dark_gray>[<red>-</red>] </dark_gray>"));
    nameFormat = chat.node("name-format").getString("<hover:show_text:\"<dark_aqua>Name: <color:#eeeeee><name></color></dark_aqua><newline><newline><gray>Click to message.</gray>\"><click:suggest_command:\"/msg <name> \"><name></click></hover>");
    namePrefix = chat.node("name-prefix").getString("<papi:vault_prefix>");
    nameSuffix = chat.node("name-suffix").getString("<papi:vault_suffix>");

    CommentedConfigurationNode msg = rootNode.node("message");
    msgJoiner = parse(msg.node("joiner").getString("<gray> -> </gray>"));
    msgPrefix = parse(msg.node("prefix").getString("<color:#eeeeee>[<color:#809ACC>Msg</color>]</color> "));
    spyMsgPrefix = parse(msg.node("spy-prefix").getString("<color:#eeeeee>[<color:#809ACC>Spy Msg</color>]</color> "));
    selfMsg = parse(msg.node("self").getString("You"));

    CommentedConfigurationNode tab = rootNode.node("tab");
    header = tab.node("header").getString(
      "<color:#dddddd>Online players: <green><online></green></color><newline>" +
        "<color:#666666><b><st>⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯</st></b></color>"
    );
    footer = tab.node("footer").getString(
      "<color:#666666><b><st>⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯</st></b></color>" +
        "<newline><color:#f5deb3>Hello world!</color>"
    );
    playerPrefix = tab.node("player-prefix").getString(" <papi:vault_prefix> ");
  }

  public @NonNull Component separator() {
    return separator;
  }

  public @NonNull Component joinPrefix() {
    return joinPrefix;
  }

  public @NonNull Component quitPrefix() {
    return quitPrefix;
  }

  public @NonNull Component nameFormat(@NonNull Player player) {
    return parsePlaceholder(nameFormat, player);
  }

  public @NonNull Component namePrefix(@NonNull Player player) {
    return parsePlaceholder(namePrefix, player, -1);
  }

  public @NonNull Component nameSuffix(@NonNull Player player) {
    return parsePlaceholder(nameSuffix, player, 1);
  }

  public @NonNull Component msgJoiner() {
    return msgJoiner;
  }

  public @NonNull Component msgPrefix() {
    return msgPrefix;
  }

  public @NonNull Component spyMsgPrefix() {
    return spyMsgPrefix;
  }

  public @NonNull Component selfMsg() {
    return selfMsg;
  }

  public @NonNull Component header() {
    return parsePlaceholder(header, null);
  }

  public @NonNull Component footer() {
    return parsePlaceholder(footer, null);
  }

  public @NonNull Component playerPrefix(@NonNull Player player) {
    return parsePlaceholder(playerPrefix, player);
  }

  private static Component parse(String text) {
    return HermesUtil.MINI_SERIALIZER.deserialize(text);
  }

  private static Component parsePlaceholder(String text, @Nullable Player player) {
    return parsePlaceholder(text, player, 0);
  }

  private static Component parsePlaceholder(String text, @Nullable Player player, int space) {
    var builder = TagResolver.builder()
      .resolver(TagResolver.resolver("papi", (args, ctx) -> papiTag(player, args, ctx, space)))
      .resolver(Placeholder.component("online", Component.text(Bukkit.getOnlinePlayers().size())));
    if (player != null) {
      builder.resolver(Placeholder.parsed("name", player.getName()));
    }
    return HermesUtil.MINI_SERIALIZER.deserialize(text, builder.build());
  }

  private static Tag papiTag(Player player, ArgumentQueue args, Context ctx, int space) {
    String placeholder = args.popOr("Missing placeholder id argument!").value();
    String s = PlaceholderAPI.setPlaceholders(player, '%' + placeholder + '%');
    if (s.isBlank()) {
      return Tag.selfClosingInserting(Component.empty());
    }
    Component result = HermesUtil.SERIALIZER.deserialize(s);
    if (space < 0) {
      result = result.append(Component.space());
    } else if (space > 0) {
      result = Component.space().append(result);
    }
    return Tag.selfClosingInserting(result);
  }
}
