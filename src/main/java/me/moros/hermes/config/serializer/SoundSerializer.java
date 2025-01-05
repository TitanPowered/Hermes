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

package me.moros.hermes.config.serializer;

import java.lang.reflect.Type;
import java.util.OptionalLong;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

@SuppressWarnings("PatternValidation")
final class SoundSerializer implements TypeSerializer<Sound> {
  static final SoundSerializer INSTANCE = new SoundSerializer();

  static final String NAME = "name";
  static final String SOURCE = "source";
  static final String PITCH = "pitch";
  static final String VOLUME = "volume";
  static final String SEED = "seed";

  private SoundSerializer() {
  }

  @Override
  public @Nullable Sound deserialize(Type type, ConfigurationNode value) throws SerializationException {
    if (value.empty()) {
      return null;
    }
    String rawName = value.node(NAME).getString();
    Key name = Key.parseable(rawName) ? Key.key(rawName) : null;
    Sound.Source source = value.node(SOURCE).get(Sound.Source.class);
    if (name == null || source == null) {
      throw new SerializationException("A name and source are required to deserialize a Sound");
    }
    var builder = Sound.sound().type(name).source(source)
      .volume(value.node(VOLUME).getFloat(1.0f)).pitch(value.node(PITCH).getFloat(1.0f));
    ConfigurationNode seed = value.node(SEED);
    if (!seed.virtual()) {
      builder.seed(OptionalLong.of(seed.getLong()));
    }
    return builder.build();
  }

  @Override
  public void serialize(Type type, @Nullable Sound obj, ConfigurationNode value) throws SerializationException {
    if (obj == null) {
      value.raw(null);
      return;
    }
    value.node(NAME).set(obj.name().asString());
    value.node(SOURCE).set(Sound.Source.class, obj.source());
    value.node(VOLUME).set(obj.volume());
    value.node(PITCH).set(obj.pitch());
    if (obj.seed().isPresent()) {
      value.node(SEED).set(obj.seed().getAsLong());
    } else {
      value.node(SEED).raw(null);
    }
  }
}
