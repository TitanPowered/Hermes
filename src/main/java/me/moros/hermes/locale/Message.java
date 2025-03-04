/*
 * Copyright 2021-2025 Moros
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

package me.moros.hermes.locale;

import java.util.Locale;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

/**
 * @see TranslationManager
 */
public interface Message {
  Locale DEFAULT_LOCALE = Locale.ENGLISH;

  Component PREFIX = text("[", DARK_GRAY)
    .append(text("Hermes", DARK_AQUA))
    .append(text("] ", DARK_GRAY));

  Args0 MSG_CMD_DESC = () -> translatable("hermes.command.msg.desc");
  Args0 REPLY_CMD_DESC = () -> translatable("hermes.command.reply.desc");
  Args0 SPY_CMD_DESC = () -> translatable("hermes.command.spy.desc");

  Args0 NO_RECIPIENT = () -> translatable("hermes.command.reply.no-recipient", RED);

  Args1<String> OFFLINE_RECIPIENT = name -> translatable("hermes.command.reply.offline-recipient", YELLOW)
    .arguments(text(name));

  Args0 SPY_ON = () -> translatable("hermes.command.spy.on", GREEN);

  Args0 SPY_OFF = () -> translatable("hermes.command.spy.off", YELLOW);

  static Component brand(ComponentLike message) {
    return text().append(PREFIX).append(message).build();
  }

  interface Args0 {
    Component build();

    default void send(Audience audience) {
      audience.sendMessage(build());
    }
  }

  interface Args1<A0> {
    Component build(A0 arg0);

    default void send(Audience audience, A0 arg0) {
      audience.sendMessage(build(arg0));
    }
  }
}
