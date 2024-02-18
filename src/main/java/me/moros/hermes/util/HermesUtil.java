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

package me.moros.hermes.util;

import me.moros.hermes.config.Config;
import me.moros.hermes.config.ConfigManager;
import me.moros.hermes.model.HermesMessage;
import me.moros.hermes.model.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.Component.text;

public final class HermesUtil {
  public static final TextColor BASE_COLOR = TextColor.fromHexString("#eeeeee");

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
    Component formatted = Formatter.format(source, PlainTextComponentSerializer.plainText().serialize(message));
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

  public static HermesMessage buildMessage(Config config, User sender, User receiver, String content) {
    Component s = config.nameFormat(sender.player());
    Component r;
    if (sender.uuid().equals(receiver.uuid())) {
      r = s;
    } else {
      r = config.nameFormat(receiver.player());
    }
    Component msg = Formatter.format(sender.player(), content);

    Component msgPrefix = config.msgPrefix();
    Component spyMsgPrefix = config.spyMsgPrefix();

    Component prepared = text().append(s).append(config.msgJoiner()).append(r)
      .append(config.separator()).append(msg).color(BASE_COLOR).build();
    Component preparedSelf = text().append(s).append(config.msgJoiner()).append(config.selfMsg())
      .append(config.separator()).append(msg).color(BASE_COLOR).build();

    return HermesMessage.build(msgPrefix.append(prepared), msgPrefix.append(preparedSelf), spyMsgPrefix.append(prepared));
  }
}
