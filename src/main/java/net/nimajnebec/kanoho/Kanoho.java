package net.nimajnebec.kanoho;

import net.nimajnebec.kanoho.command.AnimateCommand;
import net.nimajnebec.kanoho.command.util.AdvancedCommandRegistry;
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
        this.getServer().getPluginManager().registerEvents(commands, this);

        commands.setup();
        commands.register("animate", new AnimateCommand(), true);

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
