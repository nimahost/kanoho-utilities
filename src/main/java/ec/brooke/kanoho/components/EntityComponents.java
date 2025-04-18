package ec.brooke.kanoho.components;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;

import javax.naming.OperationNotSupportedException;

/**
 * Static class containing all Kanoho player components
 */
public class EntityComponents {
    public static final ComponentType<ItemStack> CONSUMED = new ComponentType<>("consumed", ItemStack.CODEC);

    public EntityComponents() throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Class should not be instantiated");
    }
}
