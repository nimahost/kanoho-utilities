package ec.brooke.kanoho.resourcepack;

import ec.brooke.kanoho.Kanoho;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;

public class ResourcepackLibrary {
    public static final String INDEX_FILENAME = "checksums.txt";
    private final HashMap<String, ResourcepackDefinition> packs = new HashMap<>();

    public ResourcepackLibrary() {
        ServerLifecycleEvents.SERVER_STARTING.register(this::reload);
    }

    public void reload(MinecraftServer server) {
        Kanoho.LOGGER.info("Reloading Resource Pack Library...");
        packs.clear();

        try {
            GitHub github = new GitHubBuilder().build();
            GHRelease release = github.getRepository(Kanoho.CONFIG.resourcepackGitHub).getLatestRelease();
            GHAsset asset = release.getAssets().stream().filter(a -> INDEX_FILENAME.equals(a.getName())).findFirst().orElseThrow();

            URLConnection conn = URI.create(asset.getBrowserDownloadUrl()).toURL().openConnection(server.getProxy());
            conn.setDoInput(true);
            conn.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            in.lines().forEach(line -> {
                if (line.isEmpty()) return;

                String[] split = line.split(" {2}", 2);
                String url = release.getAssets().stream().filter(a -> a.getName().equals(split[1])).findFirst().orElseThrow().getBrowserDownloadUrl();
                packs.put(split[1], new ResourcepackDefinition(split[0], url));
            });
        } catch (IOException | NoSuchElementException e) {
            throw new RuntimeException(e);
        }

        Kanoho.LOGGER.info("Loaded Resource Packs {}", getNames());
    }

    public ResourcepackDefinition getPack(String name) {
        return packs.get(name);
    }

    public Set<String> getNames() {
        return packs.keySet();
    }
}
