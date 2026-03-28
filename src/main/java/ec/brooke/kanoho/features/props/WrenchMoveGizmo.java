package ec.brooke.kanoho.features.props;

import com.mojang.math.Transformation;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.EnumSet;

public class WrenchMoveGizmo extends WrenchGizmo {
    private static final double SNAP = 0.25;

    private Vec3 initialPropPos;
    private Vec3 clickOffset;

    public WrenchMoveGizmo(WrenchState state, Direction.Axis axis, boolean plane) {
        super(state, axis, "move" + (plane ? "_plane" : ""), plane);

        this.setTransformation(new Transformation(
                new Vector3f(0f, 0f, 0f),
                axis.getPositive().getRotation(),
                new Vector3f(1f, 1f, 1f),
                new Quaternionf(0f, 0f, 0f, 1f)
        ));
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 position;
        if (plane) position = new Vec3(1,1,1).subtract(axis.getPositive().getUnitVec3()).scale(0.75f);
        else position = axis.getPositive().getUnitVec3();

        Vec3 player = state.player.getEyePosition().subtract(state.prop.position());
        Vec3 sign = new Vec3(Math.signum(player.x), Math.signum(player.y), Math.signum(player.z));

        this.setPos(state.prop.position().add(position.multiply(sign)));
    }

    @Override
    public void startDrag() {
        initialPropPos = state.prop.position();
        clickOffset = calculateOffset(initialPropPos);
    }

    @Override
    public void cancelDrag() {
        state.prop.setPos(initialPropPos);
    }

    @Override
    protected void drag() {
        Vec3 pos = initialPropPos.add(calculateOffset(initialPropPos)).subtract(clickOffset);

        if (!state.player.isShiftKeyDown()) {
            EnumSet<Direction.Axis> axes = EnumSet.of(this.axis);
            if (plane) axes = EnumSet.complementOf(axes);
            pos = pos.scale(1 / SNAP).align(axes).scale(SNAP);
        }

        state.prop.setPos(pos);
    }
}
