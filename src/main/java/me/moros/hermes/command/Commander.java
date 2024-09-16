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

import java.util.concurrent.atomic.AtomicReference;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import me.moros.hermes.Herald;
import me.moros.hermes.locale.Message;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper;
import org.incendo.cloud.paper.util.sender.Source;

public final class Commander {
  private final AtomicReference<Herald> heraldRef;
  private final PaperCommandManager<Source> manager;

  private Commander(BootstrapContext context) {
    this.heraldRef = new AtomicReference<>();
    this.manager = PaperCommandManager.builder(PaperSimpleSenderMapper.simpleSenderMapper())
      .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
      .buildBootstrapped(context);
    MinecraftExceptionHandler.create(Source::source).defaultHandlers().decorator(Message::brand).registerTo(manager);

    HermesCommand.register(this);
  }

  public boolean injectHerald(Herald herald) {
    return heraldRef.compareAndSet(null, herald);
  }

  public CommandManager<Source> manager() {
    return manager;
  }

  public Herald herald() {
    return heraldRef.get();
  }

  public static Commander create(BootstrapContext context) {
    return new Commander(context);
  }
}
