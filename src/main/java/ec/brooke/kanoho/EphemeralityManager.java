package ec.brooke.kanoho;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class Ephemerality {
    List<Pair> entities = new ArrayList<>();

    public Ephemerality() {
        ServerTickEvents.END_WORLD_TICK.register(this::tick);
    }

    private void tick(ServerLevel level) {
        for (Pair entry : entities) {
            entry.entity.tick();
            entry.serverEntity.sendChanges();
        }
    }

    public void create(ServerLevel level, Entity entity, ServerPlayer player) {
        EntityType<?> type = entity.getType();
        ServerEntity serverEntity = new ServerEntity(
                level,
                entity,
                type.updateInterval(),
                type.trackDeltas(),
                player.connection::send
        );

        serverEntity.addPairing(player);
        entities.add(new Pair(entity, serverEntity));
    }

    private record Pair(Entity entity, ServerEntity serverEntity) {}
}
