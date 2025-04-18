package ec.brooke.kanoho.features.resourcepack;

import ec.brooke.kanoho.Kanoho;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;

import java.util.Optional;
import java.util.UUID;

/**
 * Represents a resource pack that players can select
 * @param hash The SHA1 of this resource pack
 * @param url The URL to download this resource pack from
 */
public record ResourcepackDefinition(String hash, String url) {
    /** The UUID used to specify the packs should be replaced instead of stacked */
    public static final UUID PACK_UUID = UUID.randomUUID();

    /** The prompt shown to players when they are asked to apply a resource pack */
    public static final Component PROMPT;

    static {
        Component prompt = Component.Serializer.fromJson(Kanoho.CONFIG.resourcepackPrompt, RegistryAccess.EMPTY);
        if (prompt == null) throw new RuntimeException("Could not parse resource pack prompt");
        PROMPT = prompt;
    }

    /**
     * @return a resource pack packet to apply this pack
     */
    public ClientboundResourcePackPushPacket push() {
        return new ClientboundResourcePackPushPacket(
                PACK_UUID,
                url,
                hash,
                true,
                Optional.of(PROMPT)
        );
    }

    /**
     * @return a resource pack packet to remove this pack
     */
    public static ClientboundResourcePackPopPacket pop() {
        return new ClientboundResourcePackPopPacket(Optional.of(PACK_UUID));
    }
}
