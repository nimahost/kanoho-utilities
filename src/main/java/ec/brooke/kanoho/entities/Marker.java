package ec.brooke.kanoho.entities;

import com.mojang.math.Transformation;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Marker extends Display.ItemDisplay {
    private final Player subject;

    public Marker(Level level, Player subject) {
        super(EntityType.ITEM_DISPLAY, level);
        this.subject = subject;

        setBillboardConstraints(Display.BillboardConstraints.CENTER);
        setItemStack(Items.SLIME_BALL.getDefaultInstance());
        setGlowingTag(true);
    }

    @Override
    public void tick() {
        setGlowColorOverride(getGlowColorOverride() + 500);
        float d = (float) subject.getEyePosition().distanceTo(position());
        setTransformation(new Transformation(
                new Vector3f(0, 0, 0),
                new Quaternionf(),
                new Vector3f(d, d, d).div(4),
                new Quaternionf()
        ));

        super.tick();
    }
}
