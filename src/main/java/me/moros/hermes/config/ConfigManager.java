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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import me.moros.hermes.util.Debounced;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.reactive.Disposable;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.WatchServiceListener;

public final class ConfigManager {
  private static ConfigManager INSTANCE;

  private final AtomicReference<Config> config;
  private final Logger logger;
  private final Collection<Runnable> subscribers;
  private final ConfigurationReference<CommentedConfigurationNode> reference;
  private final Debounced<?> buffer;
  private final Disposable rootSubscriber;

  public ConfigManager(Logger logger, Path directory, WatchServiceListener listener) throws IOException {
    this.logger = logger;
    this.subscribers = new CopyOnWriteArrayList<>();
    Path path = directory.resolve("hermes.conf");
    Files.createDirectories(path.getParent());
    reference = listener.listenToConfiguration(f -> HoconConfigurationLoader.builder().path(f).build(), path);
    reference.errors().subscribe(e -> logger.warn(e.getValue().getMessage(), e.getValue()));
    buffer = Debounced.create(this::onUpdate, 1, TimeUnit.SECONDS);
    rootSubscriber = reference.updates().subscribe(e -> buffer.request());
    config = new AtomicReference<>(new Config(reference.node()));
    if (INSTANCE == null) {
      INSTANCE = this;
    }
  }

  private void onUpdate() {
    this.config.set(new Config(reference.node()));
    if (!subscribers.isEmpty()) {
      subscribers.forEach(Runnable::run);
    }
  }

  public void save() {
    try {
      reference.save();
    } catch (IOException e) {
      logger.warn(e.getMessage(), e);
    }
  }

  public void close() {
    rootSubscriber.dispose();
    subscribers.clear();
    reference.close();
  }

  public <T extends Configurable> void subscribe(Runnable runnable) {
    subscribers.add(runnable);
    runnable.run();
  }

  public static Config config() {
    return INSTANCE.config.get();
  }
}
