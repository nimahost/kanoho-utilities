package ec.brooke.kanoho.vanilla.mixins;

import ec.brooke.kanoho.framework.RawInteractCallback;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerMixin {
    @Inject(method = "handleInteract", at = @At("HEAD"))
    public void handleInteract(ServerboundInteractPacket packet, CallbackInfo ci) {
        packet.dispatch(new ServerboundInteractPacket.Handler() {

            @Override
            public void onInteraction(InteractionHand interactionHand) {
                RawInteractCallback.EVENT.invoker().interact(getPlayer(), getPlayer().level(), RawInteractCallback.Action.USE);
            }

            @Override
            public void onInteraction(InteractionHand interactionHand, Vec3 vec3) {
                RawInteractCallback.EVENT.invoker().interact(getPlayer(), getPlayer().level(), RawInteractCallback.Action.USE);
            }

            @Override
            public void onAttack() {
                RawInteractCallback.EVENT.invoker().interact(getPlayer(), getPlayer().level(), RawInteractCallback.Action.ATTACK);
            }
        });
    }

    @Accessor
    public abstract ServerPlayer getPlayer();
}
