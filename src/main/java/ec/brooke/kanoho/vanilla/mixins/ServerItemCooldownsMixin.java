package ec.brooke.kanoho.vanilla.mixins;

import ec.brooke.kanoho.framework.components.EntityComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ServerItemCooldowns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Mixin(ServerItemCooldowns.class)
public abstract class ServerItemCooldownsMixin {

    @Accessor
    protected abstract ServerPlayer getPlayer();

    @Inject(method = "onCooldownStarted", at = @At("RETURN"))
    private void onCooldownStarted(ResourceLocation resourceLocation, int i, CallbackInfo ci) {
        EntityComponents.COOLDOWNS.update(getPlayer(), ArrayList::new, l ->
                Stream.concat(l.stream(), Stream.of(resourceLocation.toString())).toList()
        );
    }

    @Inject(method = "onCooldownEnded", at = @At("RETURN"))
    private void onCooldownEnded(ResourceLocation resourceLocation, CallbackInfo ci) {
        EntityComponents.COOLDOWNS.updateIfExists(getPlayer(), l ->
                l.stream().filter(Predicate.not(resourceLocation.toString()::equals)).toList()
        );
    }

}
