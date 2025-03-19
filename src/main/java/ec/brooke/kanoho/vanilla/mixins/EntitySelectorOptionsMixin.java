package ec.brooke.kanoho.vanilla.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import ec.brooke.kanoho.vanilla.IServerPlayer;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntitySelectorOptions.class)
public abstract class EntitySelectorOptionsMixin {

    @Inject(method = "method_9957", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtUtils;compareNbt(Lnet/minecraft/nbt/Tag;Lnet/minecraft/nbt/Tag;Z)Z"))
    private static void injectStatus(CompoundTag compoundTag, boolean bl, Entity entity, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) CompoundTag tag) {
        if (entity instanceof ServerPlayer player) ((IServerPlayer) player).kanoho$putStatus(tag);
    }

}
