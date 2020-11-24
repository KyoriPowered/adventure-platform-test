/*
 * This file is part of adventure-testplugin, licensed under the MIT License.
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
package net.kyori.adventure.test.bukkit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;

import static java.util.Objects.requireNonNull;

public class AdventureTestPlugin extends JavaPlugin {

  private static final TextColor ERROR_COLOR = TextColor.color(0xff2222);
  private static final TextColor RESPONSE_COLOR = TextColor.color(0x33ac88);
  private static final TextColor BAR_COLOR = TextColor.color(0xcc0044);
  private static final Duration DEF = Duration.of(5, ChronoUnit.SECONDS);
  private static final Title.Times DEFAULT_TIME = Title.Times.of(DEF, DEF, DEF);
  private static final BossBar NOTIFICATION = BossBar.bossBar(Component.text("Welcome!", NamedTextColor.AQUA), .3f /* to see 1.8 Wither shimmer */, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);

  private BukkitAudiences platform;

  @Override
  public void onEnable() {
    this.platform = BukkitAudiences.create(this);
  }

  @Override
  public void onDisable() {
    if(this.platform != null) {
      this.platform.close();
      this.platform = null;
    }
  }

  public ComponentSerializer<Component, ? extends Component, String> serializer() {
    return MiniMessage.markdown();
  }

  public BukkitAudiences adventure() {
    return requireNonNull(this.platform, "Adventure platform not yet initialized");
  }

  /**
   * Join the elements of an array together into a string.
   *
   * @param elements elements to join
   * @param separator separator to use between strings
   * @param startIdx index in the array to join from
   * @return the string
   */
  private static String join(final @NonNull String @NonNull [] elements, final @NonNull String separator, final int startIdx) {
    final StringBuilder ret = new StringBuilder();
    for(int i = startIdx; i < elements.length; ++i) {
      if(i != startIdx) {
        ret.append(separator);
      }
      ret.append(elements[i]);
    }
    return ret.toString();
  }

  @Override
  public boolean onCommand(final @NonNull CommandSender sender, final @NonNull Command command, final @NonNull String label, final @NonNull String @NonNull [] args) {
    final Audience result = this.adventure().sender(sender);
    if(args.length < 1) {
      result.sendMessage(Component.text("Subcommand required: countdown|bar|title|version|echo|sound", ERROR_COLOR));
      return false;
    }
    switch(args[0]) {
      case "countdown":
        this.beginCountdown(Component.text("Until the end", BAR_COLOR), 10, result, viewer -> {
          viewer.sendMessage(Component.text("Countdown complete", RESPONSE_COLOR));
          viewer.sendActionBar(Component.text("Countdown complete", RESPONSE_COLOR));
        });
        break;
      case "bar":
        if(!(sender instanceof Player)) {
          return true;
        }
        result.sendActionBar(Component.text("Test"));
        break;

      case "title":
        if(args.length < 2) {
          result.sendMessage(Component.text("Usage: title <title>", ERROR_COLOR));
          return false;
        }
        final String titleStr = join(args, " ", 1);
        final Component title = this.serializer().deserialize(titleStr);
        result.showTitle(Title.title(title, Component.text("From adventure"), DEFAULT_TIME));
        break;
      case "version":
        result.sendMessage(Component.text(b -> {
          b.content("Adventure platform")
            .append(Component.text(this.platform.getClass().getPackage().getSpecificationVersion(), NamedTextColor.LIGHT_PURPLE))
            .color(NamedTextColor.DARK_PURPLE);
        }));
        break;
      case "echo":
        final String value = join(args, " ", 1);
        final Component text = this.serializer().deserialize(value);
        result.sendMessage(text);
        break;
      case "baron":
        result.showBossBar(NOTIFICATION);
        break;
      case "baroff":
        result.hideBossBar(NOTIFICATION);
        break;
      case "sound":
        if(args.length < 2) {
          result.sendMessage(Component.text("Not enough args! Usage: /adventure sound <id> [source]", ERROR_COLOR));
          return true;
        }
        Sound.Source source = Sound.Source.AMBIENT;
        if(args.length >= 3) {
          source = Sound.Source.NAMES.value(args[2]);
          if(source == null) {
            result.sendMessage(Component.text("Unknown source: ", ERROR_COLOR).append(Component.text(args[2], Style.style(TextDecoration.ITALIC))));
            return true;
          }
        }
        result.playSound(Sound.sound(Key.key(args[1]), source, 1f, 1f));
        break;
      case "stopsound":
        result.stopSound(SoundStop.all());
        break;
      case "book":
        result.openBook(Book.builder()
        .title(Component.empty())
        .author(Component.empty())
        .pages(Component.text("Welcome to Adventure!", RESPONSE_COLOR),
          Component.text("This is a book to look at!", TextColor.color(0x8844bb)))
        .build());
        break;
      default:
        result.sendMessage(Component.text("Unknown sub-command: " + args[0], ERROR_COLOR));
        return false;
    }
    return true;
  }

  /**
   * Boss bar animation update frequency, in ticks
   */
  private static final int UPDATE_FREQUENCY = 2;

  /**
   * Begin a countdown shown on a boss bar, completing with the specified action
   *
   * @param title Boss bar title
   * @param timeSeconds seconds boss bar will last
   * @param targets viewers of the action
   * @param completionAction callback to execute when countdown is complete
   */
  private void beginCountdown(final @NonNull Component title, final int timeSeconds, final @NonNull Audience targets, final @NonNull Consumer<Audience> completionAction) {
    final BossBar bar = BossBar.bossBar(title, 1, BossBar.Color.RED, BossBar.Overlay.PROGRESS);

    final int timeMs = timeSeconds * 1000; // total time ms
    final long[] times = new long[]{timeMs, System.currentTimeMillis()}; // remaining time in ms, last update time
    final BukkitRunnable run = new BukkitRunnable() {
      @Override
      public void run() {
        final long now = System.currentTimeMillis();
        final long dt = now - times[1];
        times[0] -= dt;
        times[1] = now;

        if(times[0] <= 0) { // we are complete
          this.cancel();
          targets.hideBossBar(bar);
          completionAction.accept(targets);
          return;
        }

        final float newFraction = bar.progress() - (dt / (float) timeMs);
        assert newFraction > 0;
        bar.progress(newFraction);
      }
    };
    run.runTaskTimer(this, 0, UPDATE_FREQUENCY);
    targets.showBossBar(bar);
  }
}
