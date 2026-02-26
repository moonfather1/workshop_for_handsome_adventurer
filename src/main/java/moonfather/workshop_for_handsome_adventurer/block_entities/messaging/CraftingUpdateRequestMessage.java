package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record CraftingUpdateRequestMessage(int value) implements CustomPacketPayload
{
    private static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MODID, "message_craftingupdate");
    public static final Type<CraftingUpdateRequestMessage> TYPE = new Type<>(ID);



    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, CraftingUpdateRequestMessage> STREAM_CODEC = StreamCodec.of(
            CraftingUpdateRequestMessage::encode,
            CraftingUpdateRequestMessage::decode);



    private static void encode(RegistryFriendlyByteBuf buf, CraftingUpdateRequestMessage msg) { buf.writeInt(msg.value); }
    private static @NotNull CraftingUpdateRequestMessage decode(RegistryFriendlyByteBuf buf) { return new CraftingUpdateRequestMessage(buf.readInt()); }
}
