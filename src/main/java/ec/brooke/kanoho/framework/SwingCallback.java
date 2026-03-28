package ec.brooke.kanoho.framework;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

public interface SwingCallback {
    Event<SwingCallback> EVENT = EventFactory.createArrayBacked(SwingCallback.class,
            (listeners) -> (player, level, hand) -> {
                for (SwingCallback listener : listeners) {
                    listener.swing(player, level, hand);
                }
            });

    void swing(ServerPlayer player, Level level, InteractionHand hand);
}