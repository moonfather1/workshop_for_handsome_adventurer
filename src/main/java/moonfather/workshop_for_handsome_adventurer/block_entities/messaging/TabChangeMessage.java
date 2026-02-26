package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record TabChangeMessage(int tab) implements CustomPacketPayload
{
    private static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MODID, "message_tabchange");
    public static final Type<TabChangeMessage> TYPE = new Type<>(ID);



    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, TabChangeMessage> STREAM_CODEC = StreamCodec.of(
            TabChangeMessage::encode,
            TabChangeMessage::decode);



    private static void encode(RegistryFriendlyByteBuf buf, TabChangeMessage msg) {
        buf.writeInt(msg.tab);
    }
    private static @NotNull TabChangeMessage decode(RegistryFriendlyByteBuf buf) { return new TabChangeMessage(buf.readInt()); }
}
