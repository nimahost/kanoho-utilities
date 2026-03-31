package ec.brooke.kanoho.features.props;

import com.mojang.serialization.Codec;
import ec.brooke.kanoho.framework.components.ComponentType;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class PropSystem {
    public static final ComponentType<Boolean> COMPONENT = new ComponentType<>("prop", Codec.BOOL);
    private static final SoundEvent REMOVE_SOUND = SoundEvents.ARMOR_STAND_BREAK;
    private static final SoundEvent PLACE_SOUND = SoundEvents.ARMOR_STAND_PLACE;
    public static final String PROP_TAG = "prop";

    public void register() {
        UseBlockCallback.EVENT.register(this::onUseBlock);
        new WrenchHandler().register();
    }

    private InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
        if (!player.mayBuild() || !COMPONENT.from(player.getItemInHand(hand)).orElse(false)) return InteractionResult.PASS;

        if (hit.getType() == BlockHitResult.Type.MISS) return InteractionResult.PASS;
        Vec3 position = hit.getLocation()
                .subtract(player.getLookAngle().scale(0.01))
                .align(EnumSet.allOf(Direction.Axis.class))
                .add(0.5, 0.5, 0.5);

        ItemStack held = player.getItemInHand(hand);

        Display.ItemDisplay entity = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, level);
        entity.setItemStack(held);
        entity.addTag(PROP_TAG);
        entity.setPos(position);

        player.swing(hand, true);
        level.playSound(null, position.x, position.y, position.z, PLACE_SOUND, SoundSource.BLOCKS,1f, 1f);

        level.addFreshEntity(entity);
        return InteractionResult.SUCCESS;
    }

    public static void remove(Level level, Display prop) {
        prop.remove(Entity.RemovalReason.KILLED);

        Vec3 position = prop.position();
        level.playSound(null, position.x, position.y, position.z, REMOVE_SOUND, SoundSource.BLOCKS,1f, 1f);

        if (prop instanceof Display.ItemDisplay itemDisplay) {
            ((ServerLevel) level).sendParticles(
                    new ItemParticleOption(ParticleTypes.ITEM, itemDisplay.getItemStack()),
                    position.x, position.y, position.z,
                    50, // Count
                    0.25, 0.25, 0.25, // Offset
                    0.1 // Speed
            );
        } else if (prop instanceof Display.BlockDisplay blockDisplay) {
            ((ServerLevel) level).sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, blockDisplay.getBlockState()),
                    position.x, position.y, position.z,
                    50, // Count
                    0.25, 0.25, 0.25, // Offset
                    0.1 // Speed
            );
        }
    }
}
