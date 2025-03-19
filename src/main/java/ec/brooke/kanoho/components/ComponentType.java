package ec.brooke.kanoho.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import ec.brooke.kanoho.Kanoho;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.Optional;

public record ComponentType<T>(String location, Codec<T> codec) {
    public static final String NAMESPACE = Kanoho.MOD_ID;

    public Optional<T> from(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return Optional.empty();

        if (!data.contains(NAMESPACE)) return Optional.empty();
        CompoundTag namespace = data.copyTag().getCompound(NAMESPACE);

        Tag tag = namespace.get(location);
        if (tag == null) return Optional.empty();

        DataResult<T> result = codec.parse(NbtOps.INSTANCE, tag);
        return result.resultOrPartial();
    }
}
