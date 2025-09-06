/*
 * Copyright 2021-2025 Moros
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

import io.github.miniplaceholders.api.MiniPlaceholders;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public final class Config {
  public ChatConfig chat = new ChatConfig();
  public MessageConfig message = new MessageConfig();
  public TabConfig tab = new TabConfig();

  public ChatConfig chat() {
    return chat;
  }

  public MessageConfig message() {
    return message;
  }

  public TabConfig tab() {
    return tab;
  }

  @ConfigSerializable
  public static final class ChatConfig {
    public String joinMessage = "<dark_gray>[<green>+</green>]</dark_gray> <color:#eeeeee><player_name></color>";
    public String quitMessage = "<dark_gray>[<red>-</red>]</dark_gray> <color:#eeeeee><player_name></color>";
    public String format = "<vault_prefix><hover:show_text:\"<dark_aqua>Name: <color:#eeeeee><player_name></color></dark_aqua><newline><newline><gray>Click to message.</gray>\"><hermes_click><player_name></hermes_click></hover><vault_suffix><gray> » </gray>";

    public Component joinMessage(Player player) {
      return parse(joinMessage, player);
    }

    public Component quitMessage(Player player) {
      return parse(quitMessage, player);
    }

    public Component format(Player player) {
      return parse(format, player);
    }
  }

  @ConfigSerializable
  public static final class MessageConfig {
    public String separator = "<gray> » </gray>";
    public String joiner = "<gray> -> </gray>";
    public String prefix = "<color:#eeeeee>[<color:#809acc>Msg</color>]</color> ";
    public String spyPrefix = "<color:#eeeeee>[<color:#809acc>Spy Msg</color>]</color> ";
    public String nameFormat = "<hover:show_text:\"<dark_aqua>Name: <color:#eeeeee><player_name></color></dark_aqua><newline><newline><gray>Click to message.</gray>\"><hermes_click><player_name></hermes_click></hover>";
    public String selfNameFormat = "You";
    public Sound notificationSound = Sound.sound(Key.key("entity.player.levelup"), Source.PLAYER, 1F, 1F);

    public Component separator() {
      return parse(separator);
    }

    public Component msgJoiner() {
      return parse(joiner);
    }

    public Component msgPrefix() {
      return parse(prefix);
    }

    public Component spyMsgPrefix() {
      return parse(spyPrefix);
    }

    public Component nameFormat(Player player) {
      return parse(nameFormat, player);
    }

    public Component selfNameFormat() {
      return parse(selfNameFormat);
    }

    public @Nullable Sound notificationSound() {
      return notificationSound;
    }
  }

  @ConfigSerializable
  public static final class TabConfig {
    public String header = "<color:#dddddd>Online players: <green><server_online></green></color><newline><color:#666666><b><st>⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯</st></b></color>";
    public String footer = "<color:#666666><b><st>⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯</st></b></color><newline><color:#f5deb3>Hello world!</color>";
    public String playerListFormat = " <vault_prefix><player_name>";

    public Component header() {
      return parse(header);
    }

    public Component footer() {
      return parse(footer);
    }

    public Component playerListFormat(Player player) {
      return parse(playerListFormat, player);
    }
  }

  private static Component parse(String text) {
    return parseMiniMessage(text, MiniPlaceholders.globalPlaceholders());
  }

  private static Component parse(String text, Player player) {
    var combinedResolver = TagResolver.builder()
      .resolver(MiniPlaceholders.audienceGlobalPlaceholders())
      .resolver(Placeholder.styling("hermes_click", ClickEvent.suggestCommand("/msg " + player.getName() + " ")))
      .build();
    return parseMiniMessage(text, combinedResolver);
  }

  private static Component parseMiniMessage(String text, TagResolver resolver) {
    return MiniMessage.miniMessage().deserialize(text, resolver);
  }
}
