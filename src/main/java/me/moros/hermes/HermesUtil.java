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

package me.moros.hermes;

import io.papermc.paper.text.PaperComponents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public final class HermesUtil {
  public static final TextColor BASE_COLOR = TextColor.fromHexString("#EEEEEE");
  private static final TextColor URL_COLOR = TextColor.fromHexString("#F5DEB3");

  public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer
    .legacyAmpersand().toBuilder().hexColors().extractUrls(Style.style(URL_COLOR)).build();

  public static final MiniMessage MINI_SERIALIZER = MiniMessage.miniMessage();

  private HermesUtil() {
  }

  public static @NonNull Component join(@NonNull Player player) {
    return Hermes.configManager().config().joinPrefix().append(text(player.getName(), BASE_COLOR));
  }

  public static @NonNull Component quit(@NonNull Player player) {
    return Hermes.configManager().config().quitPrefix().append(text(player.getName(), BASE_COLOR));
  }

  public static @NotNull Component renderChat(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message) {
    var config = Hermes.configManager().config();
    Component prefix = config.namePrefix(source);
    Component suffix = config.nameSuffix(source);
    Component name = config.nameFormat(source);
    String raw = PaperComponents.plainTextSerializer().serialize(message);
    Component formatted = Formatter.createAndFormat(source, SERIALIZER.deserialize(raw));
    return Component.text().color(BASE_COLOR)
      .append(prefix).append(name).append(suffix)
      .append(config.separator()).append(formatted)
      .build();
  }

  public static void refreshHeaderFooter() {
    Component header = Hermes.configManager().config().header();
    Component footer = Hermes.configManager().config().footer();
    Bukkit.getOnlinePlayers().forEach(p -> p.sendPlayerListHeaderAndFooter(header, footer));
  }

  public static void refreshListName(@NonNull Player player) {
    player.playerListName(Hermes.configManager().config().playerPrefix(player).append(player.name()));
  }
}
