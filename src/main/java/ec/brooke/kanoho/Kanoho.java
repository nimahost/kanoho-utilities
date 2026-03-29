package ec.brooke.kanoho;

import ec.brooke.kanoho.features.CooldownCommand;
import ec.brooke.kanoho.features.VelocityCommand;
import ec.brooke.kanoho.features.props.PropSystem;
import ec.brooke.kanoho.features.resourcepack.ResourcepackCommand;
import ec.brooke.kanoho.features.resourcepack.ResourcepackLibrary;
import ec.brooke.kanoho.framework.EphemeralityManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Kanoho implements ModInitializer {
    public static final String MOD_ID = "kanoho";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final KanohoConfig CONFIG = KanohoConfig.load();

    public static final ResourcepackLibrary resourcepacks = new ResourcepackLibrary();
    public static EphemeralityManager ephemerality = new EphemeralityManager();
    public static final FunctionEvents events = new FunctionEvents();

    @Override
    public void onInitialize() {
        resourcepacks.register();
        ephemerality.register();
        events.setup();

        new PropSystem().register();

        // Register commands
        new VelocityCommand().register();
        new ResourcepackCommand().register();
        new CooldownCommand().register();
    }
}
