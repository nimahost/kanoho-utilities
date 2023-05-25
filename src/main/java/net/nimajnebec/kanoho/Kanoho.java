package net.nimajnebec.kanoho;

import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

public final class Kanoho extends JavaPlugin {

    private final Logger logger = this.getSLF4JLogger();

    @Override
    public void onEnable() {
        logger.info("{} {} Loaded!", this.getName(), this.getPluginMeta().getVersion());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
