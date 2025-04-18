package ec.brooke.kanoho.vanilla.mixins;

import ec.brooke.kanoho.components.ComponentType;
import ec.brooke.kanoho.components.EntityComponents;
import ec.brooke.kanoho.vanilla.IComponentHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin implements IComponentHolder {

    @Unique
    private CompoundTag components = new CompoundTag();

    @Inject(method = "saveWithoutId", at = @At("HEAD"))
    private void saveComponents(CompoundTag compoundTag, CallbackInfoReturnable<CompoundTag> cir) {
        compoundTag.put(ComponentType.NAMESPACE, components);
    }

    @Inject(method = "load", at = @At("HEAD"))
    private void loadComponents(CompoundTag compoundTag, CallbackInfo ci) {
        components = compoundTag.getCompound(ComponentType.NAMESPACE);
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void injectStatus(CallbackInfo ci) {
        kanoho$remove(EntityComponents.CONSUMED);
    }

    @Override
    public <T> Optional<T> kanoho$get(ComponentType<T> component) {
        return component.fromTag(components);
    }

    @Override
    public <T> void kanoho$set(ComponentType<T> component, T value) {
        component.toTag(components, value);
    }

    @Override
    public boolean kanoho$contains(ComponentType<?> component) {
        return kanoho$get(component).isPresent();
    }

    @Override
    public void kanoho$remove(ComponentType<?> component) {
        component.removeTag(components);
    }
}
