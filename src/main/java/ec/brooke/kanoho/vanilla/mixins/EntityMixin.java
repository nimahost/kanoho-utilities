package ec.brooke.kanoho.vanilla.mixins;

import ec.brooke.kanoho.features.components.EntityComponents;
import ec.brooke.kanoho.framework.components.ComponentType;
import ec.brooke.kanoho.framework.components.IComponentHolder;
import net.minecraft.nbt.CompoundTag;
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

    @Inject(method = "tick", at = @At("TAIL"))
    private void injectStatus(CallbackInfo ci) {
        kanoho$remove(EntityComponents.USED);
    }

    @Override
    public <T> Optional<T> kanoho$get(ComponentType<T> component) {
        return component.from(components);
    }

    @Override
    public <T> void kanoho$set(ComponentType<T> component, T value) {
        component.to(components, value);
    }

    @Override
    public boolean kanoho$contains(ComponentType<?> component) {
        return component.in(components);
    }

    @Override
    public void kanoho$remove(ComponentType<?> component) {
        component.remove(components);
    }
}
