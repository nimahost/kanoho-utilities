package ec.brooke.kanoho.framework.components;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;

import javax.naming.OperationNotSupportedException;
import java.util.List;

/**
 * Static class containing all Kanoho player components
 */
public class EntityComponents {
    public static final ComponentType<ItemStack> CONSUMED = new ComponentType<>("consumed", ItemStack.CODEC);
    public static final ComponentType<String> RESOURCEPACK = new ComponentType<>("resourcepack", Codec.STRING);
    public static final ComponentType<List<String>> COOLDOWNS = new ComponentType<>("cooldowns", Codec.list(Codec.STRING));

    private EntityComponents() throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Class should not be instantiated");
    }
}
