package moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;


public record BlockCheckmarkMessage(BlockPos pos, int index, int pageNumber) implements CustomPacketPayload
{
    private static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MODID, "message_block_checkmark");
    public static final Type<BlockCheckmarkMessage> TYPE = new Type<>(ID);



    @Override
    public @NotNull Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockCheckmarkMessage> STREAM_CODEC = StreamCodec.of(
            BlockCheckmarkMessage::encode,
            BlockCheckmarkMessage::decode
    );



    private static void encode(RegistryFriendlyByteBuf buf, BlockCheckmarkMessage msg)
    {
        buf.writeInt(msg.pos.getX());
        buf.writeInt(msg.pos.getY());
        buf.writeInt(msg.pos.getZ());
        buf.writeInt(msg.index);
        buf.writeInt(msg.pageNumber);  // this should be unneeded
    }
    private static @NotNull BlockCheckmarkMessage decode(RegistryFriendlyByteBuf buf)
    {
        return new BlockCheckmarkMessage(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()), buf.readInt(), buf.readInt());
    }
}
