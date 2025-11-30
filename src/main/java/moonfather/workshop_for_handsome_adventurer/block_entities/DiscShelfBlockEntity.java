package moonfather.workshop_for_handsome_adventurer.block_entities;

import moonfather.workshop_for_handsome_adventurer.CommonConfig;
import moonfather.workshop_for_handsome_adventurer.blocks.DiscShelf;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DiscShelfBlockEntity extends PotionShelfBlockEntity
{
    public DiscShelfBlockEntity(BlockPos pos, BlockState state)
    {
        super(Registration.DISC_SHELF_BE.get(), pos, state, 12, DiscShelf.SLOT_COUNT);
    }

    public DiscShelfBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) 
    {
        super(blockEntityType, pos, state, 12, DiscShelf.SLOT_COUNT);
    }

    @Override
    protected int getSlotRoomMultiplier()
    {
        return CommonConfig.SlotRoomMultiplierForDiscs.get();
    }

    @Override
    protected int getSlotRoomMaximum()
    {
        return CommonConfig.SlotRoomMaximumForDiscs.get();
    }
}
