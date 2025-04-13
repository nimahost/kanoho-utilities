package ec.brooke.kanoho;

import ec.brooke.kanoho.commands.TestCommand;
import ec.brooke.kanoho.commands.VelocityCommand;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Kanoho implements ModInitializer {
    public static final String MOD_ID = "kanoho";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final KanohoConfig CONFIG = KanohoConfig.load();

    public static EphemeralityManager ephemerality;

    @Override
    public void onInitialize() {
        new VelocityCommand().register();
        new TestCommand().register();
    }
}
