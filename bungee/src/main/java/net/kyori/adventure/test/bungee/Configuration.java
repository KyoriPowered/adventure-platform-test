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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Configuration {
  private final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
  private final Path path;
  private net.md_5.bungee.config.Configuration config;

  // Options

  private Component motd;

  /* package */ Configuration(final Path configFile) {
    this.path = configFile;
    try {
      Files.createDirectories(configFile.toAbsolutePath().getParent());
    } catch(final IOException e) {
      // TODO
    }
  }

  public static Configuration create(final Path configFile) throws IOException {
    final Configuration config = new Configuration(configFile);
    config.reload();
    return config;
  }

  public void reload() throws IOException {
    if(!this.path.toFile().isFile()) { // copy default config from jar
      try(final InputStream is = this.getClass().getResourceAsStream("default-config.yml");
          final OutputStream os = Files.newOutputStream(this.path)) {
        final byte[] buf = new byte[2048];
        int read;
        while((read = is.read(buf)) != -1) {
          os.write(buf, 0, read);
        }
      }
    }

    try(final BufferedReader read = Files.newBufferedReader(this.path, StandardCharsets.UTF_8)) {
      this.config = this.provider.load(read);
    }

    this.motd = this.parseMessage("messages.motd");
  }

  /* package */ void save() throws IOException {
    if(this.config == null) {
      this.config = new net.md_5.bungee.config.Configuration();
    }

    try(final BufferedWriter writer = Files.newBufferedWriter(this.path, StandardCharsets.UTF_8)) {
      this.provider.save(this.config, writer);
    }
  }

  private @Nullable Component parseMessage(final String path) {
    final String message = this.config.getString(path, null);
    if(message == null) {
      return null;
    }
    return MiniMessage.withMarkDown().parse(message);
  }

  public Component motd() {
    return this.motd;
  }
}
