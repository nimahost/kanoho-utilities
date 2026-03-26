package ec.brooke.kanoho.features;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ec.brooke.kanoho.Kanoho;
import ec.brooke.kanoho.features.markers.PointMarker;
import ec.brooke.kanoho.framework.KanohoCommand;
import jdk.jfr.Experimental;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

@Experimental
public class TestCommand extends KanohoCommand {

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> define() {
        return literal("test").executes(this::execute);
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack ss = ctx.getSource();

        Kanoho.ephemerality.add(new PointMarker(ss.getPlayer(), ss.getPlayer().position()), ss.getPlayer());

        ctx.getSource().sendSuccess(() -> Component.literal("1 Command Tested 👍😎"), true);
        return 1;
    }
}
