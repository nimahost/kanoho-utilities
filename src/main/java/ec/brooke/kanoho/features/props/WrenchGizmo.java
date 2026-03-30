package ec.brooke.kanoho.features.props;

import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public abstract class WrenchGizmo extends Display.ItemDisplay {
    protected static final Vec3 ONE = new Vec3(1, 1, 1);
    private static final double VIEW_ANGLE = 60;
    private static final double SIN_VIEW_ANGLE = Math.sin(Math.toRadians(VIEW_ANGLE));
    private static final double COS_VIEW_ANGLE =  Math.cos(Math.toRadians(VIEW_ANGLE));
    private static final double CLAMP = 16;

    protected final WrenchHandler.WrenchState state;
    protected final Direction.Axis axis;
    protected final boolean plane;
    private final ItemStack item;

    public WrenchGizmo(WrenchHandler.WrenchState state, Direction.Axis axis, String model, boolean plane) {
        super(EntityType.ITEM_DISPLAY, state.selection.level());
        this.state = state;
        this.plane = plane;
        this.axis = axis;

        setGlowingTag(true);
        item = new ItemStack(Items.WOODEN_SWORD);
        item.applyComponents(DataComponentMap.builder().set(
                DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(
                        List.of(),
                        List.of(),
                        List.of("gizmo_" + model),
                        List.of(getColor())
                )).build());
        setItemStack(item);
    }

    protected int getColor() {
        return switch (axis) {
            case X -> 0xFF0000;
            case Y -> 0x00FF00;
            case Z -> 0x0000FF;
        };
    }

    protected final Vec3 axisVec() {
        return switch (axis) {
            case X -> new Vec3(1, 0, 0);
            case Y -> new Vec3(0, 1, 0);
            case Z -> new Vec3(0, 0, 1);
        };
    }

    @Override
    public void tick() {
        super.tick();

        if (!state.dragging) {
            boolean hovered = (state.hovered == null || state.hovered == this) && isHovered();

            this.setGlowColorOverride(hovered ? -1 : getColor());

            if (hovered) state.hovered = this;
            else if (state.hovered == this) state.hovered = null;
        } else if (state.hovered == this) drag();
    }

    private boolean isHovered() {
        Vec3 eyePos = state.player.getEyePosition();
        Vec3 lookVec = state.player.getViewVector(1.0f).normalize();

        Vec3 toTarget = this.position().subtract(eyePos);

        double threshold = (plane) ? COS_VIEW_ANGLE : SIN_VIEW_ANGLE;
        if (Math.abs(axis.getPositive().getUnitVec3().normalize().dot(lookVec)) > threshold != plane) return false;

        // Project toTarget onto the look ray to find the closest point
        double t = toTarget.dot(lookVec);

        // Target is behind the player
        if (t < 0) return false;

        Vec3 closestPoint = eyePos.add(lookVec.scale(t));

        return closestPoint.distanceTo(this.position()) <= 0.5f;
    }

    public abstract void startDrag();

    public abstract void cancelDrag();

    protected abstract void drag();

    protected Vec3 calculateOffset(Vec3 initial) {
        Vec3 relativePos = state.player.getEyePosition().subtract(initial);
        Vec3 axis = this.axis.getPositive().getUnitVec3();

        Vec3 normal = axis;
        if (!plane) {
            Vec3 axisVec = this.axis.getPositive().getUnitVec3();
            normal = axisVec.cross(relativePos).cross(axisVec).normalize();
        }

        Vec3 direction = state.player.getViewVector(1.0f).normalize();
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

    @Override
    public void setInvisible(boolean bl) {
        setItemStack(bl ? ItemStack.EMPTY : item);
        super.setInvisible(bl);
    }
}
