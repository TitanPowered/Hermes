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

package me.moros.hermes.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.WatchServiceListener;

public final class ConfigManager {
  private static Config config;

  private final Logger logger;
  private final WatchServiceListener listener;
  private final ConfigurationReference<CommentedConfigurationNode> reference;

  public ConfigManager(Logger logger, String directory) {
    this.logger = logger;
    Path path = Path.of(directory, "hermes.conf");
    try {
      Files.createDirectories(path.getParent());
      listener = WatchServiceListener.create();
      reference = listener.listenToConfiguration(f -> HoconConfigurationLoader.builder().path(f).build(), path);
      reference.updates().subscribe(this::update);
      reference.save();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void close() {
    try {
      reference.close();
      listener.close();
    } catch (IOException e) {
      logger.warn(e.getMessage(), e);
    }
  }

  private void update(CommentedConfigurationNode node) {
    config = new Config(node);
  }

  public static Config config() {
    return config;
  }
}
