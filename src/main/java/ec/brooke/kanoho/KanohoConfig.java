package ec.brooke.kanoho;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/***
 * Mod configuration model
 */
public class KanohoConfig {

    /**
     * The maximum resultant velocity from the /velocity command.
     */
    public double maximumVelocity = 100;

    /**
     * The GitHub repository containing the resourcepack in the form of owner/repo.
     */
    public String resourcepackGitHub = "nimahost/kanoho-resource-pack";

    /**
     * The default resourcepack option for new players.
     */
    public String resourcepackDefault = "kanoho-space-low";

    /**
     * Load the configuration file or the default
     * @return The loaded configuration file
     */
    public static KanohoConfig load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(Kanoho.MOD_ID + ".json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (!Files.exists(path)) try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            KanohoConfig config = new KanohoConfig();
            gson.toJson(config, writer);
            return config;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } else try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, KanohoConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
