package moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class BlockPagingMessage
{
    private final BlockPos pos;
    private final boolean movingRight;

    public BlockPagingMessage(BlockPos blockPos, boolean directionRight)
    {
        this.pos = blockPos;
        this.movingRight = directionRight;
    }


    public void encode(FriendlyByteBuf buf)
    {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        buf.writeBoolean(this.movingRight);
    }
    public static BlockPagingMessage decode(FriendlyByteBuf buf)
    {
        return new BlockPagingMessage(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()), buf.readBoolean());
    }

    public BlockPos getPos() { return this.pos; }
    public boolean isMovingRight() { return this.movingRight; }
}
