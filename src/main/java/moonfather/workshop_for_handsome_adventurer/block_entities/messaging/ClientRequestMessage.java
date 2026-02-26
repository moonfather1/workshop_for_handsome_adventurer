package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ClientRequestMessage(int value) implements CustomPacketPayload
{
    private static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MODID, "message_request");
    public static final Type<ClientRequestMessage> TYPE = new Type<>(ID);



    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientRequestMessage> STREAM_CODEC = StreamCodec.of(
            ClientRequestMessage::encode,
            ClientRequestMessage::decode);



    private static void encode(RegistryFriendlyByteBuf buf, ClientRequestMessage msg) {
        buf.writeInt(msg.value);
    }
    private static @NotNull ClientRequestMessage decode(RegistryFriendlyByteBuf buf) { return new ClientRequestMessage(buf.readInt()); }

    public static final int REQUEST_REMOTE_UPDATE = 5;
}