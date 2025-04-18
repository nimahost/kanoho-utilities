package ec.brooke.kanoho.features.markers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class PointMarker extends Marker {

    public PointMarker(Player subject, Vec3 anchor) {
        super(subject, anchor);
    }
}
