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

import me.moros.hermes.command.CommandManager;
import me.moros.hermes.config.ConfigManager;
import me.moros.hermes.listener.HermesListener;
import me.moros.hermes.locale.TranslationManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hermes extends JavaPlugin {
  private static Hermes plugin;

  private String author;
  private String version;

  private Logger logger;
  private Herald herald;

  private ConfigManager configManager;
  private TranslationManager translationManager;

  @Override
  public void onEnable() {
    plugin = this;
    author = getDescription().getAuthors().get(0);
    version = getDescription().getVersion();
    logger = LoggerFactory.getLogger(getClass().getSimpleName());
    herald = new Herald();

    String dir = plugin.getDataFolder().toString();
    configManager = new ConfigManager(dir);
    translationManager = new TranslationManager(dir);
    try {
      new CommandManager(this);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      getServer().getPluginManager().disablePlugin(this);
    }
    getServer().getPluginManager().registerEvents(new HermesListener(), this);
  }

  public static @MonotonicNonNull String author() {
    return plugin.author;
  }

  public static @MonotonicNonNull String version() {
    return plugin.version;
  }

  public static @MonotonicNonNull Logger logger() {
    return plugin.logger;
  }

  public static @MonotonicNonNull Herald herald() {
    return plugin.herald;
  }

  public static @MonotonicNonNull ConfigManager configManager() {
    return plugin.configManager;
  }

  public static @MonotonicNonNull TranslationManager translationManager() {
    return plugin.translationManager;
  }
}
