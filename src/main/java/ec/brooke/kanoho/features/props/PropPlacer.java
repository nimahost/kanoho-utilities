package ec.brooke.kanoho.features.props;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

import static ec.brooke.kanoho.features.components.ItemComponents.PROP;

public class PropPlacer {
    private static final SoundEvent SOUND = SoundEvents.ARMOR_STAND_PLACE;

    public void register() {
        UseBlockCallback.EVENT.register(this::onUseBlock);
    }

    private InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
        if (!player.mayBuild() || !PROP.from(player.getItemInHand(hand)).orElse(false)) return InteractionResult.PASS;

        if (hit.getType() == BlockHitResult.Type.MISS) return InteractionResult.PASS;
        Vec3 position = hit.getLocation()
                .subtract(player.getLookAngle().scale(0.01))
                .align(EnumSet.allOf(Direction.Axis.class))
                .add(0.5, 0.5, 0.5);

        ItemStack held = player.getItemInHand(hand);

        Display.ItemDisplay entity = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, level);
        entity.setItemStack(held);
        entity.setPos(position);

        player.swing(hand);
        level.playSound(null, position.x, position.y, position.z, SOUND, SoundSource.BLOCKS,1f, 1f);

        level.addFreshEntity(entity);
        return InteractionResult.SUCCESS;
    }
}
