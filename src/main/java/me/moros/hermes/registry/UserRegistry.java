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

package me.moros.hermes.registry;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import me.moros.hermes.model.User;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Registry for all valid users.
 */
public final class UserRegistry implements Registry<User> {
  private final Map<UUID, User> users;

  UserRegistry() {
    users = new ConcurrentHashMap<>();
  }

  public boolean contains(UUID uuid) {
    return users.containsKey(uuid);
  }

  public User user(Player player) {
    return Objects.requireNonNull(users.get(player.getUniqueId()));
  }

  public @Nullable User user(UUID uuid) {
    return users.get(uuid);
  }

  public void invalidate(UUID uuid) {
    users.remove(uuid);
  }

  public void register(User user) {
    users.putIfAbsent(user.uuid(), user);
  }

  public Stream<User> stream() {
    return users.values().stream();
  }

  @Override
  public Iterator<User> iterator() {
    return Collections.unmodifiableCollection(users.values()).iterator();
  }
}
