package ec.brooke.kanoho.features.resourcepack;

import net.minecraft.ChatFormatting;
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
    /** The prompt showed to users apply this pack */
    public static final Component PROMPT = Component.literal("Kanoho is better with it's official Resource Pack!")
            .withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_RED);

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
