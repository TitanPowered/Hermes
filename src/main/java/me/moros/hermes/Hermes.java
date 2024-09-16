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

package me.moros.hermes;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

import me.moros.hermes.command.Commander;
import me.moros.hermes.config.ConfigManager;
import me.moros.hermes.listener.HermesListener;
import me.moros.hermes.locale.TranslationManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.spongepowered.configurate.reference.WatchServiceListener;

public class Hermes extends JavaPlugin {
  private final Logger logger;

  private final WatchServiceListener listener;
  private final ConfigManager configManager;
  private final TranslationManager translationManager;

  private final Herald herald;

  private final Commander commander;

  public Hermes(Logger logger, Path dir, Commander commander) {
    this.logger = logger;
    this.commander = commander;

    try {
      this.listener = WatchServiceListener.create();
      this.configManager = new ConfigManager(logger, dir, listener);
      this.translationManager = new TranslationManager(logger, dir, listener);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    this.herald = Herald.create(configManager);
    this.commander.injectHerald(herald);
  }

  @Override
  public void onEnable() {
    new PermissionInitializer();
    getServer().getPluginManager().registerEvents(new HermesListener(herald), this);
  }

  @Override
  public void onDisable() {
    configManager.close();
    try {
      listener.close();
    } catch (IOException e) {
      logger.warn(e.getMessage(), e);
    }
  }
}
