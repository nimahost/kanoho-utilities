package ec.brooke.kanoho.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/***
 * Base class for all Kanoho Brigadier commands
 */
public abstract class KanohoCommand {

    /**
     * Called on registration, should return the root node of the command.
     * @return The root node of this command.
     */
    protected abstract LiteralArgumentBuilder<CommandSourceStack> define();

    /**
     * Setup this command to be registered, must be called before {@link CommandRegistrationCallback} is fired
     */
    public final void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(define()));
    }

    /**
     * Shortcut method to create literal command node
     * @param literal The string to use for the node
     * @return The created node
     */
    protected static LiteralArgumentBuilder<CommandSourceStack> literal(String literal) {
        return Commands.literal(literal);
    }

    /**
     * Shortcut method to create an argument command node
     * @param name The name of this argument
     * @param type The type of this argument
     * @return The created node
     * @param <T> The type of this argument
     */
    protected static <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(String name, ArgumentType<T> type) {
        return Commands.argument(name, type);
    }

    /**
     * Creates a {@link SuggestionProvider} for the options provided by the supplier
     * @param options A function to retrieve options
     * @return The resultant {@link SuggestionProvider}
     */
    protected static SuggestionProvider<CommandSourceStack> search(Supplier<Iterable<String>> options) {
        return (ctx, builder) -> {
            String remaining = builder.getRemainingLowerCase();

            for (String option : options.get())
                if (remaining.isBlank() || option.toLowerCase().contains(remaining))
                    builder.suggest(option);

            return builder.buildFuture();
        };
    }
}
