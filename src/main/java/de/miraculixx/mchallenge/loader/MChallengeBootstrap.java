package de.miraculixx.mchallenge.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * Performs the world reset requested by /reset.
 * <p>
 * This runs before the server reads server.properties and before any level data is parsed, which is the only point
 * where deleting the level directory results in a freshly generated world - and where a new seed can still be applied.
 */
public class MChallengeBootstrap implements PluginBootstrap {
    private static final Path CONFIG = Path.of("plugins", "MUtils", "Challenges", "config.json");
    private static final Path SERVER_PROPERTIES = Path.of("server.properties");
    private static final String SEED_KEY = "level-seed";

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        ComponentLogger logger = context.getLogger();
        JsonObject config = readConfig(logger);
        if (config == null) return;
        if (!config.has("reset") || !config.get("reset").getAsBoolean()) return;

        String seed = config.has("resetSeed") && !config.get("resetSeed").isJsonNull() ? config.get("resetSeed").getAsString() : "";
        Properties properties = readProperties(logger);
        Path levelDirectory = worldContainer().resolve(properties.getProperty("level-name", "world"));

        logger.info("Resetting level directory {}", levelDirectory.toAbsolutePath());
        deleteLevel(levelDirectory, logger);
        applySeed(seed, logger);
        clearResetFlag(config, logger);
    }

    private JsonObject readConfig(ComponentLogger logger) {
        if (!Files.exists(CONFIG)) return null;
        try (Reader reader = Files.newBufferedReader(CONFIG)) {
            return new Gson().fromJson(reader, JsonObject.class);
        } catch (IOException | RuntimeException e) {
            logger.warn("Failed to read {} - skipping world reset", CONFIG, e);
            return null;
        }
    }

    private Properties readProperties(ComponentLogger logger) {
        Properties properties = new Properties();
        if (!Files.exists(SERVER_PROPERTIES)) return properties;
        try (InputStream in = Files.newInputStream(SERVER_PROPERTIES)) {
            properties.load(in);
        } catch (IOException e) {
            logger.warn("Failed to read {} - falling back to default level name", SERVER_PROPERTIES, e);
        }
        return properties;
    }

    private Path worldContainer() {
        Optional<String[]> arguments = ProcessHandle.current().info().arguments();
        if (arguments.isEmpty()) return Path.of(".");
        String[] args = arguments.get();
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("--universe") || args[i].equals("--world-container")) return Path.of(args[i + 1]);
        }
        return Path.of(".");
    }

    /**
     * Deletes every dimension, player data and level.dat. Installed datapacks survive - they are server content, not
     * world progress.
     */
    private void deleteLevel(Path levelDirectory, ComponentLogger logger) {
        if (!Files.isDirectory(levelDirectory)) return;
        try (Stream<Path> children = Files.list(levelDirectory)) {
            for (Path child : children.toList()) {
                if (child.getFileName().toString().equals("datapacks")) continue;
                deleteRecursively(child);
            }
        } catch (IOException e) {
            logger.error("Failed to delete level directory {}", levelDirectory.toAbsolutePath(), e);
        }
    }

    private void deleteRecursively(Path path) throws IOException {
        try (Stream<Path> walk = Files.walk(path)) {
            for (Path target : walk.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(target);
            }
        }
    }

    private void applySeed(String seed, ComponentLogger logger) {
        if (!Files.exists(SERVER_PROPERTIES)) return;
        try {
            String entry = SEED_KEY + "=" + seed.replace("\\", "\\\\");
            List<String> lines = Files.readAllLines(SERVER_PROPERTIES, StandardCharsets.ISO_8859_1);
            boolean replaced = false;
            for (int i = 0; i < lines.size(); i++) {
                if (!lines.get(i).startsWith(SEED_KEY + "=")) continue;
                lines.set(i, entry);
                replaced = true;
                break;
            }
            if (!replaced) lines.add(entry);
            Files.write(SERVER_PROPERTIES, lines, StandardCharsets.ISO_8859_1);
            logger.info(seed.isEmpty() ? "Cleared level seed - the new world uses a random seed" : "New world seed: " + seed);
        } catch (IOException e) {
            logger.error("Failed to update {} - the new world may use the previous seed", SERVER_PROPERTIES, e);
        }
    }

    private void clearResetFlag(JsonObject config, ComponentLogger logger) {
        config.addProperty("reset", false);
        config.remove("resetSeed");
        try (Writer writer = Files.newBufferedWriter(CONFIG)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(config, writer);
        } catch (IOException e) {
            logger.error("Failed to clear the reset flag in {} - the world will be reset again on the next start", CONFIG, e);
        }
    }
}
