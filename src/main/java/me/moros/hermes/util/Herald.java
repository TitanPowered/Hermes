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

import me.moros.hermes.config.ConfigManager;
import me.moros.hermes.model.HermesMessage;
import me.moros.hermes.model.User;
import me.moros.hermes.registry.Registries;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;

public final class Herald {
  private final Sound notification;

  Herald() {
    notification = Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Source.PLAYER, 1F, 1F);
  }

  // TODO msg signing, needs signed command arguments or nms to edit chat type registry
  public void handleMessage(User sender, User receiver, String msg) {
    HermesMessage message = HermesUtil.buildMessage(ConfigManager.config(), sender, receiver, msg);
    if (!sender.uuid().equals(receiver.uuid())) {
      sender.sendMessage(message.normal());
    }

    Registries.USERS.stream()
      .filter(u -> u.socialSpy() && !sender.uuid().equals(u.uuid()))
      .collect(Audience.toAudience()).sendMessage(message.spy());

    receiver.sendMessage(message.self());
    receiver.playSound(notification);

    sender.lastRecipient(receiver);
    receiver.lastRecipient(sender);
  }

  public static Herald create() {
    return new Herald();
  }
}
