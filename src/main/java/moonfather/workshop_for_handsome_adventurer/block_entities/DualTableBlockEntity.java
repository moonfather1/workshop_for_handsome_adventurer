package moonfather.workshop_for_handsome_adventurer.block_entities;

import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DualTableBlockEntity extends SimpleTableBlockEntity
{
    public DualTableBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.DUAL_TABLE_BE.get(), pos, state);
        this.capacity = 9 /*crafting*/ + 4 /*customization, was forced to put max here*/ + 9 /*crafting*/;
    }
}
