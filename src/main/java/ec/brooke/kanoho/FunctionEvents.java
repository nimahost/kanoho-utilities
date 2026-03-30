package ec.brooke.kanoho;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.LinkedList;
import java.util.Queue;

public class FunctionEvents {
    private final Queue<QueuedEventInvocation> queue = new LinkedList<>();

    public void register() {
        ServerTickEvents.END_SERVER_TICK.register(this::handle);
    }

    public void invoke(Entity entity, ResourceLocation location) {
        queue.add(new QueuedEventInvocation(entity, location));
    }

    private void handle(MinecraftServer server) {
        ServerFunctionManager functions = server.getFunctions();

        while (!queue.isEmpty()) {
            QueuedEventInvocation invocation = queue.poll();
            functions.get(invocation.location).ifPresent(function ->
                    functions.execute(function, invocation.createCommandSourceStack())
            );
        }
    }

    private record QueuedEventInvocation(Entity entity, ResourceLocation location) {
        public CommandSourceStack createCommandSourceStack() {
            return new CommandSourceStack(
                    CommandSource.NULL,
                    entity.position(),
                    entity.getRotationVector(),
                    (ServerLevel) entity.level(),
                    2,
                    entity.getName().getString(),
                    entity.getDisplayName(),
                    entity.getServer(),
                    entity
            ).withSuppressedOutput();
        }
    }
}
