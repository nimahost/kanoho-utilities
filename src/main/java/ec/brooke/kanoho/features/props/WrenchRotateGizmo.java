package ec.brooke.kanoho.features.props;

import com.mojang.math.Transformation;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.EnumSet;

public class WrenchRotateGizmo extends WrenchGizmo {
    private Transformation initialPropTransform;
    private Vec3 initialClickPos;
    private Quaternionf initialSwing;

    private static final double SNAP = Math.PI * 0.125;

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

        // Perform a Swing-Twist decomposition
        Quaternionf rotation = initialPropTransform.getLeftRotation();
        Vec3 rotationAxis = axis.getPositive().getUnitVec3();

        double dot = rotationAxis.dot(new Vec3(rotation.x, rotation.y, rotation.z));
        Vec3 projection = rotationAxis.scale(dot);
        Quaternionf twist = new Quaternionf(projection.x, projection.y, projection.z, rotation.w).normalize();
        initialSwing = rotation.premul(twist.invert());
    }

    @Override
    public void cancelDrag() {
        state.selection.setTransformation(initialPropTransform);
    }

    @Override
    protected void drag() {
        Vec3 currentClickPos = calculateOffset(state.selection.position()).normalize();

        Vec3 rotationAxis = axis.getPositive().getUnitVec3();

        double parallel = initialClickPos.dot(currentClickPos);
        Vec3 perpendicularAxis = rotationAxis.cross(initialClickPos);
        double perpendicular = perpendicularAxis.dot(currentClickPos);

        double angle = Math.atan2(perpendicular, parallel);

        Quaternionf result = new Quaternionf().rotateAxis((float) angle, rotationAxis.toVector3f());
        Quaternionf rotation = result.mul(initialPropTransform.getLeftRotation());

        if (!state.player.isShiftKeyDown()) {
            // Perform a Swing-Twist decomposition
            double dot = rotationAxis.dot(new Vec3(rotation.x, rotation.y, rotation.z));
            Vec3 projection = rotationAxis.scale(dot);
            Quaternionf twist = new Quaternionf(projection.x, projection.y, projection.z, rotation.w).normalize();

            // Round the angle to snap it
            double total_angle = 2 * Math.atan2(new Vec3(twist.x, twist.y, twist.z).length() * Math.signum(dot), twist.w);
            total_angle = Math.round(total_angle / SNAP) * SNAP;

            // Make a new twist rotation quaternion
            twist = new Quaternionf().rotateAxis((float) total_angle, rotationAxis.toVector3f());
            rotation = twist.mul(initialSwing);
        }

        state.selection.setTransformation(new Transformation(
                initialPropTransform.getTranslation(),
                rotation,
                initialPropTransform.getScale(),
                initialPropTransform.getRightRotation()
        ));
    }
}
