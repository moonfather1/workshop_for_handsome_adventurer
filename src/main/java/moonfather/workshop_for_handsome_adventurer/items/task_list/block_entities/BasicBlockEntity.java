package moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BasicBlockEntity extends BlockEntity
{
    public BasicBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) { super(type, pos, blockState); }

    //////////////////////////////////

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        this.saveInternal(tag);
    }

    protected CompoundTag saveInternal(CompoundTag compoundTag)
    {
        return compoundTag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag)
    {
        this.load(tag); // update client
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        return this.saveInternal(new CompoundTag()); //send to client
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        // Will get tag from #getUpdateTag
        return ClientboundBlockEntityDataPacket.create(this);
    }

    //////////////////////////////////////////////////

    public void sendUpdated()
    {
        sendUpdated(this.getLevel(), this.getBlockPos());
    }
    public static void sendUpdated(@Nullable Level level, BlockPos pos)
    {
        if (level != null && level.getChunkSource() instanceof ServerChunkCache scc)
        {
            scc.blockChanged(pos);
        } // these 4 lines are faster equivalent of level.sendBlockUpdated
    }
}
