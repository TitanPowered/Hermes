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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.moros.hermes.util.Debounced;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationStore;
import net.kyori.adventure.translation.Translator;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.slf4j.Logger;
import org.spongepowered.configurate.reference.WatchServiceListener;

/**
 * TranslationManager loads localized strings and adds them to a {@link TranslationStore} that can be used
 * to create {@link TranslatableComponent}.
 * @see Message
 */
public final class TranslationManager {
  private static final String PATH = "hermes.lang.messages_en";

  private final Logger logger;
  private final Path translationsDirectory;
  private final AtomicReference<TranslationStore.StringBased<MessageFormat>> registryReference;
  private final Debounced<?> buffer;

  public TranslationManager(Logger logger, Path directory, WatchServiceListener listener) throws IOException {
    this.logger = logger;
    this.translationsDirectory = Files.createDirectories(directory.resolve("translations"));
    var registry = createRegistry(new HashSet<>());
    this.registryReference = new AtomicReference<>(registry);
    GlobalTranslator.translator().addSource(registry);
    this.buffer = Debounced.create(this::reload, 2, TimeUnit.SECONDS);
    listener.listenToDirectory(translationsDirectory, e -> buffer.request());
  }

  private void reload() {
    Set<Locale> localeSet = new LinkedHashSet<>();
    var newRegistry = createRegistry(localeSet);
    var old = registryReference.getAndSet(newRegistry);
    GlobalTranslator.translator().removeSource(old);
    GlobalTranslator.translator().addSource(newRegistry);
    int amount = localeSet.size();
    if (amount > 0) {
      String translations = localeSet.stream().map(Locale::getLanguage)
        .collect(Collectors.joining(", ", "[", "]"));
      logger.info(String.format("Loaded %d translations: %s", amount, translations));
    }
  }

  private TranslationStore.StringBased<MessageFormat> createRegistry(Set<Locale> localeSet) {
    var registry = TranslationStore.messageFormat(Key.key("hermes", "translations"));
    registry.defaultLocale(Message.DEFAULT_LOCALE);
    loadCustom(registry, localeSet);
    loadDefaults(registry);
    return registry;
  }

  private void loadDefaults(TranslationStore.StringBased<MessageFormat> registry) {
    ResourceBundle bundle = ResourceBundle.getBundle(PATH, Message.DEFAULT_LOCALE, UTF8ResourceBundleControl.get());
    registry.registerAll(Message.DEFAULT_LOCALE, bundle, false);
  }

  private void loadCustom(TranslationStore.StringBased<MessageFormat> registry, Set<Locale> localeSet) {
    Collection<Path> paths;
    try (Stream<Path> stream = Files.list(translationsDirectory)) {
      paths = stream.filter(this::isValidPropertyFile).toList();
    } catch (IOException e) {
      paths = List.of();
    }
    for (Path path : paths) {
      loadTranslationFile(path, (locale, bundle) -> {
        registry.registerAll(locale, bundle, false);
        localeSet.add(locale);
      });
    }
  }

  private void loadTranslationFile(Path path, BiConsumer<Locale, PropertyResourceBundle> consumer) {
    String localeString = removeFileExtension(path);
    Locale locale = Translator.parseLocale(localeString);
    if (locale == null) {
      logger.warn("Unknown locale: " + localeString);
      return;
    }
    PropertyResourceBundle bundle;
    try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      bundle = new PropertyResourceBundle(reader);
    } catch (IOException e) {
      logger.warn("Error loading locale file: " + localeString);
      return;
    }
    consumer.accept(locale, bundle);
  }

  private boolean isValidPropertyFile(Path path) {
    return Files.isRegularFile(path) && path.getFileName().toString().endsWith(".properties");
  }

  private String removeFileExtension(Path path) {
    String fileName = path.getFileName().toString();
    return fileName.substring(0, fileName.length() - ".properties".length());
  }
}
