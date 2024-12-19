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
    public static final double SIZE = 0.25;

    private final Player subject;
    private final Vec3 anchor;

    public Marker(Player subject, Vec3 anchor) {
        super(EntityType.ITEM_DISPLAY, subject.level());
        this.subject = subject;
        this.anchor = anchor;

        // Setup
        setBillboardConstraints(BillboardConstraints.CENTER);
        setItemStack(Items.SLIME_BALL.getDefaultInstance());
        setPosRotInterpolationDuration(2);
        setGlowingTag(true);
    }

    @Override
    public void tick() {
        Vec3 eye = subject.getEyePosition();
        Vec3 pos = anchor;

        // Keep within render distance
        double distance = eye.distanceTo(pos);
        if (distance >= DETACH_DISTANCE) {
            double d = DETACH_DISTANCE / distance;
            distance = DETACH_DISTANCE;

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
                new Vector3f(
                        (float) (distance * SIZE),
                        (float) (distance * SIZE),
                        (float) (distance * SIZE)
                ),
                new Quaternionf()
        ));

        super.tick();
    }
}
