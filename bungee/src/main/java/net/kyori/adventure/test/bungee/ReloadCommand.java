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
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ReloadCommand extends Command {
  private final AdventureTestPlugin plugin;

  /* package */ ReloadCommand(final AdventureTestPlugin plugin) {
    super("advreload", AdventureTestPlugin.permission("reload"));
    this.plugin = plugin;
  }

  @Override
  public void execute(final CommandSender sender, final String[] args) {
    final Audience output = this.plugin.adventure().audience(sender);
    if(args.length > 0) {
      output.sendMessage(TextComponent.of("Too many arguments!", AdventureTestPlugin.COLOR_ERROR));
      return;
    }
    this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
      try {
        this.plugin.config().reload();
        output.sendMessage(TextComponent.of("Successfully reloaded configuration!", AdventureTestPlugin.COLOR_RESPONSE));
      } catch(final IOException e) {
        output.sendMessage(TextComponent.of("Failed to reload configuration", AdventureTestPlugin.COLOR_ERROR));
      }
    });
  }
}
