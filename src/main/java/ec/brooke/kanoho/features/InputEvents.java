package ec.brooke.kanoho.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ec.brooke.kanoho.Kanoho;
import ec.brooke.kanoho.features.components.EntityComponents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Input;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public record InputEvents(
        Optional<ResourceLocation> forward,
        Optional<ResourceLocation> backward,
        Optional<ResourceLocation> left,
        Optional<ResourceLocation> right,
        Optional<ResourceLocation> jump,
        Optional<ResourceLocation> sneak,
        Optional<ResourceLocation> sprint
) {
    public static final Codec<InputEvents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.optionalFieldOf("forward").forGetter(InputEvents::forward),
        ResourceLocation.CODEC.optionalFieldOf("backward").forGetter(InputEvents::backward),
        ResourceLocation.CODEC.optionalFieldOf("left").forGetter(InputEvents::left),
        ResourceLocation.CODEC.optionalFieldOf("right").forGetter(InputEvents::right),
        ResourceLocation.CODEC.optionalFieldOf("jump").forGetter(InputEvents::jump),
        ResourceLocation.CODEC.optionalFieldOf("sneak").forGetter(InputEvents::sneak),
        ResourceLocation.CODEC.optionalFieldOf("sprint").forGetter(InputEvents::sprint)
    ).apply(instance, InputEvents::new));

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                Entity vehicle = player.getVehicle();
                if (vehicle != null) EntityComponents.INPUT_EVENTS.from(vehicle).ifPresent(component -> component.invoke(vehicle, player));
            }
        });
    }

    public void invoke(Entity entity, ServerPlayer player) {
        Map<String, String> args = Collections.singletonMap("player", player.getUUID().toString());
        Input input = player.getLastClientInput();

        if (input.forward()) forward.ifPresent(resource -> Kanoho.events.invoke(entity, resource, args));
        if (input.backward()) backward.ifPresent(resource -> Kanoho.events.invoke(entity, resource, args));
        if (input.left()) left.ifPresent(resource -> Kanoho.events.invoke(entity, resource, args));
        if (input.right()) right.ifPresent(resource -> Kanoho.events.invoke(entity, resource, args));
        if (input.jump()) jump.ifPresent(resource -> Kanoho.events.invoke(entity, resource, args));
        if (input.shift()) sneak.ifPresent(resource -> Kanoho.events.invoke(entity, resource, args));
        if (input.sprint()) sprint.ifPresent(resource -> Kanoho.events.invoke(entity, resource, args));
    }
}
