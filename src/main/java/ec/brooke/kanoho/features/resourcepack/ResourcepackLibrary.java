package ec.brooke.kanoho.features.resourcepack;

import ec.brooke.kanoho.Kanoho;
import ec.brooke.kanoho.features.components.EntityComponents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.io.FilenameUtils;
import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Represents a library of resource packs players can choose between
 * This class is responsible for generating this library from GitHub
 */
public class ResourcepackLibrary {
    /** Override pack name used to turn off resource packs entirely */
    public static final String OVERRIDE_NAME = "none";

    /** The name of the release asset containing the SHA1s of the options */
    public static final String INDEX_FILENAME = "checksums.txt";

    /** The message sent to players when their resource pack can't be applied */
    public static final Component ERROR_MESSAGE = Component.literal("Could not apply resource pack :(").withStyle(ChatFormatting.RED);

    private final HashMap<String, ResourcepackDefinition> library = new HashMap<>();

    public ResourcepackLibrary() {
        ServerLifecycleEvents.SERVER_STARTING.register(this::reload);
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((s, c) -> this.reload(s));
        ServerPlayConnectionEvents.JOIN.register((l, s, m) -> this.apply(l.player));
    }

    /**
     * Applies their selected resource pack to the specified player
     * @param player The player to reload
     */
    public void apply(ServerPlayer player) {
        String name = EntityComponents.RESOURCEPACK.from(player).orElse(Kanoho.CONFIG.resourcepackDefault);

        if (OVERRIDE_NAME.equals(name)) player.connection.send(ResourcepackDefinition.pop());
        else if (contains(name)) player.connection.send(get(name).push());
        else {
            // Send error message
            player.sendSystemMessage(ERROR_MESSAGE);
            String command = "/resourcepack %s".formatted(name);
            player.sendSystemMessage(Component.literal("Click to Retry").withStyle(ChatFormatting.UNDERLINE)
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)))
            );
        }
    }

    /**
     * Reloads the resource pack library from GitHub.
     * @param server The current instance of {@link MinecraftServer}
     */
    public void reload(MinecraftServer server) {
        library.clear();

        new Thread(() -> {
            Kanoho.LOGGER.info("Reloading resource pack library...");

            try {
                GitHub github = new GitHubBuilder().build();
                GHRelease release = github.getRepository(Kanoho.CONFIG.resourcepackGitHub).getLatestRelease();

                GHAsset index = release.getAssets().stream().filter(a -> INDEX_FILENAME.equals(a.getName())).findFirst()
                        .orElseThrow(() -> new NoSuchElementException("Could not find '%s' in release assets".formatted(INDEX_FILENAME)));

                // Request index file
                URLConnection conn = URI.create(index.getBrowserDownloadUrl()).toURL().openConnection(server.getProxy());
                conn.setDoInput(true);
                conn.connect();

                // Parse lines
                new BufferedReader(new InputStreamReader(conn.getInputStream())).lines().forEach(line -> {
                    if (line.isEmpty()) return;

                    String[] split = line.split(" {2}", 2);
                    if (split.length != 2) throw new IndexOutOfBoundsException("Invalid index file");
                    String name = FilenameUtils.removeExtension(split[1]);

                    GHAsset asset = release.getAssets().stream().filter(a -> a.getName().equals(split[1])).findFirst()
                            .orElseThrow(() -> new NoSuchElementException("Could not find '%s' in release assets".formatted(split[1])));

                    library.put(name, new ResourcepackDefinition(split[0], asset.getBrowserDownloadUrl()));
                });
            } catch (IOException | NoSuchElementException | IndexOutOfBoundsException e) {
                Kanoho.LOGGER.error("Failed to reload resource pack library", e);
            }

            Kanoho.LOGGER.info("Loaded resource packs {}", names());

            // Update player packs
            for (ServerPlayer player : server.getPlayerList().getPlayers()) apply(player);
        }).start();
    }

    /**
     * @param name The name of the resource pack
     * @return The resource pack with the given name
     */
    public ResourcepackDefinition get(String name) {
        return library.get(name);
    }

    /**
     * @param name The name of the resource pack
     * @return Whether the library contains a resource pack with the specified name
     */
    public boolean contains(String name) {
        return library.containsKey(name) || OVERRIDE_NAME.equals(name);
    }

    /**
     * @return The collection of all resource pack names in the library
     */
    public Set<String> names() {
        return library.keySet();
    }
}
