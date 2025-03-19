package ec.brooke.kanoho.vanilla.mixins;

import ec.brooke.kanoho.Kanoho;
import ec.brooke.kanoho.components.KanohoComponents;
import ec.brooke.kanoho.vanilla.IServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements IServerPlayer {

    @Unique
    private final CompoundTag status = new CompoundTag();

    @Override
    public void kanoho$putStatus(CompoundTag tag) {
        tag.put(Kanoho.MOD_ID, status);
    }

    @Override
    public CompoundTag kanoho$getStatus() {
        return status;
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void injectStatus(CallbackInfo ci) {
        // Reset consumed status at the end of the next tick
        status.remove(KanohoComponents.CONSUMED.location());
    }

}
