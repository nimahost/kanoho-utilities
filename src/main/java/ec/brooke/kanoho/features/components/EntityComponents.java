package ec.brooke.kanoho.features.components;

import com.mojang.serialization.Codec;
import ec.brooke.kanoho.framework.components.ComponentType;
import net.minecraft.world.item.ItemStack;

import javax.naming.OperationNotSupportedException;
import java.util.List;

/**
 * Static class containing all Kanoho player components
 */
public class EntityComponents {
    public static final ComponentType<List<String>> COOLDOWNS = new ComponentType<>("cooldowns", Codec.list(Codec.STRING));
    public static final ComponentType<String> RESOURCEPACK = new ComponentType<>("resourcepack", Codec.STRING);
    public static final ComponentType<ItemStack> USED = new ComponentType<>("used", ItemStack.CODEC);

    private EntityComponents() throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Class should not be instantiated");
    }
}
