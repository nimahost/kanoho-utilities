package ec.brooke.kanoho.framework.components;

import com.mojang.serialization.Codec;

import javax.naming.OperationNotSupportedException;

/**
 * Static class containing all Kanoho item components
 */
public class ItemComponents {
    public static final ComponentType<Boolean> CONSUMED = new ComponentType<>("consumed", Codec.BOOL);

    private ItemComponents() throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Class should not be instantiated");
    }
}
