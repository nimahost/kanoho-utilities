package ec.brooke.kanoho.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import ec.brooke.kanoho.Kanoho;
import net.fabricmc.fabric.mixin.gametest.ArgumentTypesMixin;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class QualityCommand extends KanohoCommand {
    private static final SimpleCommandExceptionType ERROR_NOT_PLAYER = new SimpleCommandExceptionType(Component.literal("Executor must be a player"));
    private static final SimpleCommandExceptionType ERROR_NOT_OPTION = new SimpleCommandExceptionType(Component.literal("Invalid quality option"));

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> define() {
        return literal("quality").then(argument("name", StringArgumentType.string())
                        .suggests(search(Kanoho.resourcepacks::getNames))
                        .executes(this::execute));
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) throw ERROR_NOT_PLAYER.create();

        String name = StringArgumentType.getString(ctx, "name");
        if (!Kanoho.resourcepacks.contains(name)) throw ERROR_NOT_OPTION.create();

        player.connection.send(Kanoho.resourcepacks.getPack(name).packet());

        ctx.getSource().sendSuccess(() -> Component.literal("Set quality level to ").append(name), true);
        return 1;
    }
}
