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

package me.moros.hermes.util;

import java.util.Map;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.command.CommandSender;

import static java.util.Map.entry;

public final class Formatter {
  private static final Map<String, TagResolver> DEFAULT_TAGS = Map.ofEntries(
    entry("color", StandardTags.color()),
    entry("translatable", StandardTags.translatable()),
    entry("decorations", StandardTags.decorations()),
    entry("gradient", StandardTags.gradient()),
    entry("rainbow", StandardTags.rainbow()),
    entry("reset", StandardTags.reset()),
    entry("newline", StandardTags.newline())
  );

  private Formatter() {
  }

  public static Component format(CommandSender sender, String message) {
    TagResolver.Builder resolver = TagResolver.builder();
    boolean hasAllDecorations = false;
    for (var entry : DEFAULT_TAGS.entrySet()) {
      String key = entry.getKey();
      if (sender.hasPermission("hermes.format." + key)) {
        resolver.resolver(entry.getValue());
        if (key.equals("decorations")) {
          hasAllDecorations = true;
        }
      }
    }
    if (!hasAllDecorations) {
      for (var decoration : TextDecoration.values()) {
        if (!sender.hasPermission("hermes.format." + decoration.name())) {
          continue;
        }
        resolver.resolver(StandardTags.decorations(decoration));
      }
    }
    return MiniMessage.builder().tags(resolver.build()).build().deserialize(message);
  }
}
