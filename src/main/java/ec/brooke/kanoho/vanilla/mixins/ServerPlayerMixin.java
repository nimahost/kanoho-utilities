package ec.brooke.kanoho.vanilla.mixins;

import ec.brooke.kanoho.features.components.EntityComponents;
import ec.brooke.kanoho.framework.SwingCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(method = "disconnect", at = @At("HEAD"))
    private void disconnect(CallbackInfo ci) {
        EntityComponents.COOLDOWNS.remove(this);
    }

    @Inject(method = "swing", at = @At("HEAD"))
    private void swing(InteractionHand interactionHand, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        SwingCallback.EVENT.invoker().swing(player, player.level(), interactionHand);
    }
}
