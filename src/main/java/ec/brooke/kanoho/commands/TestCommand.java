package ec.brooke.kanoho.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ec.brooke.kanoho.Kanoho;
import ec.brooke.kanoho.entities.markers.Marker;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class TestCommand extends KanohoCommand {
    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> define() {
        return literal("test").executes(this::execute);
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack ss = ctx.getSource();
        var marker = new Marker(ss.getPlayer(), ss.getPosition());
        Kanoho.ephemerality.add(marker, ss.getPlayer());

        ctx.getSource().sendSuccess(() -> Component.literal("1 Command Tested 👍😎"), true);
        return 1;
    }
}
