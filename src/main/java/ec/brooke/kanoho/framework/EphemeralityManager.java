package ec.brooke.kanoho.framework;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for create, ticking and removing ephemeral entities.
 */
public class EphemeralityManager {
    List<EphemeralEntity> entities = new ArrayList<>();

    /**
     * Registers the manager's event handlers.
     */
    public void register() {
        ServerTickEvents.START_SERVER_TICK.register(this::tick);
    }

    private void tick(MinecraftServer minecraftServer) {
        entities.removeIf((entity) -> {
            if (entity.target.isRemoved()) return true;

            if (entity.isRemoved()) return entity.removePairing();

            entity.tick();

            if (entity.isRemoved()) return entity.removePairing();
            else return false;
        });
    }

    /**
     * Add the specified entity to this manager visible only to the target
     * @param entity The entity to manage
     * @param target The player the entity should be visible to
     */
    public <T extends Entity> T add(T entity, ServerPlayer target) {
        EphemeralEntity e = new EphemeralEntity(entity, target);
        entities.add(e);
        e.addPairing();
        return entity;
    }

    /**
     * Represents a managed entity
     */
    private static final class EphemeralEntity {
        private final ServerEntity serverEntity;
        private final ServerPlayer target;
        private final Entity entity;

        private EphemeralEntity(Entity entity, ServerPlayer target) {
            this.entity = entity;
            this.target = target;

            EntityType<?> type = entity.getType();
            this.serverEntity = new ServerEntity(
                    target.serverLevel(),
                    entity,
                    type.updateInterval(),
                    type.trackDeltas(),
                    target.connection::send
            );
        }

        /**
         * Tick the entity and update on the target's client
         */
        public void tick() {
            this.entity.tick();
            this.serverEntity.sendChanges();
        }

        /**
         * @return Weather this entity has been removed
         */
        public boolean isRemoved() {
            return this.entity.isRemoved();
        }

        /**
         * Creates a corresponding pairing of this entity on the target's client
         */
        public void addPairing() {
            this.serverEntity.addPairing(this.target);
        }

        /**
         * Remove the corresponding pairing of this entity on the target's client
         */
        public boolean removePairing() {
            this.serverEntity.removePairing(this.target);
            return true;
        }
    }
}
