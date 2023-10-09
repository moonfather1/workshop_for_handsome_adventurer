package moonfather.workshop_for_handsome_adventurer.block_entities;

import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DualTableBlockEntity extends SimpleTableBlockEntity
{
    public DualTableBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.DUAL_TABLE_BE.get(), pos, state);
        this.setCapacity(9 /*crafting*/ + 4 /*customization, was forced to put max here*/ + 9) /*crafting*/;
    }

    public DualTableBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state)  // needed this for EveryCompat
    {
        super(blockEntityType, pos, state);
        this.setCapacity(9 /*crafting*/ + 4 /*customization, was forced to put max here*/ + 9 /*crafting*/);
    }

    //////////// integer data //////////////

    private int data0 = 0;
    public void StoreIntegerData(int index, int value) {
        assert index == 0;
        this.data0 = value;
    }

    public int GetIntegerData(int index) {
        assert index == 0;
        return this.data0;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.data0 = tag.getInt("int0");
    }

    @Override
    public CompoundTag saveInternal(CompoundTag tag) {
        tag.putInt("int0", this.data0);
        return super.saveInternal(tag);
    }



    public Direction getDirection()
    {
        if (this.cachedDirection == null)
        {
            this.cachedDirection = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        }
        return this.cachedDirection;
    }
    private Direction cachedDirection;
}
