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

package me.moros.hermes.config;

import me.clip.placeholderapi.PlaceholderAPI;
import me.moros.hermes.HermesUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class Configurable {
  protected Component parse(String text) {
    return HermesUtil.MINI_SERIALIZER.deserialize(text);
  }

  protected Component parsePlaceholder(String text, @Nullable Player player) {
    return parsePlaceholder(text, player, 0);
  }

  protected Component parsePlaceholder(String text, @Nullable Player player, int space) {
    var builder = TagResolver.builder()
      .resolver(TagResolver.resolver("papi", (args, ctx) -> papiTag(player, args, space)))
      .resolver(Placeholder.component("online", Component.text(Bukkit.getOnlinePlayers().size())));
    if (player != null) {
      builder.resolver(Placeholder.component("name", player.name()));
    }
    return HermesUtil.MINI_SERIALIZER.deserialize(text, builder.build());
  }

  private Tag papiTag(@Nullable Player player, ArgumentQueue args, int space) {
    String placeholder = args.popOr("Missing placeholder id argument!").value();
    String s = PlaceholderAPI.setPlaceholders(player, '%' + placeholder + '%');
    if (s.isBlank()) {
      return Tag.selfClosingInserting(Component.empty());
    }
    Component result = HermesUtil.SERIALIZER.deserialize(s);
    if (space < 0) {
      result = result.append(Component.space());
    } else if (space > 0) {
      result = Component.space().append(result);
    }
    return Tag.selfClosingInserting(result);
  }
}
