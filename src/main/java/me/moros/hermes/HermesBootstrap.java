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

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import me.moros.hermes.command.Commander;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class HermesBootstrap implements PluginBootstrap {
  private Commander commander;

  @Override
  public void bootstrap(@NotNull BootstrapContext context) {
    this.commander = Commander.create(context);
  }

  @Override
  public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
    return new Hermes(context.getLogger(), context.getDataDirectory(), commander);
  }
}
