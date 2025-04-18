package ec.brooke.kanoho.features.resourcepack;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import ec.brooke.kanoho.Kanoho;
import ec.brooke.kanoho.framework.KanohoCommand;
import ec.brooke.kanoho.framework.components.EntityComponents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * A command for players to select their preferred resourcepack
 */
public class ResourcepackCommand extends KanohoCommand {
    private static final SimpleCommandExceptionType ERROR_ALREADY = new SimpleCommandExceptionType(Component.literal("Already using that resource pack"));
    private static final SimpleCommandExceptionType ERROR_NOT_PLAYER = new SimpleCommandExceptionType(Component.literal("Executor must be a player"));
    private static final SimpleCommandExceptionType ERROR_NOT_OPTION = new SimpleCommandExceptionType(Component.literal("Invalid quality option"));

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> define() {
        return literal("resourcepack").then(argument("name", StringArgumentType.string())
                        .suggests(search(Kanoho.resourcepacks::names))
                        .executes(this::execute));
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        // Ensure player
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) throw ERROR_NOT_PLAYER.create();

        // Get and validate argument
        String name = StringArgumentType.getString(ctx, "name");
        if (!Kanoho.resourcepacks.contains(name)) throw ERROR_NOT_OPTION.create();

        // Check if already using
        String current = EntityComponents.RESOURCEPACK.from(player).orElse(Kanoho.CONFIG.resourcepackDefault);
        if (name.equals(current)) throw ERROR_ALREADY.create();
        EntityComponents.RESOURCEPACK.to(player, name);

        // Update pack and feedback
        Kanoho.resourcepacks.apply(player);
        ctx.getSource().sendSuccess(() -> Component.literal("Set resource pack level to ").append(name), true);
        return 1;
    }
}
