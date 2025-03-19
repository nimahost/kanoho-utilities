package ec.brooke.kanoho.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ec.brooke.kanoho.vanilla.IServerPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record UsableComponent(boolean consumes) {
    public static final String STATUS_KEY = "consumed";

    public static final Codec<UsableComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.BOOL.optionalFieldOf("consumes", true).forGetter(UsableComponent::consumes)
    ).apply(builder, UsableComponent::new));

    public void use(ServerPlayer player, ItemStack stack) {
        ((IServerPlayer) player).kanoho$getStatus().put(STATUS_KEY, stack.save(player.registryAccess()));
    }
}
