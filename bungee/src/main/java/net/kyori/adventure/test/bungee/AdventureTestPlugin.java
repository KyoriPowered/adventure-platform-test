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

import java.io.IOException;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Entry point for the bungee Adventure test plugin.
 */
public class AdventureTestPlugin extends Plugin implements Listener {

  public static final TextColor COLOR_RESPONSE = TextColor.color(0xAA33CC);
  public static final TextColor COLOR_ERROR = TextColor.color(0xFF0000);
  private static final String ID = "adventure-testplugin";

  private BungeeAudiences adventure;
  private BossBarServerIndicator serverIndicators;
  private Configuration config;

  @Override
  public void onEnable() {
    this.adventure = BungeeAudiences.create(this);
    this.serverIndicators = BossBarServerIndicator.create(this);
    try {
      this.config = Configuration.create(this.getDataFolder().toPath().resolve("config.yml"));
    } catch(final IOException ex) {
      throw new RuntimeException("Unable to load Adventure Test configuration", ex);
    }
    this.getProxy().getPluginManager().registerCommand(this, new ReloadCommand(this));
    this.getProxy().getPluginManager().registerListener(this, this);
  }

  @Override
  public void onDisable() {
    this.adventure.close();
    this.adventure = null;
  }

  public BungeeAudiences adventure() {
    return requireNonNull(this.adventure, "Adventure platform has not yet been initialized");
  }

  public Configuration config() {
    return this.config;
  }

  /* package */ static @NonNull String permission(final @NonNull String base) {
    return ID + "." + base;
  }

  @EventHandler
  public void onProxyPing(final ProxyPingEvent pong) {
    final @Nullable Component motd = this.config.motd();
    if(motd != null) {
      pong.getResponse().setDescriptionComponent(BungeeComponentSerializer.get().serialize(this.config.motd())[0]); // TODO: how to downsample nicely?
    }
  }
}
