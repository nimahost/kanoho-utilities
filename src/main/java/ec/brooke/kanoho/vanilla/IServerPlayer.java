package ec.brooke.kanoho.vanilla;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

/**
 * Interface for interacting with Kanoho methods on {@link ServerPlayer}
 */
public interface IServerPlayer {

    /**
     * Add the {@code kanoho} "pseudo-tag" to the provided {@link CompoundTag}
     * @param tag The tag to add to
     */
    void kanoho$putStatus(CompoundTag tag);

    /**
     * Return the player's {@code kanoho} "pseudo-tag" as mutable
     * @return The {@code kanoho} "pseudo-tag"
     */
    CompoundTag kanoho$getStatus();

}
