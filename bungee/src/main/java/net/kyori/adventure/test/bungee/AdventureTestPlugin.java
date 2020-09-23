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

package net.kyori.adventure.test.bungee;

import net.kyori.adventure.platform.bungeecord.BungeeAdventure;
import net.kyori.adventure.platform.bungeecord.BungeeAudienceProvider;
import net.kyori.adventure.test.AdventureCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class AdventureTestPlugin extends Plugin {
  private BungeeAudienceProvider platform;

  @Override
  public void onEnable() {
    this.platform = BungeeAdventure.of(this);
    this.getProxy().getPluginManager().registerCommand(this, new Command());
  }

  @Override
  public void onDisable() {
    if(this.platform != null) {
      this.platform.close();
      this.platform = null;
    }
  }

  private final class Command extends net.md_5.bungee.api.plugin.Command {
    private Command() {
      super("adventure");
    }

    @Override
    public void execute(final @NonNull CommandSender sender, final String @NonNull[] args) {
      if(AdventureTestPlugin.this.platform != null) {
        new AdventureCommand(args).accept(AdventureTestPlugin.this.platform.sender(sender));
      }
    }
  }
}
