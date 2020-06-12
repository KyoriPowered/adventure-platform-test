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

package net.kyori.testplugin.paper;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.Adventure;
import net.kyori.adventure.platform.AdventurePlatform;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class AdventureTestPlugin extends JavaPlugin {

  private static final TextColor ERROR_COLOR = TextColor.of(0xff2222);
  private static final TextColor RESPONSE_COLOR = TextColor.of(0x33ac88);
  private static final TextColor BAR_COLOR = TextColor.of(0xcc0044);
  private static final Duration DEF = Duration.of(5, ChronoUnit.SECONDS);

  private AdventurePlatform platform;

  @Override
  public void onEnable() {
    this.platform = Adventure.of(Key.of("adventure-testplugin", "default"));
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
    final Audience result = this.platform.player(((Player) sender).getUniqueId());
    if(args.length < 1) {
      result.sendMessage(TextComponent.of("Subcommand required: countdown|bar|title|version|echo", ERROR_COLOR));
      return false;
    }
    switch(args[0]) {
      case "countdown":
        beginCountdown(TextComponent.of("Until the end", BAR_COLOR), 10, result, viewer -> {
          viewer.sendMessage(TextComponent.of("Countdown complete", RESPONSE_COLOR));
          viewer.sendActionBar(TextComponent.of("Countdown complete", RESPONSE_COLOR));
        });
        break;
      case "bar":
        if(!(sender instanceof Player)) {
          return true;
        }
        result.sendActionBar(TextComponent.of("Test"));
        break;

      case "title":
        if(args.length < 2) {
          result.sendMessage(TextComponent.of("Usage: title <title>", ERROR_COLOR));
          return false;
        }
        final String titleStr = join(args, " ", 1);
        final Component title = GsonComponentSerializer.INSTANCE.deserialize(titleStr);
        result.showTitle(Title.of(title, TextComponent.of("From adventure"), DEF, DEF, DEF));
        break;
      case "version":
        result.sendMessage(TextComponent.make("Adventure platform ", b -> {
          b.append(TextComponent.of(this.platform.getClass().getPackage().getSpecificationVersion(), NamedTextColor.LIGHT_PURPLE));
          b.color(NamedTextColor.DARK_PURPLE);
        }));
        break;
      case "echo":
        final String value = join(args, " ", 1);
        final Component text = GsonComponentSerializer.INSTANCE.deserialize(value);
        result.sendMessage(text);
        break;
      default:
        result.sendMessage(TextComponent.of("Unknown sub-command: " + args[0], ERROR_COLOR));
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
    final BossBar bar = BossBar.of(title, 1, BossBar.Color.RED, BossBar.Overlay.PROGRESS);

    final int timeMs = timeSeconds * 1000; // total time ms
    final long[] times = new long[]{timeMs, System.currentTimeMillis()}; // remaining time in ms, last update time
    getServer().getScheduler().runTaskTimer(this, task -> {
      final long now = System.currentTimeMillis();
      final long dt = now - times[1];
      times[0] -= dt;
      times[1] = now;

      if(times[0] <= 0) { // we are complete
        task.cancel();
        targets.hideBossBar(bar);
        completionAction.accept(targets);
        return;
      }

      final float newFraction = bar.percent() - (dt / (float) timeMs);
      assert newFraction > 0;
      bar.percent(newFraction);
    }, 0, UPDATE_FREQUENCY);
    targets.showBossBar(bar);
  }
}
