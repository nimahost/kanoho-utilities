package ec.brooke.kanoho;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.level.ServerPlayer;

import java.util.LinkedList;
import java.util.Queue;

public class FunctionEvents {
    private final Queue<QueuedEventInvocation> queue = new LinkedList<>();

    public void setup() {
        ServerTickEvents.END_SERVER_TICK.register(this::handle);
    }

    public void invoke(ServerPlayer player, ResourceLocation location) {
        queue.add(new QueuedEventInvocation(player, location));
    }

    private void handle(MinecraftServer server) {
        ServerFunctionManager functions = server.getFunctions();

        while (!queue.isEmpty()) {
            QueuedEventInvocation invocation = queue.poll();
            functions.get(invocation.location).ifPresent(function -> functions.execute(function,
                    invocation.player.createCommandSourceStack().withSuppressedOutput().withPermission(2)
            ));
        }
    }

    private record QueuedEventInvocation(ServerPlayer player, ResourceLocation location) {}
}
