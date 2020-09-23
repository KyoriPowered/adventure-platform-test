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

package net.kyori.adventure.test.sponge;

import com.google.inject.Inject;
import net.kyori.adventure.platform.spongeapi.SpongeAudienceProvider;
import net.kyori.adventure.test.AdventureCommand;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import static org.spongepowered.api.command.args.GenericArguments.remainingRawJoinedStrings;

@Plugin(id = ProjectData.ID, name = ProjectData.ID, version = ProjectData.VERSION, description = ProjectData.DESCRIPTION)
public final class AdventureTestPlugin {
  private final Game game;
  private final SpongeAudienceProvider platform;

  @Inject
  public AdventureTestPlugin(final @NonNull Game game, final @NonNull SpongeAudienceProvider platform) {
    this.game = game;
    this.platform = platform;
  }

  @Listener
  public void onInit(final @NonNull GamePreInitializationEvent event) {
    this.game.getCommandManager().register(this, CommandSpec.builder()
      .permission("adventure.test")
      .description(Text.of("A test command for Adventure"))
      .arguments(remainingRawJoinedStrings(Text.of("args")))
      .executor((src, args) -> CommandResult.success())
      .build(), "adventure");
  }

  @Listener(order = Order.FIRST)
  public void onCommand(final @NonNull SendCommandEvent event, final @First CommandSource sender) {
    if(event.getCommand().equalsIgnoreCase("adventure")) {
      new AdventureCommand(event.getArguments().split(" ")).accept(this.platform.receiver(sender));
      event.setResult(CommandResult.success());
    }
  }
}
