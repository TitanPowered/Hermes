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

import me.moros.hermes.config.serializer.Serializers;
import me.moros.hermes.util.Debounced;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.reactive.Disposable;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.ValueReference;
import org.spongepowered.configurate.reference.WatchServiceListener;

public final class ConfigManager {
  private final Config defaultConfig;

  private final Collection<Runnable> subscribers;
  private final ConfigurationReference<CommentedConfigurationNode> reference;
  private final ValueReference<Config, CommentedConfigurationNode> configReference;
  private final Debounced<?> buffer;
  private final Disposable rootSubscriber;

  public ConfigManager(Logger logger, Path directory, WatchServiceListener listener) throws IOException {
    this.defaultConfig = new Config();
    this.subscribers = new CopyOnWriteArrayList<>();

    Path path = directory.resolve("hermes.conf");
    Files.createDirectories(path.getParent());

    this.reference = listener.listenToConfiguration(f -> HoconConfigurationLoader.builder()
      .defaultOptions(o -> o.serializers(b -> b.registerAll(Serializers.ALL)))
      .path(f).build(), path);
    this.reference.errors().subscribe(e -> logger.warn(e.getValue().getMessage(), e.getValue()));
    this.configReference = reference.referenceTo(Config.class, NodePath.path(), defaultConfig);
    this.reference.save();

    this.buffer = Debounced.create(this::updateSubscribers, 1, TimeUnit.SECONDS);
    this.rootSubscriber = reference.updates().subscribe(e -> buffer.request());
  }

  public void close() {
    rootSubscriber.dispose();
    subscribers.clear();
    reference.close();
  }

  public void subscribe(Runnable runnable) {
    subscribers.add(runnable);
  }

  private void updateSubscribers() {
    if (!subscribers.isEmpty()) {
      subscribers.forEach(Runnable::run);
    }
  }

  public Config config() {
    Config config = configReference.get();
    return config == null ? defaultConfig : config;
  }
}
