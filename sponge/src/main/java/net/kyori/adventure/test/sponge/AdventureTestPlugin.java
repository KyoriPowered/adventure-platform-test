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

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.permission.PermissionChecker;
import net.kyori.adventure.platform.spongeapi.SpongeAudiences;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.parser.ParsingException;
import net.kyori.adventure.title.Title;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
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
  private final SpongeAudiences adventure;

  // Setup //

  @Inject
  public AdventureTestPlugin(final @NonNull Logger logger, final @NonNull Game game, final @NonNull PluginContainer container, final @NonNull SpongeAudiences adventure) {
    this.logger = logger;
    this.game = game;
    this.container = container;
    this.adventure = adventure;
  }

  @Listener
  public void preInit(final @NonNull GamePreInitializationEvent event) {
    // request our platform instance
    this.game.getCommandManager().register(this, this.createTestCommand(), "adventure", "adv");

    this.logger.info("{} version {} was successfully loaded", this.container.getName(), this.container.getVersion().orElse("unknown"));
  }

  public @NonNull SpongeAudiences adventure() {
    return this.adventure;
  }

  // Event listeners //

  @Listener
  public void playerJoin(final ClientConnectionEvent.@NonNull Join event) {
    final Player joining = event.getTargetEntity();
    final Audience adventure = this.adventure().receiver(joining);
    adventure.sendActionBar(Component.text(b -> {
      b.content("Welcome to the ")
      .append(Component.text("adventure test plugin", NamedTextColor.BLUE))
        .color(NamedTextColor.AQUA);
    }));
  }

  // Commands //

  private CommandSpec createTestCommand() {
    return CommandSpec.builder()
      .description(Text.of("A test command for Adventure"))
      .child(this.echoCommand(), "echo")
      .child(this.playSoundCommand(), "sound", "playsound")
      .child(this.stopSoundCommand(), "stopsound", "silence")
      .child(this.titleCommand(), "title")
      .child(this.pointersCommand(), "pointers", "ptr")
      .build();
  }

  private CommandSpec echoCommand() {
    return CommandSpec.builder()
      .permission(permission("echo"))
      .arguments(remainingRawJoinedStrings(Text.of("message")))
      .executor((src, args) -> {
        final Audience audience = this.adventure().receiver(src);
        final String raw = args.<String>getOne("message")
          .orElseThrow(() -> new CommandException(Text.of("No message was provided!")));
        final Component component;

        try {
          component = MiniMessage.get().parse(raw);
        } catch(final ParsingException ex) {
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
      .arguments(GenericArguments.enumValue(Text.of("source"), Sound.Source.class), keyArg(Text.of("sound")))
      .executor((src, ctx) -> {
        final Audience audience = this.adventure().receiver(src);
        final Sound.Source source = ctx.requireOne("source");
        final Key sound = ctx.requireOne("sound");
        audience.playSound(Sound.sound(sound, source, 1f, 1f));
        return CommandResult.success();
      })
      .build();
  }

  private CommandSpec stopSoundCommand() {
    return CommandSpec.builder()
      .permission(permission("sound.stop"))
      .description(Text.of("Stop a sound"))
      .executor((src, ctx) -> {
        this.adventure.receiver(src).stopSound(SoundStop.all());
        return CommandResult.empty();
      })
      .build();
  }

  private CommandSpec titleCommand() {
    return CommandSpec.builder()
      .permission(permission("title"))
      .description(Text.of("Show a title to the sender"))
      .arguments(remainingRawJoinedStrings(Text.of("header")))
      .executor((src, ctx) -> {
        final Audience audience = this.adventure().receiver(src);
        final String raw = ctx.<String>getOne("message")
          .orElseThrow(() -> new CommandException(Text.of("No message was provided!")));
        final Component component;

        try {
          component = MiniMessage.get().parse(raw);
        } catch(final ParsingException ex) {
          throw new CommandException(Text.of("Unable to parse JSON"), ex);
        }
        audience.showTitle(Title.title(
          component,
          Component.text("from adventure", NamedTextColor.AQUA, TextDecoration.ITALIC)
        ));
        return CommandResult.empty();
      })
      .build();
  }

  private CommandSpec pointersCommand() {
    return CommandSpec.builder()
      .permission(permission("pointers"))
      .description(Text.of("Show a selection of relevant pointers"))
      .executor((src, ctx) -> {
        final Audience result = this.adventure().receiver(src);
        Stream.of(
            Identity.LOCALE,
            Identity.DISPLAY_NAME,
            Identity.NAME,
            Identity.UUID,
            PermissionChecker.POINTER
          )
          .map(pointer -> Maps.immutableEntry(pointer, result.get(pointer)))
          .map(pointerValue -> {
            final net.kyori.adventure.text.TextComponent.Builder output = Component.text().content("- ");
            output.append(Component.text(pointerValue.getKey().key().toString(), NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD));
            output.append(Component.text(": "));
            if (pointerValue.getValue().isPresent()) {
              final Object unwrapped = pointerValue.getValue().get();
              if (unwrapped instanceof ComponentLike) {
                output.append((ComponentLike) unwrapped);
              } else {
                output.append(Component.text(unwrapped.toString()));
              }
            } else {
              output.append(Component.text("<absent>", NamedTextColor.GRAY));
            }
            return output.build();
          }).forEach(result::sendMessage);
        return CommandResult.empty();
      })
      .build();
  }

  /**
   * Get a permission within the plugin's namespace.
   *
   * @param permission local permission key
   * @return the permission
   */
  private static String permission(final @NonNull String permission) {
    return ProjectData.ID + '.' + permission;
  }

  private static KeyElement keyArg(final Text id) {
    return new KeyElement(id);
  }

  static class KeyElement extends CommandElement {

    KeyElement(@Nullable final Text key) {
      super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(final CommandSource source, final CommandArgs args) throws ArgumentParseException {
      try {
        return Key.key(args.next());
      } catch (final InvalidKeyException ex) {
        throw args.createError(Text.of(ex.getMessage()));
      }
    }

    @Override
    public List<String> complete(final CommandSource src, final CommandArgs args, final CommandContext context) {
      return Collections.emptyList();
    }
  }
}
