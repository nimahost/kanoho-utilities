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
    @Nullable public WrenchGizmo selected;
    public Display.ItemDisplay prop;
    public boolean dragging;
    public Player player;

    private List<WrenchGizmo> gizmos;

    public WrenchState(Player player, Display.ItemDisplay prop) {
        this.gizmos = List.of(
                new WrenchRotateGizmo(this, Direction.Axis.X),
                new WrenchRotateGizmo(this, Direction.Axis.Y),
                new WrenchRotateGizmo(this, Direction.Axis.Z)
        );
        this.player = player;
        this.prop = prop;

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
    }
}
