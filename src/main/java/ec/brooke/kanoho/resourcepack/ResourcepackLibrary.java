package ec.brooke.kanoho.resourcepack;

import ec.brooke.kanoho.Kanoho;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
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

public class ResourcepackLibrary {
    public static final String INDEX_FILENAME = "checksums.txt";

    private final HashMap<String, ResourcepackDefinition> library = new HashMap<>();

    public ResourcepackLibrary() {
        ServerLifecycleEvents.SERVER_STARTING.register(this::reload);
    }

    public void reload(MinecraftServer server) {
        Kanoho.LOGGER.info("Reloading resource pack library...");
        library.clear();

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

        Kanoho.LOGGER.info("Loaded resource packs {}", getNames());
    }

    public ResourcepackDefinition getPack(String name) {
        return library.get(name);
    }

    public boolean contains(String name) {
        return library.containsKey(name);
    }

    public Set<String> getNames() {
        return library.keySet();
    }
}
