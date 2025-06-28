package moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data;


import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class BlockCheckmarkMessage
{
    private final BlockPos pos;
    private final int index, pageNumber;

    public BlockCheckmarkMessage(BlockPos blockPos, int index, int pageNumber)
    {
        this.pos = blockPos;
        this.index = index;
        this.pageNumber = pageNumber;
    }


    public void encode(FriendlyByteBuf buf)
    {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        buf.writeInt(this.index);
        buf.writeInt(this.pageNumber);  // this should be unneeded
    }

    public static BlockCheckmarkMessage decode(FriendlyByteBuf buf)
    {
        return new BlockCheckmarkMessage(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()), buf.readInt(), buf.readInt());
    }

    public int getPageNumber() { return this.pageNumber; }
    public int getIndex() { return this.index; }
    public BlockPos getPos() { return this.pos; }
}
