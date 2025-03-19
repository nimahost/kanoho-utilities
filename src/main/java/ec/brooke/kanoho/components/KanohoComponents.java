package ec.brooke.kanoho.components;

import com.mojang.serialization.Codec;

import javax.naming.OperationNotSupportedException;

/**
 * Static class containing all Kanoho custom components
 */
public class KanohoComponents {
    public static final ComponentType<Boolean> CONSUMED = new ComponentType<>("consumed", Codec.BOOL);

    public KanohoComponents() throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Class should not be instantiated");
    }
}
