package ec.brooke.kanoho.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import jdk.jfr.Experimental;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;

import java.util.Optional;
import java.util.UUID;

@Experimental
public class TestCommand extends KanohoCommand {

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> define() {
        return literal("test").executes(this::execute);
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack ss = ctx.getSource();

        String hash = "b1d553cc5d2cafb5d0f91f2758933c658b995535";

        ss.getPlayer().connection.send(
                new ClientboundResourcePackPushPacket(
                        UUID.nameUUIDFromBytes(hash.getBytes()),
                        "https://github.com/nimahost/kanoho-resource-pack/releases/download/v0.1.0/kanoho-space-high.zip",
                        "b1d553cc5d2cafb5d0f91f2758933c658b995532",
                        true,
                        Optional.of(Component.literal("Kanoho is better with it's Resource Pack!"))
                )
        );

        ctx.getSource().sendSuccess(() -> Component.literal("1 Command Tested 👍😎"), true);
        return 1;
    }
}
