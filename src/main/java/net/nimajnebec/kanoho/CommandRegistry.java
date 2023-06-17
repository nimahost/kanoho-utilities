package net.nimajnebec.kanoho;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;

public class CommandRegistry {

    private final RootCommandNode<CommandSourceStack> rootNode;
    private CommandNode<CommandSourceStack> originalRunNode;

    public CommandRegistry(Server server) {
        CraftServer craftServer = (CraftServer) server;
        this.rootNode = craftServer.getServer().vanillaCommandDispatcher.getDispatcher().getRoot();

        CommandNode<CommandSourceStack> executeNode = rootNode.getChild("execute");
        this.originalRunNode = executeNode.getChild("run");
        executeNode.removeCommand("run");
    }

    public void cleanUp() {
        CommandNode<CommandSourceStack> executeNode = rootNode.getChild("execute");
        executeNode.removeCommand("run");
        executeNode.addChild(originalRunNode);
    }
}
