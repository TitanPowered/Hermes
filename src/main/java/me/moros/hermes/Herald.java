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

package me.moros.hermes;

import java.util.Objects;

import me.moros.hermes.config.Config;
import me.moros.hermes.config.ConfigManager;
import me.moros.hermes.model.HermesMessage;
import me.moros.hermes.model.User;
import me.moros.hermes.registry.Registries;
import me.moros.hermes.util.Formatter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.ChatType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.cloud.minecraft.signed.SignedString;

import static net.kyori.adventure.text.Component.text;

public final class Herald {
  private static final TextColor BASE_COLOR = TextColor.fromHexString("#eeeeee");

  private final ConfigManager configManager;

  private Herald(ConfigManager configManager) {
    this.configManager = configManager;
    this.configManager.subscribe(this::refreshHeaders);
  }

  private void refreshHeaders() {
    refreshHeaderFooter();
    Bukkit.getOnlinePlayers().forEach(this::refreshListName);
  }

  private Config config() {
    return configManager.config();
  }

  public Component joinMessage(Player player) {
    return config().chat().joinMessage(player);
  }

  public Component quitMessage(Player player) {
    return config().chat().quitMessage(player);
  }

  private static HermesMessage buildMessage(Config config, User sender, User receiver, SignedString signed) {
    Component s = config.message().nameFormat(sender.player());
    Component r;
    if (sender.uuid().equals(receiver.uuid())) {
      r = s;
    } else {
      r = config.message().nameFormat(receiver.player());
    }
    Component msg = Formatter.format(sender.player(), signed.string());

    Component msgPrefix = config.message().msgPrefix();
    Component spyMsgPrefix = config.message().spyMsgPrefix();

    Component prepared = text().append(s).append(config.message().msgJoiner()).append(r)
      .append(config.message().separator()).append(msg).color(BASE_COLOR).build();
    Component preparedSelf = text().append(s).append(config.message().msgJoiner()).append(config.message().selfNameFormat())
      .append(config.message().separator()).append(msg).color(BASE_COLOR).build();

    return HermesMessage.build(signed, msgPrefix.append(prepared), msgPrefix.append(preparedSelf), spyMsgPrefix.append(prepared));
  }

  private static final Key RAW_CHAT = Key.key("paper", "raw");

  public void handleMessage(User sender, User receiver, SignedString msg) {
    var config = config();
    HermesMessage message = buildMessage(config, sender, receiver, msg);
    ChatType type = ChatType.chatType(RAW_CHAT);
    if (!sender.uuid().equals(receiver.uuid())) {
      message.signed().sendMessage(sender, type, message.normal());
    }

    Audience spyAudience = Registries.USERS.stream()
      .filter(u -> u.socialSpy() && !sender.uuid().equals(u.uuid()))
      .collect(Audience.toAudience());
    message.signed().sendMessage(spyAudience, type, message.spy());

    message.signed().sendMessage(receiver, type, message.self());
    Sound notificationSound = config.message().notificationSound();
    if (notificationSound != null) {
      receiver.playSound(notificationSound);
    }

    sender.lastRecipient(receiver);
    receiver.lastRecipient(sender);
  }

  public Component renderChat(Player source, Component sourceDisplayName, Component message) {
    Component format = config().chat().format(source);
    Component formattedContent = Formatter.format(source, PlainTextComponentSerializer.plainText().serialize(message));
    return text().color(BASE_COLOR).append(format).append(formattedContent).build();
  }

  public void refreshHeaderFooter() {
    var config = config();
    Component header = config.tab().header();
    Component footer = config.tab().footer();
    Bukkit.getOnlinePlayers().forEach(p -> p.sendPlayerListHeaderAndFooter(header, footer));
  }

  public void refreshListName(Player player) {
    player.playerListName(config().tab().playerListFormat(player));
  }

  public static Herald create(ConfigManager configManager) {
    Objects.requireNonNull(configManager);
    return new Herald(configManager);
  }
}
