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

package me.moros.hermes.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import me.moros.hermes.Hermes;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

public final class ConfigManager {
  private final HoconConfigurationLoader loader;

  private CommentedConfigurationNode configRoot;
  private Config config;

  public ConfigManager(@NonNull String directory) {
    Path path = Paths.get(directory, "hermes.conf");
    loader = HoconConfigurationLoader.builder().path(path).build();
    try {
      Files.createDirectories(path.getParent());
      configRoot = loader.load();
      config = new Config(configRoot);
      loader.save(configRoot);
    } catch (IOException e) {
      Hermes.logger().warn(e.getMessage(), e);
    }
  }

  public void reload() {
    try {
      configRoot = loader.load();
      config = new Config(configRoot);
    } catch (IOException e) {
      Hermes.logger().warn(e.getMessage(), e);
    }
  }

  public @NonNull Config config() {
    return config;
  }
}
