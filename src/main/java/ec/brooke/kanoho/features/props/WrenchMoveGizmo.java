package ec.brooke.kanoho.features.props;

import com.mojang.math.Transformation;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class WrenchMoveGizmo extends Display.ItemDisplay implements IWrenchGizmo {
    private static final double CLAMP = 16;

    private final Display.ItemDisplay prop;
    private final Direction.Axis axis;
    private final boolean plane;
    private final Player player;

    private Vec3 initialPropPos;
    private Vec3 clickOffset;

    public WrenchMoveGizmo(Player player, ItemDisplay prop, Direction.Axis axis, boolean plane) {
        super(EntityType.ITEM_DISPLAY, prop.level());
        this.player = player;
        this.plane = plane;
        this.prop = prop;
        this.axis = axis;

        this.setTransformation(new Transformation(
                new Vector3f(0f, 0f, 0f),
                axis.getPositive().getRotation(),
                new Vector3f(1f, 1f, 1f),
                new Quaternionf(0f, 0f, 0f, 1f)
        ));

        ItemStack stack = new ItemStack(Items.ARMADILLO_SCUTE);
        stack.applyComponents(DataComponentMap.builder().set(
                DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(
                List.of(),
                List.of(),
                List.of("gizmo_move" + (plane ? "_plane" : "")),
                List.of(getColor())
        )).build());
        setItemStack(stack);
    }

    private int getColor() {
        return switch (axis) {
            case X -> 0xFF0000;
            case Y -> 0x00FF00;
            case Z -> 0x0000FF;
        };
    }

    @Override
    public void tick() {
        Vec3 position;
        if (plane) position = new Vec3(1,1,1).subtract(axis.getPositive().getUnitVec3()).scale(0.75f);
        else position = axis.getPositive().getUnitVec3();

        Vec3 player = this.player.getEyePosition().subtract(this.prop.position());
        Vec3 sign = new Vec3(Math.signum(player.x), Math.signum(player.y), Math.signum(player.z));

        this.setPos(this.prop.position().add(position.multiply(sign)));
        super.tick();
    }

    @Override
    public boolean isHovered() {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0f).normalize();

        Vec3 toTarget = this.position().subtract(eyePos);

        if (Math.abs(axis.getPositive().getUnitVec3().normalize().dot(lookVec)) > 0.5f != plane) return false;

        // Project toTarget onto the look ray to find the closest point
        double t = toTarget.dot(lookVec);

        // Target is behind the player
        if (t < 0) return false;

        Vec3 closestPoint = eyePos.add(lookVec.scale(t));

        return closestPoint.distanceTo(this.position()) <= 0.5f;
    }

    @Override
    public void setSelected(boolean glowing) {
        this.setGlowingTag(glowing);
    }

    @Override
    public void startDrag() {
        this.setGlowColorOverride(getColor());
        initialPropPos = this.prop.position();
        clickOffset = calculate();
    }

    @Override
    public void drag() {
        Vec3 offset = calculate();
        this.prop.setPos(initialPropPos.add(offset).subtract(clickOffset));
    }

    @Override
    public void remove() {
        super.remove(RemovalReason.DISCARDED);
    }

    @Override
    public void stopDrag() {
        this.setGlowColorOverride(-1);
    }

    private Vec3 calculate() {
        Vec3 relativePos = this.player.getEyePosition().subtract(initialPropPos);
        Vec3 axis = this.axis.getPositive().getUnitVec3();

        Vec3 normal = axis;
        if (!plane) {
            Vec3 axisVec = this.axis.getPositive().getUnitVec3();
            normal = axisVec.cross(relativePos).cross(axisVec).normalize();
        }

        Vec3 direction = this.player.getViewVector(1.0f).normalize();
        double distance = normal.dot(relativePos);

        double x = normal.dot(direction);
        if (x * Math.signum(distance) >= 0)  return Vec3.ZERO;
        double scale = -distance / x;

        Vec3 offset = clamp(direction.scale(scale)).add(relativePos);

        if (!plane) offset = axis.scale(axis.dot(offset));
        return offset;
    }

    private Vec3 clamp(Vec3 offset) {
        return new Vec3(
                Math.max(-CLAMP, Math.min(CLAMP, offset.x)),
                Math.max(-CLAMP, Math.min(CLAMP, offset.y)),
                Math.max(-CLAMP, Math.min(CLAMP, offset.z))
        );
    }
}
