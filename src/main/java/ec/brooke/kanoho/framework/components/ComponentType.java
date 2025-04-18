package ec.brooke.kanoho.framework.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import ec.brooke.kanoho.Kanoho;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Definition type for Kanoho's custom component system
 * @param <T> The type of this component, usually a record
 */
public class ComponentType<T> {
    /** The key of the components root tag */
    public static final String NAMESPACE = Kanoho.MOD_ID;

    /** The key of this component's root */
    public final String location;
    /** Codec for parsing this component from NBT */
    private final Codec<T> codec;

    /**
     * Create a new component type with the specified parameters
     * @param location The key of this component's root
     * @param codec Codec for parsing this component from NBT
     */
    public ComponentType(String location, Codec<T> codec) {
        this.location = location;
        this.codec = codec;
    }

    /**
     * Gets the namespace tag if it exists in the provided tag.
     * @param tag The tag to get the namespace from.
     * @return Optional containing the namespace tag.
     */
    public static Optional<CompoundTag> namespace(CompoundTag tag) {
        if (!tag.contains(NAMESPACE)) return Optional.empty();
        return Optional.of(tag.getCompound(NAMESPACE));
    }

    /**
     * Wraps the provided {@link CompoundTag} consumer to consume the namespace tag, for example:
     * @param consumer The consumer to wrap
     * @return The wrapped consumer
     */
    public static Consumer<CompoundTag> namespace(Consumer<CompoundTag> consumer) {
        return tag -> namespace(tag).ifPresent(consumer);
    }

    /**
     * Creates an instance of this component if it exists on the provided {@link IComponentHolder}
     * @param holder The holder to get a component from
     * @return An optional containing the instance if it was found
     */
    public Optional<T> from(Object holder) {
        if (holder instanceof CompoundTag tag) {
            Tag component = tag.get(location);
            if (component == null) return Optional.empty();

            DataResult<T> result = codec.parse(NbtOps.INSTANCE, component);
            return result.resultOrPartial();
        } else if (holder instanceof IComponentHolder h) return h.kanoho$get(this);
        else {
            Kanoho.LOGGER.warn("Tried get {} from non-{} class {}", this.location, IComponentHolder.class.getName(), holder.getClass().getName());
            return Optional.empty();
        }
    }

    /**
     * Sets the value of this component in the provided {@link IComponentHolder}
     * @param holder The holder to set the component value on
     * @param value The value to set the component to
     */
    public void to(Object holder, T value) {
        if (holder instanceof CompoundTag tag) {
            Tag component = codec.encodeStart(NbtOps.INSTANCE, value).getOrThrow();
            tag.put(location, component);
        } else if (holder instanceof IComponentHolder h) h.kanoho$set(this, value);
        else Kanoho.LOGGER.warn("Tried set {} in non-{} class {}", this.location, IComponentHolder.class.getName(), holder.getClass().getName());
    }

    /**
     * Checks whether this component is contained within the provided {@link IComponentHolder}
     * @param holder The holder to check for presence in
     * @return True if the component exists
     */
    public boolean in(Object holder) {
        if (holder instanceof CompoundTag tag) return tag.contains(location);
        else if (holder instanceof IComponentHolder h) return h.kanoho$contains(this);
        else {
            Kanoho.LOGGER.warn("Tried check if {} is in non-{} class {}", this.location, IComponentHolder.class.getName(), holder.getClass().getName());
            return false;
        }
    }

    /**
     * Removes this component from the provided {@link IComponentHolder}
     * @param holder The holder to remove from
     */
    public void remove(Object holder) {
        if (holder instanceof CompoundTag tag) tag.remove(location);
        else if (holder instanceof IComponentHolder h) h.kanoho$remove(this);
        else Kanoho.LOGGER.warn("Tried remove {} from non-{} class {}", this.location, IComponentHolder.class.getName(), holder.getClass().getName());
    }
}
