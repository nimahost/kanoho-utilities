package ec.brooke.kanoho;

import ec.brooke.kanoho.commands.VelocityCommand;
import net.fabricmc.api.ModInitializer;

public class Kanoho implements ModInitializer {
    public static EphemeralityManager ephemerality;

    @Override
    public void onInitialize() {
        new VelocityCommand().register();
    }
}
