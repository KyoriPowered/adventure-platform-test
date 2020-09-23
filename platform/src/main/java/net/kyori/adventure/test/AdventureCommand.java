/*
 * This file is part of adventure-platform-test, licensed under the MIT License.
 *
 * Copyright (c) 2017-2020 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.kyori.adventure.test;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.translation.TranslationRegistry;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static net.kyori.adventure.text.TextComponent.empty;

public final class AdventureCommand implements Consumer<Audience> {
  private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1);
  private static final TranslationRegistry TRANSLATIONS = TranslationRegistry.get();

  static {
    for(final Locale locale : Locale.getAvailableLocales()) {
      TRANSLATIONS.register(locale.toLanguageTag(), locale, new MessageFormat(locale.getDisplayName(locale)));
    }
  }

  private final String command;
  private final String argument;

  public AdventureCommand(final String @NonNull[] arguments) {
    this.command = arguments.length < 1 ? "help" : arguments[0].toLowerCase();
    final StringBuilder builder = new StringBuilder();
    for(int i = 1; i < arguments.length; i++) {
      builder.append(arguments[i]).append(' ');
    }
    this.argument = builder.toString().trim();
  }

  @Override
  public void accept(final @NonNull Audience audience) {
    switch(this.command) {
      case "chat":
        audience.sendMessage(this.createText());
        break;
      case "actionbar":
        audience.sendActionBar(this.createText());
        break;
      case "title":
        audience.showTitle(Title.of(this.createText(), empty()));
        break;
      case "book":
        audience.openBook(Book.of(empty(), empty(), this.createText()));
        break;
      case "bossbar":
        audience.showBossBar(this.createBossBar(audience::hideBossBar));
        break;
      case "sound":
        audience.playSound(this.createSound());
        break;
      default:
        audience.sendMessage(TextComponent.of("/adventure chat|actionbar|title|book|bossbar|sound", TextColor.of(0xff2222)));
        audience.playSound(this.createSound());
        break;
    }
  }

  private @NonNull Component createText() {
    if(this.argument == null) {
      return empty();
    }
    return MiniMessage.markdown().deserialize(this.argument);
  }

  private @NonNull Sound createSound() {
    if(this.argument == null || this.argument.isEmpty() || this.command.equalsIgnoreCase("help")) {
      return Sound.of(Key.of("block.anvil.land"), Sound.Source.MASTER,1f, 0.75f);
    }
    return Sound.of(Key.of(this.argument), Sound.Source.MASTER, 1, 1);
  }

  private @NonNull BossBar createBossBar(final Consumer<BossBar> whenDone) {
    final BossBar bar = BossBar.of(this.createText(), 1, this.randomEnum(BossBar.Color.class), this.randomEnum(BossBar.Overlay.class));
    EXECUTOR.scheduleWithFixedDelay(() -> {
      if(bar.percent() <= 0.01f) {
        whenDone.accept(bar); // TODO: cancel this task somehow
      }
      bar.percent(bar.percent() - 0.01f);
    }, 0, 100, TimeUnit.MILLISECONDS); // Every 2 ticks
    return bar;
  }

  private <E extends Enum<E>> @NonNull E randomEnum(final Class<E> enumClass) {
    final E[] enumList = enumClass.getEnumConstants();
    return enumList[(int) Math.floor(enumList.length * Math.random())];
  }
}
