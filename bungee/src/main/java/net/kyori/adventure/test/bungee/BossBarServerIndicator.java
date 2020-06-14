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

package net.kyori.adventure.test.bungee;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.AdventurePlatform;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A module that will add a boss bar to every user indicating their current server.
 *
 * Because the boss bar exists for the entire lifetime of the player,
 * we don't explicitly have to hide it, unless we want to disable the view
 *
 * This indicator will only be shown to players with the permission {@code adventure-testplugin.bossbar.indicator}
 */
public class BossBarServerIndicator implements Listener {
  private static final BossBar.Color[] COLORS = BossBar.Color.values();

  private final AdventurePlatform adventure;
  private volatile boolean visible = true;
  private final Map<UUID, BossBar> indicators = new ConcurrentHashMap<>();

  static BossBarServerIndicator create(final @NonNull AdventureTestPlugin plugin) {
    final BossBarServerIndicator module = new BossBarServerIndicator(plugin.adventure());
    plugin.getProxy().getPluginManager().registerListener(plugin, module);
    return module;
  }

  private BossBarServerIndicator(final @NonNull AdventurePlatform adventure) {
    this.adventure = adventure;
  }

  public void showAll() {
    this.visible = true;
    for(Map.Entry<UUID, BossBar> entry : this.indicators.entrySet()) {
      this.adventure.player(entry.getKey()).showBossBar(entry.getValue());
    }
  }

  public void hideAll() {
    this.visible = false;
    for(Map.Entry<UUID, BossBar> entry : this.indicators.entrySet()) {
      this.adventure.player(entry.getKey()).hideBossBar(entry.getValue());
    }
  }

  private void updateOrCreateBar(final @NonNull ProxiedPlayer player, final @NonNull String serverName) {
    final BossBar.Color barColor = COLORS[Math.abs(serverName.hashCode() % COLORS.length)];
    final Component nameComponent = TextComponent.builder("You are connected to: ", NamedTextColor.GRAY)
      .append(TextComponent.of(serverName, color(barColor))).build();

    // Create and show bar if necessary (for first join)
    final BossBar bar = this.indicators.computeIfAbsent(player.getUniqueId(), ply -> {
      if(!player.hasPermission(AdventureTestPlugin.permission("indicator.login"))) {
        return null;
      }

      BossBar ret = BossBar.of(nameComponent, 1f, barColor, BossBar.Overlay.NOTCHED_20);
      if(this.visible) {
        this.adventure.player(ply).showBossBar(ret);
      }
      return ret;
    });

    if(bar == null) return;

    // Update data to current server
    bar.name(nameComponent)
      .color(barColor);
  }

  @EventHandler
  public void playerChangedServer(final @NonNull ServerSwitchEvent event) {
    final ProxiedPlayer player = event.getPlayer();
    updateOrCreateBar(player, player.getServer().getInfo().getName());
  }

  @EventHandler
  public void cleanupPlayer(final @NonNull PlayerDisconnectEvent event) {
    // Just remove our tracking -- Adventure will clean up the bar on disconnect
    this.indicators.remove(event.getPlayer().getUniqueId());
  }

  // Given a boss bar colour, find an appropriate text colour
  private static TextColor color(final BossBar.@NonNull Color barColor) {
    switch(barColor) {
      case PINK: return NamedTextColor.LIGHT_PURPLE;
      case BLUE: return NamedTextColor.BLUE;
      case RED: return NamedTextColor.RED;
      case GREEN: return NamedTextColor.GREEN;
      case YELLOW: return NamedTextColor.YELLOW;
      case PURPLE: return NamedTextColor.DARK_PURPLE;
      case WHITE: return NamedTextColor.WHITE;
      default: throw new IllegalArgumentException("Unknown color " + barColor);
    }
  }
}
