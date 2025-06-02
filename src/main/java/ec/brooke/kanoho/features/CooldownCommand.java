package ec.brooke.kanoho.features;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ec.brooke.kanoho.framework.KanohoCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Command to modify player item cooldowns
 */
public class CooldownCommand extends KanohoCommand {

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> define() {
        return literal("cooldown").then(argument("targets", EntityArgument.players())
                .then(literal("set")
                        .then(argument("group", ResourceLocationArgument.id())
                                .then(argument("time", IntegerArgumentType.integer(0))
                                        .executes(ctx -> modify(ctx, IntegerArgumentType.getInteger(ctx, "time"))))))
                .then(literal("remove")
                        .then(argument("group", ResourceLocationArgument.id())
                                .executes(ctx -> modify(ctx, 0))))
        );
    }

    private int modify(CommandContext<CommandSourceStack> ctx, int time) throws CommandSyntaxException {
        ResourceLocation resource = ResourceLocationArgument.getId(ctx, "group");

        ServerPlayer[] targets = EntityArgument.getPlayers(ctx, "targets").toArray(ServerPlayer[]::new);
        for (ServerPlayer target: targets)
            if (time == 0) target.getCooldowns().removeCooldown(resource);
            else target.getCooldowns().addCooldown(resource, time);

        String string = String.format("%s cooldown '%s' for ", time == 0 ? "Removed" : "Set", resource);

        if (targets.length == 1)
            ctx.getSource().sendSuccess(() -> Component.literal(string).append(targets[0].getDisplayName()), true);
        else ctx.getSource().sendSuccess(() -> Component.literal(string + targets.length + " targets"), true);

        return targets.length;
    }
}
