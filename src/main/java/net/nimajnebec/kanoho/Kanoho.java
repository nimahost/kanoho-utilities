package net.nimajnebec.kanoho;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.command.VanillaCommandWrapper;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.util.List;

public final class Kanoho extends JavaPlugin {

    private final Logger logger = this.getSLF4JLogger();
//    private final CommandRegistry commands = new CommandRegistry(this.getServer());

    @Override
    public void onEnable() {
        logger.info("{} {} Loaded!", this.getName(), this.getPluginMeta().getVersion());
    }

    private int test(CommandContext<CommandSourceStack> ctx) {
        var mm = MiniMessage.miniMessage();
        Component parsed = mm.deserialize("<rainbow>RAINBOW!!!</rainbow>");
        ctx.getSource().sendSuccess(PaperAdventure.asVanilla(parsed), true);
        return 2;
    }

    @Override
    public void onDisable() {
//        commands.cleanUp();
    }
}
