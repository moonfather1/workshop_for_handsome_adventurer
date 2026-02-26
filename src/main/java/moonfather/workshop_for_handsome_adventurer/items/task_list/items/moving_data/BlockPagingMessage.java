package moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;


public record BlockPagingMessage(BlockPos pos, boolean movingRight) implements CustomPacketPayload
{
    private static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MODID, "message_block_paging");
    public static final Type<BlockPagingMessage> TYPE = new Type<>(ID);



    @Override
    public @NotNull Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockPagingMessage> STREAM_CODEC = StreamCodec.of(
            BlockPagingMessage::encode,
            BlockPagingMessage::decode
    );



    private static void encode(RegistryFriendlyByteBuf buf, BlockPagingMessage msg)
    {
        buf.writeInt(msg.pos.getX());
        buf.writeInt(msg.pos.getY());
        buf.writeInt(msg.pos.getZ());
        buf.writeBoolean(msg.movingRight);
    }
    private static @NotNull BlockPagingMessage decode(RegistryFriendlyByteBuf buf)
    {
        return new BlockPagingMessage(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()), buf.readBoolean());
    }
}
