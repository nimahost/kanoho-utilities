package ec.brooke.kanoho.vanilla.mixins;

import ec.brooke.kanoho.framework.components.ComponentType;
import ec.brooke.kanoho.framework.components.IComponentHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IComponentHolder {

    @Unique
    private ItemStack itemstack() {
        return (ItemStack) (Object) this;
    }

    @Unique
    private Optional<CustomData> getCustomData() {
        return Optional.ofNullable(itemstack().get(DataComponents.CUSTOM_DATA));
    }

    @Override
    public <T> Optional<T> kanoho$get(ComponentType<T> component) {
        return getCustomData().flatMap(d -> component.from(d.copyTag()));
    }

    @Override
    public <T> void kanoho$set(ComponentType<T> component, T value) {
        CustomData.update(DataComponents.CUSTOM_DATA, itemstack(), t -> ComponentType.namespace(t).ifPresent(n -> component.to(n, value)));
    }

    @Override
    public boolean kanoho$contains(ComponentType<?> component) {
        return getCustomData().orElse(CustomData.EMPTY).contains(component.location);
    }

    @Override
    public void kanoho$remove(ComponentType<?> component) {
        CustomData.update(DataComponents.CUSTOM_DATA, itemstack(), ComponentType.namespace(component::remove));
    }
}
