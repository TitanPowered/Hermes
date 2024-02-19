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

package me.moros.hermes.command;

import me.moros.hermes.Hermes;
import me.moros.hermes.locale.Message;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.paper.PaperCommandManager;

public final class Commander {
  private final Hermes plugin;
  private final PaperCommandManager<CommandSender> manager;

  private Commander(Hermes plugin) {
    this.plugin = plugin;
    this.manager = PaperCommandManager.createNative(plugin, ExecutionCoordinator.simpleCoordinator());
    this.manager.registerBrigadier();
    MinecraftExceptionHandler.<CommandSender>createNative().defaultHandlers().decorator(Message::brand)
      .registerTo(manager);
    HermesCommand.register(this);
    MsgCommand.register(this);
    ReplyCommand.register(this);
  }

  public Hermes plugin() {
    return plugin;
  }

  public CommandManager<CommandSender> manager() {
    return manager;
  }

  public static Commander create(Hermes plugin) {
    return new Commander(plugin);
  }
}
