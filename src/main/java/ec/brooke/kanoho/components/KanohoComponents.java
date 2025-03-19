package ec.brooke.kanoho.components;

import com.mojang.serialization.Codec;

public class KanohoComponents {
    public static final ComponentType<Boolean> USABLE_DATA = new ComponentType<>("consumed", Codec.BOOL);

}
