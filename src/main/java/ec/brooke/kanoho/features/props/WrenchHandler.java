package ec.brooke.kanoho.features.props;

import com.mojang.serialization.Codec;
import ec.brooke.kanoho.framework.components.ComponentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

import static ec.brooke.kanoho.features.components.ItemComponents.PROP;

public class WrenchHandler {
    private static final ComponentType<Boolean> WRENCH = new ComponentType<>("wrench", Codec.BOOL);
    private static final SoundEvent SOUND = SoundEvents.COPPER_BULB_TURN_ON;
    private static final double FIND_RADIUS = 0.75;
    private static final double FIND_DISTANCE = 5;

    private final HashMap<Player, WrenchState> state = new HashMap<>();

    public void register() {
        UseItemCallback.EVENT.register(this::onUseItem);
        ServerTickEvents.END_SERVER_TICK.register(this::tick);
    }

    private void tick(MinecraftServer server) {
        this.state.values().removeIf(state -> {
            if (
                state.player.isRemoved()
                || state.prop.isRemoved()
                || !WRENCH.from(state.player.getMainHandItem()).orElse(false)
            ) return state.cleanup();

            return false;
        });
    }

    private InteractionResult onUseItem(Player player, Level level, InteractionHand hand) {
        if (!player.mayBuild() || !WRENCH.from(player.getItemInHand(hand)).orElse(false)) return InteractionResult.PASS;

        @Nullable WrenchState state = this.state.get(player);
        if (state != null && state.selected != null) {
            player.playNotifySound(SOUND, player.getSoundSource(), 1, state.dragging ? 0.75f : 1);
            state.toggleDragging();
        } else {
            Display.ItemDisplay prop = findProp(player, level);

            if (prop != null) {
                if (state != null && state.prop == prop) {
                    player.playNotifySound(SOUND, player.getSoundSource(), 1, 2);
                    state.cycle();
                } else {
                    player.playNotifySound(SOUND, player.getSoundSource(), 1, 2);
                    WrenchState old = this.state.put(player, new WrenchState(player, prop));
                    if (old != null) old.cleanup();
                }
            } else if (state != null) {
                player.playNotifySound(SOUND, player.getSoundSource(), 1, 2);
                this.state.remove(state.player);
                state.cleanup();
            }
        }

        return InteractionResult.SUCCESS;
    }

    private @Nullable Display.ItemDisplay findProp(Player player, Level world) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        // Broad AABB to cheaply cull candidates before the precise check
        AABB searchBox = AABB.ofSize(eyePos, FIND_DISTANCE * 2, FIND_DISTANCE * 2, FIND_DISTANCE * 2);

        List<Display.ItemDisplay> candidates = world.getEntitiesOfClass(Display.ItemDisplay.class, searchBox,
                e -> PROP.from(e.getItemStack()).orElse(false)
        );

        Display.ItemDisplay closest = null;
        double closestDist = Double.MAX_VALUE;

        for (Display.ItemDisplay entity : candidates) {
            Vec3 toEntity = entity.getPosition(0).subtract(eyePos);

            // How far along the look direction this entity is
            double along = toEntity.dot(lookVec);

            // Must be in front of the player and within 4 blocks
            if (along < 0 || along > FIND_DISTANCE) continue;

            // Perpendicular offset from the look axis
            Vec3 projected = lookVec.multiply(along, along, along);
            double perpendicular = toEntity.subtract(projected).length();

            // Must be within the 1-block-wide tunnel
            if (perpendicular > FIND_RADIUS) continue;

            // Closest along the look direction wins
            double distance = along + perpendicular;
            if (distance < closestDist) {
                closestDist = distance;
                closest = entity;
            }
        }

        return closest;
    }
}
