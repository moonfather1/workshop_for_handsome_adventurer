package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record GridChangeMessage(int destination) implements CustomPacketPayload
{
    private static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MODID, "message_gridchange");
    public static final Type<GridChangeMessage> TYPE = new Type<>(ID);



    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, GridChangeMessage> STREAM_CODEC = StreamCodec.of(
            GridChangeMessage::encode,
            GridChangeMessage::decode);



    private static void encode(RegistryFriendlyByteBuf buf, GridChangeMessage msg) {
        buf.writeInt(msg.destination);
    }
    private static @NotNull GridChangeMessage decode(RegistryFriendlyByteBuf buf) { return new GridChangeMessage(buf.readInt()); }
}