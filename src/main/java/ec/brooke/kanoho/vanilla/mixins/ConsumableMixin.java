package ec.brooke.kanoho.vanilla.mixins;

import ec.brooke.kanoho.components.KanohoComponents;
import ec.brooke.kanoho.vanilla.IServerPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Consumable.class)
public abstract class ConsumableMixin {

    // Handle updating player status and preventing item consumption if component set
    @Inject(method = "onConsume", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;consume(ILnet/minecraft/world/entity/LivingEntity;)V"))
    private void cancelConsume(Level level, LivingEntity entity, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (entity instanceof ServerPlayer player) {
            ((IServerPlayer) player).kanoho$getStatus().put(KanohoComponents.CONSUMED.location(), stack.save(player.registryAccess()));
            KanohoComponents.CONSUMED.from(stack).ifPresent(consumed -> {
                if (consumed) return;
                player.containerMenu.broadcastFullState();
                cir.setReturnValue(stack);
                cir.cancel();
            });
        }
    }

}
