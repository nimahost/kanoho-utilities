package ec.brooke.kanoho.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public abstract class KanohoCommand {
    protected abstract LiteralArgumentBuilder<CommandSourceStack> define();

    public final void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(define());
        });
    }

    protected static LiteralArgumentBuilder<CommandSourceStack> literal(String literal) {
        return Commands.literal(literal);
    }

    protected static <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(String name, ArgumentType<T> type) {
        return Commands.argument(name, type);
    }

}
