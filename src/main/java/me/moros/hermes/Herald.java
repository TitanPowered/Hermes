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

import java.util.Objects;

import me.moros.hermes.registry.Registries;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class Herald {
  private final Sound notification;

  Herald() {
    notification = Sound.sound(Key.key("entity.player.levelup"), Source.PLAYER, 1F, 1F);
  }

  public void handleMessage(@NonNull User sender, @NonNull User receiver, @NonNull String msg) {
    Objects.requireNonNull(sender);
    Objects.requireNonNull(receiver);
    Objects.requireNonNull(msg);

    HermesMessage message = HermesMessage.build(sender, receiver, msg);

    if (!sender.uuid().equals(receiver.uuid())) {
      sender.sendMessage(sender, message.normal());
    }

    Registries.USERS.stream()
      .filter(u -> u.socialSpy() && !sender.uuid().equals(u.uuid()))
      .collect(Audience.toAudience()).sendMessage(sender, message.spy());

    receiver.sendMessage(sender, message.self());
    receiver.playSound(notification);

    sender.lastRecipient(receiver);
    receiver.lastRecipient(sender);
  }
}
