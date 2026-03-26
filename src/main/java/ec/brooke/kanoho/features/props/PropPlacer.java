package ec.brooke.kanoho.features.props;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

import static ec.brooke.kanoho.features.components.ItemComponents.PROP;

public class PropPlacer {
    private static final SoundEvent SOUND = SoundEvents.WOOL_PLACE;

    public void register() {
        UseItemCallback.EVENT.register(this::onUseItem);
    }

    private InteractionResult onUseItem(Player player, Level level, InteractionHand hand) {
        if (!player.mayBuild() || !PROP.from(player.getItemInHand(hand)).orElse(false)) return InteractionResult.PASS;

        ClipContext context = new ClipContext(
                player.getEyePosition(),
                player.getLookAngle().scale(5).add(player.getEyePosition()),
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        );

        BlockHitResult hit = level.clip(context);
        if (hit.getType() == BlockHitResult.Type.MISS) return InteractionResult.PASS;
        Vec3 position = hit.getLocation().subtract(player.getLookAngle()).align(EnumSet.allOf(Direction.Axis.class)).add(0.5, 0.5, 0.5);

        ItemStack held = player.getItemInHand(hand);

        Display.ItemDisplay entity = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, level);
        entity.forceSetRotation(-player.getYRot(), 0);
        entity.setItemStack(held);
        entity.setPos(position);

        player.swing(hand);
        level.playSound(null, position.x, position.y, position.z, SOUND, SoundSource.BLOCKS,1f, 1f);
        ((ServerLevel) level).sendParticles(
                new ItemParticleOption(ParticleTypes.ITEM, held),
                position.x, position.y, position.z,
                50, // Count
                0.25, 0.25, 0.25, // Offset
                0.1 // Speed
        );

        level.addFreshEntity(entity);
        return InteractionResult.SUCCESS;
    }

}
