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

package me.moros.hermes;

import me.moros.hermes.config.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.Component.text;

public final class HermesUtil {
  public static final TextColor BASE_COLOR = TextColor.fromHexString("#EEEEEE");

  public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer
    .legacyAmpersand().toBuilder().hexColors().build();

  public static final MiniMessage MINI_SERIALIZER = MiniMessage.miniMessage();

  private HermesUtil() {
  }

  public static Component join(Player player) {
    return ConfigManager.config().joinPrefix().append(text(player.getName(), BASE_COLOR));
  }

  public static Component quit(Player player) {
    return ConfigManager.config().quitPrefix().append(text(player.getName(), BASE_COLOR));
  }

  public static Component renderChat(Player source, Component sourceDisplayName, Component message) {
    var config = ConfigManager.config();
    Component prefix = config.namePrefix(source);
    Component suffix = config.nameSuffix(source);
    Component name = config.nameFormat(source);
    String raw = PlainTextComponentSerializer.plainText().serialize(message);
    Component formatted = Formatter.createAndFormat(source, SERIALIZER.deserialize(raw));
    return Component.text().color(BASE_COLOR)
      .append(prefix).append(name).append(suffix)
      .append(config.separator()).append(formatted)
      .build();
  }

  public static void refreshHeaderFooter() {
    Component header = ConfigManager.config().header();
    Component footer = ConfigManager.config().footer();
    Bukkit.getOnlinePlayers().forEach(p -> p.sendPlayerListHeaderAndFooter(header, footer));
  }

  public static void refreshListName(Player player) {
    player.playerListName(ConfigManager.config().playerPrefix(player).append(player.name()));
  }
}
