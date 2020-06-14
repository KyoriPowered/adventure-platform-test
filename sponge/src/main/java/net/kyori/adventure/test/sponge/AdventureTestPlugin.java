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

package net.kyori.adventure.test.sponge;

import com.google.gson.JsonParseException;
import com.google.inject.Inject;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.Adventure;
import net.kyori.adventure.platform.AdventurePlatform;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import static org.spongepowered.api.command.args.GenericArguments.remainingRawJoinedStrings;

@Plugin(id = ProjectData.ID, name = ProjectData.ID, version = ProjectData.VERSION, description = ProjectData.DESCRIPTION)
public class AdventureTestPlugin {
  private final Logger logger;
  private final Game game;
  private final PluginContainer container;
  private AdventurePlatform platform;

  // Setup //

  @Inject
  public AdventureTestPlugin(Logger logger, Game game, PluginContainer container) {
    this.logger = logger;
    this.game = game;
    this.container = container;
  }

  @Listener
  public void preInit(final @NonNull GamePreInitializationEvent event) {
    // request our platform instance
    this.platform = Adventure.of(Key.of(ProjectData.ID, "default"));

    this.game.getCommandManager().register(this, createTestCommand(), "adventure", "adv");

    this.logger.info("{} version {} was successfully loaded", this.container.getName(), this.container.getVersion());
  }

  public @NonNull AdventurePlatform adventure() {
    return this.platform;
  }

  public @NonNull Audience audience(final CommandSource source) {
    if(source instanceof Player) {
      return adventure().player(((Player) source).getUniqueId());
    } else {
      return adventure().console(); // TODO: this is not always correct
    }
  }

  // Event listeners //

  @Listener
  public void playerJoin(final ClientConnectionEvent.@NonNull Join event) {
    final Player joining = event.getTargetEntity();
    final Audience adventure = audience(joining);
    adventure.sendActionBar(TextComponent.make("Welcome to the ", b -> {
      b.append(TextComponent.of("adventure test plugin", NamedTextColor.BLUE))
        .color(NamedTextColor.AQUA);
    }));
  }

  // Commands //

  private CommandSpec createTestCommand() {
    return CommandSpec.builder()
      .description(Text.of("A test command for Adventure"))
      .child(echoCommand(), "echo")
      .child(playSoundCommand(), "sound", "playsound")
      .child(stopSoundCommand(), "stopsound", "silence")
      .child(titleCommand(), "title")
      .child(countdownCommand(), "countdown")
      .build();
  }

  private CommandSpec echoCommand() {
    return CommandSpec.builder()
      .permission(permission("echo"))
      .arguments(remainingRawJoinedStrings(Text.of("message")))
      .executor((src, args) -> {
        final Audience audience = audience(src);
        final String raw = args.<String>getOne("message")
          .orElseThrow(() -> new CommandException(Text.of("No message was provided!")));
       final Component component;

        try {
          component = GsonComponentSerializer.INSTANCE.deserialize(raw);
        } catch(final JsonParseException ex) {
          throw new CommandException(Text.of("Unable to parse JSON"), ex);
        }
        audience.sendMessage(component);

        return CommandResult.success();
      })
      .build();
  }

  private CommandSpec playSoundCommand() {
    return CommandSpec.builder()
      .permission(permission("sound.play"))
      .description(Text.of("Play a sound"))
      .executor((src, ctx) -> {
        return CommandResult.empty();
      })
      .build();
  }

  private CommandSpec stopSoundCommand() {
    return CommandSpec.builder()
      .permission(permission("sound.stop"))
      .description(Text.of("Stop a sound"))
      .executor((src, ctx) -> {
        return CommandResult.empty();
      })
      .build();
  }

  private CommandSpec titleCommand() {
    return CommandSpec.builder()
      .permission(permission("title"))
      .description(Text.of("Show a title to the sender"))
      .executor((src, ctx) -> {
        return CommandResult.empty();
      })
      .build();
  }

  private CommandSpec countdownCommand() {
    return CommandSpec.builder()
      .permission(permission("countdown"))
      .description(Text.of("Show a boss bar"))
      .executor((src, ctx) -> {
        return CommandResult.empty();
      })
      .build();
  }

  /**
   * Get a permission within the plugin's namespace
   *
   * @param permission local permission key
   * @return the permission
   */
  private static String permission(final @NonNull String permission) {
    return ProjectData.ID + '.' + permission;
  }
}
