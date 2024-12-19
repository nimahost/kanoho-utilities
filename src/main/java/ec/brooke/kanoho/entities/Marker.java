package ec.brooke.kanoho.entities;

import com.mojang.math.Transformation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * A display entity meant to mark a position in the world for a single player.
 */
public class Marker extends Display.ItemDisplay {
    public static final int DETACH_DISTANCE = 16*4;

    private final Player target;
    private final Vec3 anchor;

    public Marker(Player subject, Vec3 anchor) {
        super(EntityType.ITEM_DISPLAY, subject.level());
        this.target = subject;
        this.anchor = anchor;

        // Setup
        setBillboardConstraints(BillboardConstraints.CENTER);
        setItemStack(Items.SLIME_BALL.getDefaultInstance());
        setPosRotInterpolationDuration(2);
        setGlowingTag(true);
    }

    @Override
    public void tick() {
        Vec3 eye = target.getEyePosition();
        Vec3 pos = anchor;

        float distance = (float) eye.distanceTo(pos);
        float scale = Mth.sqrt(distance);

        // Keep within render distance
        if (distance >= DETACH_DISTANCE) {
            float d = DETACH_DISTANCE / distance;
            scale *= DETACH_DISTANCE / distance;

            pos = new Vec3(
                    eye.x - ((eye.x - pos.x) * d),
                    eye.y - ((eye.y - pos.y) * d),
                    eye.z - ((eye.z - pos.z) * d)
            );
        }

        // Update position and scale
        if (position() != pos) setPos(pos);
        setTransformation(new Transformation(
                new Vector3f(0,0,0),
                new Quaternionf(),
                new Vector3f(scale, scale, 0),
                new Quaternionf()
        ));

        super.tick();
    }
}
