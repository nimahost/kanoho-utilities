package net.nimajnebec.kanoho;

import net.nimajnebec.kanoho.command.VelocityCommand;
import net.nimajnebec.kanoho.command.util.AdvancedCommandRegistry;
import net.nimajnebec.kanoho.events.UseHelper;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

public final class Kanoho extends JavaPlugin {
    private static Kanoho instance;
    private final AdvancedCommandRegistry commands = new AdvancedCommandRegistry(this);
    private final Logger logger = this.getSLF4JLogger();

    public Kanoho() {
        Kanoho.instance = this;
    }

    @Override
    public void onEnable() {
        PluginManager manager = this.getServer().getPluginManager();

        // Register Events
        manager.registerEvents(new UseHelper(this), this);

        // Register Commands
        commands.setup();
        commands.register("velocity", new VelocityCommand(), true);

        logger.info("{} {} Loaded!", this.getName(), this.getPluginMeta().getVersion());
    }

    @Override
    public void onDisable() {
        commands.cleanup();
    }

    public static Kanoho getInstance() {
        return instance;
    }
}
