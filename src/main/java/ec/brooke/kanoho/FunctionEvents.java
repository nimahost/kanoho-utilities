package ec.brooke.kanoho;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.*;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;

import java.util.*;

public class FunctionEvents {
    private final Queue<QueuedEventInvocation> queue = new LinkedList<>();

    public void register() {
        ServerTickEvents.END_SERVER_TICK.register(this::handle);
    }

    public void invoke(Entity entity, ResourceLocation location) {
        invoke(entity, location, new CompoundTag());
    }

    public void invoke(Entity entity, ResourceLocation location, Map<String, String> args) {
        CompoundTag tag = new CompoundTag();
        args.forEach(tag::putString);
        invoke(entity, location, tag);
    }

    public void invoke(Entity entity, ResourceLocation location, CompoundTag args) {
        queue.add(new QueuedEventInvocation(entity, location, args));
    }

    private void handle(MinecraftServer server) {
        while (!queue.isEmpty()) {
            QueuedEventInvocation invocation = queue.poll();
            invocation.execute(server);
        }
    }

    private record QueuedEventInvocation(Entity entity, ResourceLocation location, CompoundTag args) {
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

        public void execute(MinecraftServer server) {
            ServerFunctionManager functions = server.getFunctions();
            Optional<CommandFunction<CommandSourceStack>> maybe = functions.get(location);

            if (maybe.isEmpty()) return;
            CommandFunction<CommandSourceStack> function = maybe.get();

            ProfilerFiller profiler = Profiler.get();
            profiler.push(() -> "function " + function.id());

            try {
                CommandSourceStack css = createCommandSourceStack();
                InstantiatedFunction<CommandSourceStack> bound = function.instantiate(args, css.dispatcher());
                Commands.executeCommandInContext(css, ctx -> ExecutionContext.queueInitialFunctionCall(ctx, bound, css, CommandResultCallback.EMPTY));
            } catch (Exception e) {
                Kanoho.LOGGER.warn("Failed to execute function {}", function.id(), e);
            } finally {
                profiler.pop();
            }
        }
    }
}
