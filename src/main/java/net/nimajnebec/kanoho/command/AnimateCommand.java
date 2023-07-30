package net.nimajnebec.kanoho.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.nimajnebec.kanoho.command.util.AdvancedCommandDefinition;

public class AnimateCommand extends AdvancedCommandDefinition {
    private static final int MAX_ANIMATE_AMOUNT = 64;
    private static final Dynamic2CommandExceptionType ERROR_TOO_LARGE = new Dynamic2CommandExceptionType((maxcount, count) -> {
        return net.minecraft.network.chat.Component.translatable("commands.clone.toobig", maxcount, count);
    });

    @Override
    public void define(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(argument("begin", BlockPosArgument.blockPos())
            .then(argument("end", BlockPosArgument.blockPos())
            .then(argument("to", BlockPosArgument.blockPos())
            .then(argument("duration", IntegerArgumentType.integer(1, 1200))
            .executes(this::execute)))));
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        BlockPos begin = BlockPosArgument.getLoadedBlockPos(ctx, "begin");
        BlockPos end = BlockPosArgument.getLoadedBlockPos(ctx, "end");
        BlockPos to = BlockPosArgument.getLoadedBlockPos(ctx, "to");
        int duration = IntegerArgumentType.getInteger(ctx, "duration");

        BoundingBox box = BoundingBox.fromCorners(begin, end);
        int amount = box.getXSpan() * box.getYSpan() * box.getZSpan();
        if (amount > MAX_ANIMATE_AMOUNT) throw ERROR_TOO_LARGE.create(MAX_ANIMATE_AMOUNT, amount);

        readBox(box);

        return 1;
    }

    private static void readBox(BoundingBox box) {

    }

    private int test(CommandContext<CommandSourceStack> ctx) {
        var mm = MiniMessage.miniMessage();
        Component parsed = mm.deserialize("<rainbow>RAINBOW!!!</rainbow>");
        ctx.getSource().sendSuccess(() -> PaperAdventure.asVanilla(parsed), true);
        return 1;
    }
}
