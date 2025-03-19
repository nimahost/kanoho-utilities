package ec.brooke.kanoho.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Command to modify entity velocities, including players
 */
public class VelocityCommand extends KanohoCommand {
    private static final SimpleCommandExceptionType ERROR_TOO_LARGE = new SimpleCommandExceptionType(Component.literal("Could not set velocity: Velocity is too large"));

    private static final int MAXIMUM_VELOCITY = 100;

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> define() {
        return literal("velocity").then(literal("set")
                        .then(argument("targets", EntityArgument.entities())
                                .then(argument("velocity", Vec3Argument.vec3())
                                        .executes(this::setVelocity))))
                .then(literal("add")
                        .then(argument("targets", EntityArgument.entities())
                                .then(argument("velocity", Vec3Argument.vec3())
                                        .executes(this::addVelocity))));
    }

    /**
     * Calculates the executor's resultant velocity from a command's arguments
     * @param ctx The context to get arguments from
     * @return Vector of the resultant velocity
     */
    private Vec3 calculateVelocity(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();

        Coordinates argument = Vec3Argument.getCoordinates(ctx, "velocity");
        Vec3 end = argument.getPosition(source);
        Vec3 start = source.getPosition();

        double x, y, z;

        // Cancel Out Relative Coordinates
        if (argument.isXRelative()) x = -start.x;
        else x = -0.5;

        if (argument.isYRelative()) y = -start.y;
        else y = 0;

        if (argument.isZRelative()) z = -start.z;
        else z = -0.5;

        x += end.x;
        y += end.y;
        z += end.z;

        // Apply multiplier to relative velocity
        if (argument instanceof WorldCoordinates) {
            Vec3 relative;
            @Nullable Entity executor = source.getEntity();
            if (executor == null) relative = new Vec3(0, 0, 0);
            else relative = executor.getDeltaMovement();

            if (argument.isXRelative()) x *= relative.x;
            if (argument.isYRelative()) y *= relative.y;
            if (argument.isZRelative()) z *= relative.z;
        }

        return new Vec3(x, y, z);
    }

    private int setVelocity(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");

        Vec3 velocity = calculateVelocity(ctx);

        if (velocity.length() > MAXIMUM_VELOCITY) throw ERROR_TOO_LARGE.create();

        for (Entity target: targets) {
            target.setDeltaMovement(velocity);
            target.hurtMarked = true;
        }

        if (targets.size() == 1)
            ctx.getSource().sendSuccess(() -> Component.literal("Set velocity of ")
                    .append(targets.iterator().next().getDisplayName()), true);
        else ctx.getSource().sendSuccess(() -> Component.literal(String.format("Set velocity of %s targets",
                targets.size())), true);

        return targets.size();
    }

    private int addVelocity(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");

        Vec3 velocity = calculateVelocity(ctx);

        if (velocity.length() > MAXIMUM_VELOCITY) {
            ctx.getSource().sendFailure(Component.literal("Could not add velocity: Velocity is too large"));
            return 0;
        }

        ArrayList<Entity> successful = new ArrayList<>();
        for (Entity target: targets) {
            Vec3 newVelocity = target.getDeltaMovement().add(velocity);
            if (newVelocity.length() > MAXIMUM_VELOCITY) continue;
            target.setDeltaMovement(newVelocity);
            target.hurtMarked = true;
            successful.add(target);
        }

        if (successful.isEmpty())
            throw ERROR_TOO_LARGE.create();
        else if (successful.size() == 1)
            ctx.getSource().sendSuccess(() -> Component.literal("Added velocity to ").append(successful.getFirst().getDisplayName()), true);
        else ctx.getSource().sendSuccess(() -> Component.literal(String.format("Added velocity to %s targets", successful.size())), true);

        return successful.size();
    }
}
