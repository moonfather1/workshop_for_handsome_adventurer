package moonfather.workshop_for_handsome_adventurer.block_entities;

import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ToolRackBlockEntity extends BaseContainerBlockEntity
{
	public ToolRackBlockEntity(BlockPos pos, BlockState state)
	{
		super(Registration.TOOL_RACK_BE.get(), pos, state);
		this.capacity = 6;
	}
}
