package ec.brooke.kanoho.features.props;

import ec.brooke.kanoho.Kanoho;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WrenchState {
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

    @Nullable public WrenchGizmo selected;
    public Display.ItemDisplay prop;
    public boolean dragging;
    public Player player;

    private List<WrenchGizmo> gizmos;
    private int mode;

    public WrenchState(Player player, Display.ItemDisplay prop) {
        this.gizmos = List.of();
        this.player = player;
        this.prop = prop;

        this.mode = -1;
        this.cycle();
    }

    public boolean cleanup() {
        gizmos.forEach((gizmo) -> gizmo.remove(Entity.RemovalReason.DISCARDED));
        dragging = false;
        selected = null;
        return true;
    }

    public void toggleDragging() {
        dragging = !dragging;
        gizmos.forEach((gizmo) -> gizmo.setInvisible(gizmo != selected && dragging));
        if (dragging && selected != null) selected.startDrag();
    }

    public void cycle() {
        this.cleanup();
        this.mode = (mode + 1) % MODES.size();
        this.gizmos = MODES.get(mode).gizmos.stream().map(
                factory -> Kanoho.ephemerality.add(factory.create(this), (ServerPlayer) player)
        ).toList();
    }

    private record GizmoMode(String name, List<GizmoFactory> gizmos) {
        @FunctionalInterface
        public interface GizmoFactory {
            WrenchGizmo create(WrenchState state);
        }
    }
}
