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

import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;

import static net.kyori.adventure.text.Component.text;

public final class HermesMessage {
  private final Component prepared;
  private final Component preparedSelf;

  private HermesMessage(Component sender, Component receiver, Component content) {
    var config = Hermes.configManager().config();
    this.prepared = text().append(sender).append(config.msgJoiner()).append(receiver)
      .append(config.separator()).append(content).color(HermesUtil.BASE_COLOR).build();
    this.preparedSelf = text().append(sender).append(config.msgJoiner()).append(config.selfMsg())
      .append(config.separator()).append(content).color(HermesUtil.BASE_COLOR).build();
  }

  public @NonNull Component normal() {
    return Hermes.configManager().config().msgPrefix().append(prepared);
  }

  public @NonNull Component self() {
    return Hermes.configManager().config().msgPrefix().append(preparedSelf);
  }

  public @NonNull Component spy() {
    return Hermes.configManager().config().spyMsgPrefix().append(prepared);
  }

  public static @NonNull HermesMessage build(@NonNull User sender, @NonNull User receiver, @NonNull String content) {
    Component s = Hermes.configManager().config().nameFormat(sender.player());
    Component r;
    if (sender.uuid().equals(receiver.uuid())) {
      r = s;
    } else {
      r = Hermes.configManager().config().nameFormat(receiver.player());
    }
    Component msg = Formatter.createAndFormat(sender.player(), HermesUtil.SERIALIZER.deserialize(content));
    return new HermesMessage(s, r, msg);
  }
}
