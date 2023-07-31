package net.nimajnebec.kanoho.command.util;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.nimajnebec.kanoho.Kanoho;
import org.bukkit.command.PluginCommand;


class AdvancedCommandHandler {
    private final AdvancedCommandDefinition definition;
    private final PluginCommand configuration;
    private boolean executeInjected = false;
    private boolean allowInExecute;

    public AdvancedCommandHandler(AdvancedCommandDefinition definition, PluginCommand configuration, boolean allowInExecute) {
        this.allowInExecute = allowInExecute;
        this.configuration = configuration;
        this.definition = definition;
    }

    public void injectExecuteNode() {
        // Inject node if allowed and not already injected
        if (this.allowInExecute && !this.executeInjected) {
            CommandNode<CommandSourceStack> root = AdvancedCommandRegistry.getVanillaDispatcher().getDispatcher().getRoot();
            String name = this.getName();

            if (root.getChild(name) != null) {
                Kanoho.getInstance().getSLF4JLogger().warn("Skipping injecting execute node for '{}' as a node by that name already exists.", name);
                this.allowInExecute = false; // Disallow in execute to prevent further inject attempts.
                return;
            }

            root.addChild(this.getCommandNode(name));
            this.executeInjected = true;
        }
    }

    public void cleanup() {
        // Remove execute node if injected
        if (this.executeInjected) {
            CommandNode<CommandSourceStack> root = AdvancedCommandRegistry.getVanillaDispatcher().getDispatcher().getRoot();
            root.removeCommand(this.getName());
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
