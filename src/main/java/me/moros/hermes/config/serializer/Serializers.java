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

package me.moros.hermes.config.serializer;

import net.kyori.adventure.sound.Sound;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public final class Serializers {
  public static final TypeSerializerCollection ALL = TypeSerializerCollection.builder()
    .register(Sound.class, SoundSerializer.INSTANCE)
    .build();

  private Serializers() {
  }
}
