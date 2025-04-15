package ec.brooke.kanoho.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import ec.brooke.kanoho.Kanoho;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class QualityCommand extends KanohoCommand {
    private static final SimpleCommandExceptionType ERROR_NOT_PLAYER = new SimpleCommandExceptionType(Component.literal("Executor must be a player"));

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> define() {
        return literal("quality").then(argument("name", StringArgumentType.string()).suggests(this::suggest).executes(this::execute));
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (!ctx.getSource().isPlayer()) throw ERROR_NOT_PLAYER.create();

        String name = StringArgumentType.getString(ctx, "name");
        Objects.requireNonNull(ctx.getSource().getPlayer()).connection.send(Kanoho.resourcepacks.getPack(name).packet());
        return 1;
    }

    private CompletableFuture<Suggestions> suggest(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {
        Kanoho.resourcepacks.getNames().forEach(builder::suggest);
        return builder.buildFuture();
    }
}
