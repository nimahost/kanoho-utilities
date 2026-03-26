package ec.brooke.kanoho.features.props;

import com.mojang.serialization.Codec;
import ec.brooke.kanoho.Kanoho;
import ec.brooke.kanoho.framework.components.ComponentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class WrenchHandler {
    private static final ComponentType<Boolean> WRENCH = new ComponentType<>("wrench", Codec.BOOL);
    private static final double FIND_RADIUS = 0.75;
    private static final double FIND_DISTANCE = 5;

    private final HashMap<Player, WrenchState> state = new HashMap<>();

    public void register() {
        UseItemCallback.EVENT.register(this::onUseItem);
        ServerTickEvents.END_WORLD_TICK.register(this::tick);
    }

    private void tick(ServerLevel server) {
        this.state.entrySet().removeIf(entry -> {
            WrenchState state = entry.getValue();
            Player player = entry.getKey();

            if (player.isRemoved() || state.prop.isRemoved()) return true;

            if (!WRENCH.from(player.getMainHandItem()).orElse(false)) end(state);
            else if (!state.dragging) state.selected = state.gizmos.stream().filter(IWrenchGizmo::isHovered).findFirst().orElse(null);
            state.gizmos.forEach(gizmo -> gizmo.setSelected(gizmo == state.selected));
            if (state.dragging) state.selected.drag();

            return false;
        });
    }

    private InteractionResult onUseItem(Player player, Level level, InteractionHand hand) {
        if (!player.mayBuild() || !WRENCH.from(player.getItemInHand(hand)).orElse(false)) return InteractionResult.PASS;

        @Nullable WrenchState state = this.state.get(player);
        if (state == null) {
            Display.ItemDisplay prop = findProp(player, level);
            if (prop == null) return InteractionResult.PASS;

            WrenchState newstate = new WrenchState();
            newstate.player = player;
            newstate.prop = prop;
            newstate.gizmos = List.of(
                Kanoho.ephemerality.add(new WrenchMoveGizmo(player, prop, Direction.Axis.X, false), (ServerPlayer) player),
                Kanoho.ephemerality.add(new WrenchMoveGizmo(player, prop, Direction.Axis.Y, false), (ServerPlayer) player),
                Kanoho.ephemerality.add(new WrenchMoveGizmo(player, prop, Direction.Axis.Z, false), (ServerPlayer) player),
                Kanoho.ephemerality.add(new WrenchMoveGizmo(player, prop, Direction.Axis.X, true), (ServerPlayer) player),
                Kanoho.ephemerality.add(new WrenchMoveGizmo(player, prop, Direction.Axis.Y, true), (ServerPlayer) player),
                Kanoho.ephemerality.add(new WrenchMoveGizmo(player, prop, Direction.Axis.Z, true), (ServerPlayer) player)
            );

            this.state.put(player, newstate);
        } else {
            if (state.selected != null) {
                state.dragging = !state.dragging;
                if (state.dragging) state.selected.startDrag();
                else state.selected.stopDrag();
            } else end(state);
        }

        return InteractionResult.SUCCESS;
    }

    private void end(WrenchState state) {
        state.gizmos.forEach(IWrenchGizmo::remove);
        this.state.remove(state.player);
    }

    private @Nullable Display.ItemDisplay findProp(Player player, Level world) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        // Broad AABB to cheaply cull candidates before the precise check
        AABB searchBox = AABB.ofSize(eyePos, FIND_DISTANCE * 2, FIND_DISTANCE * 2, FIND_DISTANCE * 2);

        List<Display.ItemDisplay> candidates = world.getEntitiesOfClass(Display.ItemDisplay.class, searchBox,
                e -> e.getTags().contains("prop")
        );

        Display.ItemDisplay closest = null;
        double closestDist = Double.MAX_VALUE;

        for (Display.ItemDisplay entity : candidates) {
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

    static class WrenchState {
        public Player player;
        public Display.ItemDisplay prop;
        public List<IWrenchGizmo> gizmos;
        public IWrenchGizmo selected;
        public boolean dragging;
    }
}
