package moonfather.workshop_for_handsome_adventurer.block_entities;

import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class ToolRackBlockEntity extends BaseContainerBlockEntity
{
	public ToolRackBlockEntity(BlockPos pos, BlockState state)
	{
		super(Registration.TOOL_RACK_BE.get(), pos, state);
		this.setCapacity(6);
	}

	public ToolRackBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, int capacity)
	{
		super(blockEntityType, pos, state);
		this.setCapacity(capacity);
	}

    public int getNumberOfItemsInOneRow() {
		return 2;
	}



	// run TESR even if main block isn't visible.
	public AABB getRenderBoundingBox()
	{
		if (this.cachedRenderingAABB == null && ! this.worldPosition.equals(BlockPos.ZERO))
		{
			this.cachedRenderingAABB = new AABB(this.worldPosition.getX(), this.worldPosition.getY() - 1, this.worldPosition.getZ(), this.worldPosition.getX() + 1, this.worldPosition.getY() + 1, this.worldPosition.getZ() + 1);
		}
		return this.cachedRenderingAABB;
	}
	private AABB cachedRenderingAABB = null;
}
