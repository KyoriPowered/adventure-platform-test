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

package net.kyori.adventure.test.paper;

import net.kyori.adventure.platform.bukkit.BukkitAdventure;
import net.kyori.adventure.platform.bukkit.BukkitAudienceProvider;
import net.kyori.adventure.test.AdventureCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class AdventureTestPlugin extends JavaPlugin {
  private BukkitAudienceProvider platform;

  @Override
  public void onEnable() {
    this.platform = BukkitAdventure.of(this);
  }

  @Override
  public void onDisable() {
    if(this.platform != null) {
      this.platform.close();
      this.platform = null;
    }
  }

  @Override
  public boolean onCommand(final @NonNull CommandSender sender, final @NonNull Command command, final @NonNull String label, final @NonNull String @NonNull [] args) {
    if(this.platform == null) {
      return false;
    }
    new AdventureCommand(args).accept(this.platform.sender(sender));
    return true;
  }
}
