package ec.brooke.kanoho.features.resourcepack;

import ec.brooke.kanoho.Kanoho;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a resource pack that players can select
 * @param hash The SHA1 of this resource pack
 * @param url The URL to download this resource pack from
 */
public record ResourcepackDefinition(String hash, String url) {
    /** The prompt shown to players when they are asked to apply a resource pack */
    public static final Component PROMPT;

    static {
        Component prompt = Component.Serializer.fromJson(Kanoho.CONFIG.resourcepackPrompt, RegistryAccess.EMPTY);
        if (prompt == null) throw new RuntimeException("Could not parse resource pack prompt");
        PROMPT = prompt;
    }

    /**
     * @return a resource pack packet to apply this definition.
     */
    public ClientboundResourcePackPushPacket packet() {
        return new ClientboundResourcePackPushPacket(
                UUID.nameUUIDFromBytes(hash.getBytes(StandardCharsets.UTF_8)),
                url,
                hash,
                true,
                Optional.of(PROMPT)
        );
    }
}
