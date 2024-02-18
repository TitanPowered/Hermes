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

import io.papermc.paper.plugin.configuration.PluginMeta;
import me.moros.hermes.command.Commander;
import me.moros.hermes.config.ConfigManager;
import me.moros.hermes.listener.HermesListener;
import me.moros.hermes.locale.TranslationManager;
import me.moros.hermes.util.Herald;
import me.moros.hermes.util.HermesUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.spongepowered.configurate.reference.WatchServiceListener;

public class Hermes extends JavaPlugin {
  private final Logger logger;
  private final String author;
  private final String version;

  private final Herald herald;

  private final WatchServiceListener listener;
  private final ConfigManager configManager;
  private final TranslationManager translationManager;

  public Hermes(Logger logger, Path dir, PluginMeta meta) {
    this.logger = logger;
    this.author = meta.getAuthors().get(0);
    this.version = meta.getVersion();

    this.herald = Herald.create();

    try {
      this.listener = WatchServiceListener.create();
      this.configManager = new ConfigManager(logger, dir, listener);
      this.translationManager = new TranslationManager(logger, dir, listener);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void onEnable() {
    Commander.create(this);
    configManager.save();
    configManager.subscribe(this::syncRefreshHeaders);
    getServer().getPluginManager().registerEvents(new HermesListener(), this);
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

  public String author() {
    return author;
  }

  public String version() {
    return version;
  }

  public Herald herald() {
    return herald;
  }

  private void syncRefreshHeaders() {
    getServer().getGlobalRegionScheduler().execute(this, () -> {
      HermesUtil.refreshHeaderFooter();
      Bukkit.getOnlinePlayers().forEach(HermesUtil::refreshListName);
    });
  }
}
