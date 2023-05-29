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

import java.util.Optional;
import java.util.UUID;

import me.moros.hermes.registry.Registries;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class User implements ForwardingAudience.Single, Identity {
  private final Player player;

  private Recipient lastRecipient;
  private boolean socialSpy = false;

  private User(Player player) {
    this.player = player;
  }

  public Player player() {
    return player;
  }

  @Override
  public @NonNull UUID uuid() {
    return player.getUniqueId();
  }

  public @Nullable Recipient lastRecipient() {
    return lastRecipient;
  }

  public void lastRecipient(@Nullable User lastRecipient) {
    this.lastRecipient = lastRecipient == null ? null : new Recipient(lastRecipient);
  }

  public boolean socialSpy() {
    return socialSpy;
  }

  public boolean socialSpy(boolean socialSpy) {
    this.socialSpy = socialSpy;
    return this.socialSpy;
  }

  @Override
  public @NonNull Audience audience() {
    return player;
  }

  public static Optional<User> createUser(Player player) {
    if (Registries.USERS.contains(player.getUniqueId())) {
      return Optional.empty();
    }
    return Optional.of(new User(player));
  }
}
