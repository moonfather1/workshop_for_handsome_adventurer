package moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.slf4j.Logger;

import javax.annotation.Nullable;

public class BasicBlockEntity extends BlockEntity
{
    public BasicBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) { super(type, pos, blockState); }

    //////////////////////////////////

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        this.saveInternal(output);
    }

    protected ValueOutput saveInternal(ValueOutput output)
    {
        return output;
    }

    @Override
    public void handleUpdateTag(ValueInput input)
    {
        this.loadWithComponents(input); // update client
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries)
    {
        try (ProblemReporter.ScopedCollector pr = new ProblemReporter.ScopedCollector(LOGGER))
        {
            TagValueOutput output = TagValueOutput.createWithContext(pr.forChild(this.problemPath()), registries);
            this.saveInternal(output);
            return output.buildResult();   //send to client
        }
    }
    protected static final Logger LOGGER = LogUtils.getLogger();

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
