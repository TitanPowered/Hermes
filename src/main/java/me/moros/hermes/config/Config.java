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

package me.moros.hermes.config;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.CommentedConfigurationNode;

public final class Config extends Configurable {
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

  public Component separator() {
    return separator;
  }

  public Component joinPrefix() {
    return joinPrefix;
  }

  public Component quitPrefix() {
    return quitPrefix;
  }

  public Component nameFormat(Player player) {
    return parsePlaceholder(nameFormat, player);
  }

  public Component namePrefix(Player player) {
    return parsePlaceholder(namePrefix, player, -1);
  }

  public Component nameSuffix(Player player) {
    return parsePlaceholder(nameSuffix, player, 1);
  }

  public Component msgJoiner() {
    return msgJoiner;
  }

  public Component msgPrefix() {
    return msgPrefix;
  }

  public Component spyMsgPrefix() {
    return spyMsgPrefix;
  }

  public Component selfMsg() {
    return selfMsg;
  }

  public Component header() {
    return parsePlaceholder(header, null);
  }

  public Component footer() {
    return parsePlaceholder(footer, null);
  }

  public Component playerPrefix(Player player) {
    return parsePlaceholder(playerPrefix, player);
  }
}
