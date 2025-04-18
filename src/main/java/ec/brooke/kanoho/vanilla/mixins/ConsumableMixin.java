package ec.brooke.kanoho.vanilla.mixins;

import ec.brooke.kanoho.components.EntityComponents;
import ec.brooke.kanoho.components.ItemComponents;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Consumable.class)
public abstract class ConsumableMixin {

    // Handle updating player status and preventing item consumption if component set
    @Inject(method = "onConsume", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;consume(ILnet/minecraft/world/entity/LivingEntity;)V"))
    private void cancelConsume(Level level, LivingEntity entity, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (entity instanceof ServerPlayer player) {
            EntityComponents.CONSUMED.to(player, stack);

            ItemComponents.CONSUMED.from(stack).ifPresent(consumed -> {
                if (consumed || player.hasInfiniteMaterials()) return;
                player.containerMenu.broadcastFullState();
                cir.setReturnValue(stack);
                cir.cancel();
            });
        }
    }

    @Accessor
    protected abstract float getConsumeSeconds();

    // If the consume time is zero, set to non-existent namespace to prevent
    @Inject(method = "sound", cancellable = true, at = @At(value = "RETURN"))
    private void cancelConsume(CallbackInfoReturnable<Holder<SoundEvent>> cir) {
        if (getConsumeSeconds() != 0) return;
        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath("", "");
        cir.setReturnValue(new Holder.Direct<>(SoundEvent.createFixedRangeEvent(rl, 0)));
    }
}
