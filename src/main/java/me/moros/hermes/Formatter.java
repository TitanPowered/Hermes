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

import java.util.Objects;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.Style.Merge;
import net.kyori.adventure.text.format.Style.Merge.Strategy;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.bukkit.command.CommandSender;

public final class Formatter {
  private final Style style;
  private final boolean allowRgb;

  private Formatter(CommandSender sender) {
    allowRgb = sender.hasPermission("hermes.format.rgb");
    style = Style.style(sb -> {
      for (TextDecoration d : TextDecoration.values()) {
        if (!sender.hasPermission("hermes.format." + d.toString())) {
          sb.decoration(d, State.FALSE);
        }
      }
    });
  }

  private TextComponent apply(TextComponent buildableComponent) {
    Style.Builder forceStyle = buildableComponent.style().toBuilder()
      .merge(style, Strategy.ALWAYS, Merge.DECORATIONS);
    if (!allowRgb) {
      forceStyle.color(null);
    }
    return buildableComponent.style(forceStyle);
  }

  public static Component createAndFormat(CommandSender sender, TextComponent msg) {
    Objects.requireNonNull(sender);
    Objects.requireNonNull(msg);
    Formatter formatter = new Formatter(sender);
    return msg.toBuilder().mapChildrenDeep(c -> formatter.apply((TextComponent) c)).build();
  }
}
