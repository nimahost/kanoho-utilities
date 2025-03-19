package ec.brooke.kanoho.vanilla.mixins;

import ec.brooke.kanoho.vanilla.IServerPlayer;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NbtPredicate.class)
public abstract class NbtPredicateMixin {
    @Inject(method = "getEntityTagToCompare", at = @At(value = "RETURN"))
    private static void injectStatus(Entity entity, CallbackInfoReturnable<CompoundTag> cir) {
        if (entity instanceof ServerPlayer player) ((IServerPlayer) player).kanoho$putStatus(cir.getReturnValue());
    }
}
