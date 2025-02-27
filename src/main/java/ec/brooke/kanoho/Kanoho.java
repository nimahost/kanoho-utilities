package ec.brooke.kanoho;

import ec.brooke.kanoho.commands.TestCommand;
import net.fabricmc.api.ModInitializer;

public class Kanoho implements ModInitializer {
    public static EphemeralityManager ephemerality;

    @Override
    public void onInitialize() {
        ephemerality = new EphemeralityManager();
        new TestCommand().register();
    }
}
