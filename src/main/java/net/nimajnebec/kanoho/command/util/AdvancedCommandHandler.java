package net.nimajnebec.kanoho.command.util;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.nimajnebec.kanoho.Kanoho;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;

final class AdvancedCommandHandler {
    private final AdvancedCommandDefinition definition;
    private final PluginCommand configuration;
    private boolean executeInjected = false;
    private boolean allowInExecute;

    private static CommandNode<CommandSourceStack> getRootNode() {
        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        return craftServer.getServer().vanillaCommandDispatcher.getDispatcher().getRoot();
    }

    public AdvancedCommandHandler(AdvancedCommandDefinition definition, PluginCommand configuration, boolean allowInExecute) {
        this.allowInExecute = allowInExecute;
        this.configuration = configuration;
        this.definition = definition;
    }

    public void injectExecuteNode() {
        // Inject node if allowed and not already injected
        if (this.allowInExecute && !this.executeInjected) {
            CommandNode<CommandSourceStack> rootNode = getRootNode();
            String name = this.getName();

            if (rootNode.getChild(name) != null) {
                Kanoho.getInstance().getSLF4JLogger().warn("Skipping injecting execute node for '{}' as a node by that name already exists.", name);
                this.allowInExecute = false; // Disallow in execute to prevent further inject attempts.
                return;
            }

            rootNode.addChild(this.getCommandNode(name));
            this.executeInjected = true;
        }
    }

    public void cleanup() {
        // Remove execute node if injected
        if (this.executeInjected) {
            getRootNode().removeCommand(this.getName());
        }
    }

    public LiteralCommandNode<CommandSourceStack> getCommandNode(String name) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(name) // Create node with specified name
                .requires(s -> this.configuration.testPermissionSilent(s.getBukkitSender())); // Check Bukkit permissions
        this.definition.define(builder); // Define command
        return builder.build();
    }

    public PluginCommand getConfiguration() {
        return this.configuration;
    }

    public String getName() {
        return this.configuration.getName();
    }
}
