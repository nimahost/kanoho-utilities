package ec.brooke.kanoho.features.components;

import com.mojang.serialization.Codec;
import ec.brooke.kanoho.framework.components.ComponentType;
import net.minecraft.resources.ResourceLocation;

import javax.naming.OperationNotSupportedException;

/**
 * Static class containing all Kanoho item components
 */
public class ItemComponents {
    public static final ComponentType<ResourceLocation> ON_USED = new ComponentType<>("on_used", ResourceLocation.CODEC);
    public static final ComponentType<Boolean> CONSUMED = new ComponentType<>("consumed", Codec.BOOL);

    private ItemComponents() throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Class should not be instantiated");
    }
}
