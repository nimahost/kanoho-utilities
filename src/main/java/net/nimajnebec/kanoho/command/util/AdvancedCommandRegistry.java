package net.nimajnebec.kanoho.command.util;

import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AdvancedCommandRegistry implements Listener {
    private final List<AdvancedCommandHandler> commands = new ArrayList<>();
    private final JavaPlugin plugin;
    private boolean setup = false;

    protected static DedicatedServer getServer() {
        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        return craftServer.getServer();
    }

    protected static Commands getBukkitDispatcher() {
        return getServer().resources.managers().commands;
    }

    protected static Commands getVanillaDispatcher() {
        return getServer().vanillaCommandDispatcher;
    }

    public AdvancedCommandRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void register(String name, AdvancedCommandDefinition definition) {
        this.register(name, definition, false);
    }

    public void register(String name, AdvancedCommandDefinition definition, boolean allowInExecute) {
        @Nullable PluginCommand configuration = plugin.getCommand(name);
        if (configuration == null) throw new RuntimeException("Command '"+ name + "' not found.");
        this.register(configuration, definition, allowInExecute);
    }

    public void register(PluginCommand configuration, AdvancedCommandDefinition definition) {
        this.register(configuration, definition, false);
    }

    public void register(PluginCommand configuration, AdvancedCommandDefinition definition, boolean allowInExecute) {
        if (!this.setup) throw new RuntimeException("Tried to register a command before the registry was set up.");
        this.commands.add(new AdvancedCommandHandler(definition, configuration, allowInExecute));
    }

    public void setup() {
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.setup = true;
    }

    @EventHandler
    private void onRegisterCommand(CommandRegisteredEvent<CommandSourceStack> event) {
        if (event.getCommand() instanceof PluginCommand configuration) {
            for (AdvancedCommandHandler command : this.commands) {
                if (command.getConfiguration() == configuration) {
                    Commands dispatcher = getBukkitDispatcher();

                    // Register command node
                    LiteralCommandNode<CommandSourceStack> node = command.getCommandNode(event.getCommandLabel());
                    dispatcher.vanillaCommandNodes.add(node); // Add to vanilla command nodes so that errors are handled properly
                    event.setLiteral(node); // Set literal from definition

                    // Inject execute node
                    command.injectExecuteNode();
                    return;
                }
            }
        }
    }

    public void cleanup() {
        // Cleanup all registered commands
        for (AdvancedCommandHandler command : this.commands) {
            command.cleanup();
        }
    }
}
