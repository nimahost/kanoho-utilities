package ec.brooke.kanoho.framework;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public interface RawInteractCallback {
    Event<RawInteractCallback> EVENT = EventFactory.createArrayBacked(RawInteractCallback.class,
            (listeners) -> (player, level, action) -> {
                for (RawInteractCallback listener : listeners) {
                    listener.interact(player, level, action);
                }
            });

    void interact(ServerPlayer player, Level level, Action action);

    enum Action {
        ATTACK,
        USE,
    }
}