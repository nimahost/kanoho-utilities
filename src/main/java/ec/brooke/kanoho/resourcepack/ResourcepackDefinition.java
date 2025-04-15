package ec.brooke.kanoho.resourcepack;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public record ResourcepackDefinition(String hash, String url) {
    public static final Component PROMPT = Component.literal("Kanoho is better with the official Resource Pack!");

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
