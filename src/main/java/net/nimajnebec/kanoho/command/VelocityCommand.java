package net.nimajnebec.kanoho.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.nimajnebec.kanoho.command.util.AdvancedCommandDefinition;

import java.util.Collection;

public class VelocityCommand extends AdvancedCommandDefinition {

    @Override
    public void define(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(literal("set")
            .then(argument("targets", EntityArgument.entities())
            .then(argument("x", DoubleArgumentType.doubleArg())
            .then(argument("y", DoubleArgumentType.doubleArg())
            .then(argument("z", DoubleArgumentType.doubleArg())
            .executes(this::execute))))));
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");

        for (Entity target: targets) {
            Vec3 velocity = new Vec3(
                DoubleArgumentType.getDouble(ctx, "x"),
                DoubleArgumentType.getDouble(ctx, "y"),
                DoubleArgumentType.getDouble(ctx, "z")
            );

            target.setDeltaMovement(velocity);
            if (target instanceof Player player) {
                player.hurtMarked = true;
            }
        }

        if (targets.size() == 1)
            ctx.getSource().sendSuccess(() -> Component.literal("Set velocity of ")
                .append(targets.iterator().next().getDisplayName()), true);
        else ctx.getSource().sendSuccess(() -> Component.literal(String.format("Set velocity of %s targets",
                targets.size())), true);

        return 1;
    }
}
