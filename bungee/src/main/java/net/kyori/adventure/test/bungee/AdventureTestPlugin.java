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

import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.Adventure;
import net.kyori.adventure.platform.AdventurePlatform;
import net.md_5.bungee.api.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Entry point for the bungee Adventure test plugin.
 */
public class AdventureTestPlugin extends Plugin {

  private static final String ID = "adventure-testplugin";

  private AdventurePlatform adventure;
  private BossBarServerIndicator serverIndicators;

  @Override
  public void onEnable() {
    this.adventure = Adventure.of(Key.of(ID, "default"));
    serverIndicators = BossBarServerIndicator.create(this);
    // todo: register some commands
  }

  public AdventurePlatform adventure() {
    return this.adventure;
  }

  /* package */ static @NonNull String permission(final @NonNull String base) {
    return ID + "." + base;
  }
}