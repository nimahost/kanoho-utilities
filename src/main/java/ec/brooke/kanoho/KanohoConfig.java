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
     * The maximum resultant velocity from the /velocity command
     */
    public double maximumVelocity = 100;

    /**
     * The GitHub repository containing the resourcepack in the form of owner/repo.
     */
    public String resourcepackGitHub = "nimahost/kanoho-resource-pack";

    /**
     * The default resourcepack option for new players
     */
    public String resourcepackDefault = "kanoho-space-normal";

    /**
     * The prompt shown to players when they are asked to apply a resource pack
     */
    public String resourcepackPrompt = "{text:\"Kanoho is better with it's official Resource Pack!\",bold:true,color:\"gold\"}";

    /**
     * Load the configuration file or the default
     * @return The loaded configuration file
     */
    public static KanohoConfig load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(Kanoho.MOD_ID + ".json");
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

        KanohoConfig instance;

        // Load config file if it exists
        if (!Files.exists(path)) instance = new KanohoConfig();
        else try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            instance = gson.fromJson(reader, KanohoConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Write config file to disk in case any updates
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            gson.toJson(instance, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return instance;
    }
}
