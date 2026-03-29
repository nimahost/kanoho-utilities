package ec.brooke.kanoho.features.props;

import com.mojang.serialization.Codec;
import ec.brooke.kanoho.Kanoho;
import ec.brooke.kanoho.framework.SwingCallback;
import ec.brooke.kanoho.framework.components.ComponentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class WrenchHandler {
    private static final List<GizmoMode> MODES = List.of(
            new GizmoMode("move", List.of(
                    state -> new WrenchMoveGizmo(state, Direction.Axis.X, false),
                    state -> new WrenchMoveGizmo(state, Direction.Axis.Y, false),
                    state -> new WrenchMoveGizmo(state, Direction.Axis.Z, false),
                    state -> new WrenchMoveGizmo(state, Direction.Axis.X, true),
                    state -> new WrenchMoveGizmo(state, Direction.Axis.Y, true),
                    state -> new WrenchMoveGizmo(state, Direction.Axis.Z, true)
            )),
            new GizmoMode("rotate", List.of(
                    state -> new WrenchRotateGizmo(state, Direction.Axis.X),
                    state -> new WrenchRotateGizmo(state, Direction.Axis.Y),
                    state -> new WrenchRotateGizmo(state, Direction.Axis.Z)
            ))
    );

    private static final ComponentType<Boolean> WRENCH = new ComponentType<>("wrench", Codec.BOOL);
    private static final SoundEvent CHANGE_SOUND = SoundEvents.COPPER_BULB_TURN_ON;
    private static final double FIND_RADIUS = 0.75;
    private static final double FIND_DISTANCE = 5;
    private static final int COOLDOWN = 3;

    private final HashMap<Player, WrenchState> players = new HashMap<>();

    public void register() {
        ServerTickEvents.END_SERVER_TICK.register(this::tick);
        UseItemCallback.EVENT.register(this::onUseItem);
        SwingCallback.EVENT.register(this::swing);
    }

    private void tick(MinecraftServer server) {
        HashSet<Player> players = new HashSet<>(server.getPlayerList().getPlayers());
        players.addAll(this.players.keySet());

        for (Player player : players) {
            boolean wrench = player.isRemoved() || WRENCH.from(player.getMainHandItem()).orElse(false);

            if (wrench) this.players.computeIfAbsent(player, WrenchState::new).decrementCooldown();
            else removeState(player);
        }
    }

    private void swing(Player player, Level level, InteractionHand hand) {
        WrenchState state = this.players.get(player);
        if (state == null || state.cooldown()) return;

        if (state.isDragging()) state.cancelDrag();
        else {
            Display target = findProp(player, level);
            if (target != null && (!state.hasSelected() || target == state.selection)) {
                if (state.hasSelected()) removeState(player);
                PropSystem.remove(level, target);
            } else if (state.hasSelected()) state.deselectProp();
        }
    }

    private InteractionResult onUseItem(Player player, Level level, InteractionHand hand) {
        WrenchState state = this.players.get(player);
        if (state == null || state.cooldown()) return InteractionResult.PASS;

        if (state.hovered != null) state.toggleDragging();
        else {
            Display target = findProp(player, level);

            if (target != null) {
                if (state.selection == target) state.cycleMode();
                else state.selectProp(target);
            } else if (state.hasSelected()) state.deselectProp();
        }

        return InteractionResult.SUCCESS;
    }

    private @Nullable Display findProp(Player player, Level level) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        // Broad AABB to cheaply cull candidates before the precise check
        AABB searchBox = AABB.ofSize(eyePos, FIND_DISTANCE * 2, FIND_DISTANCE * 2, FIND_DISTANCE * 2);

        List<Display> candidates = level.getEntitiesOfClass(Display.class, searchBox,
                e -> e.getTags().contains(PropSystem.PROP_TAG)
        );

        Display closest = null;
        double closestDist = Double.MAX_VALUE;

        for (Display entity : candidates) {
            Vec3 toEntity = entity.getPosition(0).subtract(eyePos);

            // How far along the look direction this entity is
            double along = toEntity.dot(lookVec);

            // Must be in front of the player and within 4 blocks
            if (along < 0 || along > FIND_DISTANCE) continue;

            // Perpendicular offset from the look axis
            Vec3 projected = lookVec.multiply(along, along, along);
            double perpendicular = toEntity.subtract(projected).length();

            // Must be within the 1-block-wide tunnel
            if (perpendicular > FIND_RADIUS) continue;

            // Closest along the look direction wins
            double distance = along + perpendicular;
            if (distance < closestDist) {
                closestDist = distance;
                closest = entity;
            }
        }

        return closest;
    }

    private void removeState(Player player) {
        WrenchState old = this.players.remove(player);
        if (old != null) old.cleanup();
    }

    public static class WrenchState {
        @Nullable public WrenchGizmo hovered;
        @Nullable public Display selection;
        public final Player player;
        public boolean dragging;

        private List<WrenchGizmo> gizmos;
        private int cooldown;
        private int mode;

        public WrenchState(Player player) {
            this.gizmos = List.of();
            this.player = player;
        }

        public void decrementCooldown() {
            if (cooldown > 0) cooldown--;
        }

        public boolean cooldown() {
            if (cooldown == 0) {
                cooldown = COOLDOWN;
                return false;
            } else return true;
        }

        public void selectProp(Display prop) {
            player.playNotifySound(CHANGE_SOUND, player.getSoundSource(), 1, 2);
            this.selection = prop;
            spawnGizmos();
        }

        public void deselectProp() {
            player.playNotifySound(CHANGE_SOUND, player.getSoundSource(), 1, 2);
            this.selection = null;
            removeGizmos();
        }

        public void cleanup() {
            this.removeGizmos();
            this.selection = null;
        }

        public void removeGizmos() {
            dragging = false;
            hovered = null;
            gizmos.forEach((gizmo) -> gizmo.remove(Entity.RemovalReason.DISCARDED));
        }

        public void toggleDragging() {
            dragging = !dragging;
            if (dragging && hovered != null) hovered.startDrag();
            gizmos.forEach((gizmo) -> gizmo.setInvisible(gizmo != hovered && dragging));
            player.playNotifySound(CHANGE_SOUND, player.getSoundSource(), 1, dragging ? 1 : 0.75f);
        }

        public void cancelDrag() {
            player.playNotifySound(CHANGE_SOUND, player.getSoundSource(), 1, 0.75f);
            if (hovered != null) hovered.cancelDrag();
            toggleDragging();
        }

        public void cycleMode() {
            player.playNotifySound(CHANGE_SOUND, player.getSoundSource(), 1, 2);
            this.mode = (mode + 1) % MODES.size();
            spawnGizmos();
        }

        public boolean hasSelected() {
            return this.selection != null;
        }

        public boolean isDragging() {
            return this.dragging;
        }

        public void spawnGizmos() {
            this.removeGizmos();
            this.gizmos = MODES.get(mode).gizmos.stream().map(
                    factory -> Kanoho.ephemerality.add(factory.create(this), (ServerPlayer) player)
            ).toList();
        }
    }

    private record GizmoMode(String name, List<GizmoFactory> gizmos) {
        @FunctionalInterface
        public interface GizmoFactory {
            WrenchGizmo create(WrenchState state);
        }
    }
}
