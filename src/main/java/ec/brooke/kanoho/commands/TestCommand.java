package ec.brooke.kanoho.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ec.brooke.kanoho.Kanoho;
import ec.brooke.kanoho.entities.Marker;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class TestCommand extends KanohoCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("test").executes(TestCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack ss = ctx.getSource();
        var marker = new Marker(ss.getLevel(), ss.getPlayer());
        Kanoho.ephemerality.add(marker, ss.getPlayer());
        marker.setPos(ss.getPosition());

        ctx.getSource().sendSuccess(() -> Component.literal("1 Command Tested 👍😎"), true);
        return 1;
    }
}
