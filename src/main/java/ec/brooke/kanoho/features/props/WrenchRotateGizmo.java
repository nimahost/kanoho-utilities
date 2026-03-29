package ec.brooke.kanoho.features.props;

import com.mojang.math.Transformation;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class WrenchRotateGizmo extends WrenchGizmo {
    private Transformation initialPropTransform;
    private Vec3 initialClickPos;

    public WrenchRotateGizmo(WrenchHandler.WrenchState state, Direction.Axis axis) {
        super(state, axis, "rotate", true);
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 plane = ONE.subtract(axisVec());
        Vec3 player = state.player.getEyePosition().subtract(state.selection.position());
        this.setPos(state.selection.position().add(player.multiply(plane).normalize()));

        Quaternionf rotation = switch (axis) {
            case X -> new Quaternionf().rotateX((float) (-Math.atan2(player.y, player.z) + Math.PI / 2));
            case Y -> new Quaternionf().rotateY((float) (-Math.atan2(player.x, -player.z)));
            case Z -> new Quaternionf().rotateZ((float) (Math.atan2(player.y, player.x) - Math.PI / 2));
        };

        this.setTransformation(new Transformation(
                new Vector3f(0f, 0f, 0f),
                rotation.mul(axis.getPositive().getRotation()),
                new Vector3f(1f, 1f, 1f),
                new Quaternionf(0f, 0f, 0f, 1f)
        ));
    }

    @Override
    public void startDrag() {
        initialPropTransform = ItemDisplay.createTransformation(state.selection.getEntityData());
        initialClickPos = calculateOffset(state.selection.position()).normalize();
    }

    @Override
    public void cancelDrag() {
        state.selection.setTransformation(initialPropTransform);
    }

    @Override
    protected void drag() {
        Vec3 currentClickPos = calculateOffset(state.selection.position()).normalize();
        double parallel = initialClickPos.dot(currentClickPos);
        Vec3 perpendicularAxis = axis.getPositive().getUnitVec3().cross(initialClickPos);
        double perpendicular = perpendicularAxis.dot(currentClickPos);
        double angle = Math.atan2(perpendicular, parallel);
        Quaternionf result = new Quaternionf().rotateAxis((float) angle, axis.getPositive().getUnitVec3().toVector3f());

        state.selection.setTransformation(new Transformation(
            initialPropTransform.getTranslation(),
            result.mul(initialPropTransform.getLeftRotation()),
            initialPropTransform.getScale(),
            initialPropTransform.getRightRotation()
        ));
    }
}
