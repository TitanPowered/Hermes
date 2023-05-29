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

import me.moros.hermes.command.CommandManager;
import me.moros.hermes.config.ConfigManager;
import me.moros.hermes.listener.HermesListener;
import me.moros.hermes.locale.TranslationManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

public class Hermes extends JavaPlugin {
  private String author;
  private String version;

  private Herald herald;

  private ConfigManager configManager;
  private TranslationManager translationManager;

  @Override
  public void onEnable() {
    author = getPluginMeta().getAuthors().get(0);
    version = getPluginMeta().getVersion();

    Logger logger = getSLF4JLogger();
    String dir = getDataFolder().toString();
    configManager = new ConfigManager(logger, dir);
    translationManager = new TranslationManager(logger, dir);

    herald = new Herald();
    try {
      new CommandManager(this);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      getServer().getPluginManager().disablePlugin(this);
      return;
    }
    getServer().getPluginManager().registerEvents(new HermesListener(), this);
  }

  @Override
  public void onDisable() {
    configManager.close();
  }

  public String author() {
    return author;
  }

  public String version() {
    return version;
  }

  public Herald herald() {
    return herald;
  }

  public TranslationManager translationManager() {
    return translationManager;
  }
}
