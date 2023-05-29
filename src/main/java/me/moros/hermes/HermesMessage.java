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

import me.moros.hermes.config.Config;
import net.kyori.adventure.text.Component;

import static net.kyori.adventure.text.Component.text;

public final class HermesMessage {
  private final Component normal;
  private final Component self;
  private final Component spy;

  private HermesMessage(Component normal, Component self, Component spy) {
    this.normal = normal;
    this.self = self;
    this.spy = spy;
  }

  public Component normal() {
    return normal;
  }

  public Component self() {
    return self;
  }

  public Component spy() {
    return spy;
  }

  public static HermesMessage build(Config config, User sender, User receiver, String content) {
    Component s = config.nameFormat(sender.player());
    Component r;
    if (sender.uuid().equals(receiver.uuid())) {
      r = s;
    } else {
      r = config.nameFormat(receiver.player());
    }
    Component msg = Formatter.createAndFormat(sender.player(), HermesUtil.SERIALIZER.deserialize(content));

    Component msgPrefix = config.msgPrefix();
    Component spyMsgPrefix = config.spyMsgPrefix();

    Component prepared = text().append(s).append(config.msgJoiner()).append(r)
      .append(config.separator()).append(msg).color(HermesUtil.BASE_COLOR).build();
    Component preparedSelf = text().append(s).append(config.msgJoiner()).append(config.selfMsg())
      .append(config.separator()).append(msg).color(HermesUtil.BASE_COLOR).build();

    return new HermesMessage(msgPrefix.append(prepared), msgPrefix.append(preparedSelf), spyMsgPrefix.append(prepared));
  }
}
