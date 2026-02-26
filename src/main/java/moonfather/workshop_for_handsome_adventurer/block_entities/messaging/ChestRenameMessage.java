package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ChestRenameMessage(String value) implements CustomPacketPayload
{
    private static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MODID, "message_rename");
    public static final Type<ChestRenameMessage> TYPE = new Type<>(ID);



    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ChestRenameMessage> STREAM_CODEC = StreamCodec.of(
            ChestRenameMessage::encode,
            ChestRenameMessage::decode);



    private static void encode(RegistryFriendlyByteBuf buf, ChestRenameMessage msg) {
        buf.writeUtf(msg.value);
    }
    private static @NotNull ChestRenameMessage decode(RegistryFriendlyByteBuf buf) { return new ChestRenameMessage(Optional.of(buf.readUtf()).orElse("")); }
}
