package moonfather.workshop_for_handsome_adventurer.blocks;

import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AdvancedTableTopSecondary extends DualTableBaseBlock
{
	private static final VoxelShape SHAPE_WALL_S = Block.box( 0.0D, 0.0D,  0.0D, 16.0D, 8.0D, 3.0D);
	private static final VoxelShape SHAPE_WALL_W = Block.box(13.0D, 0.0D,  0.0D, 16.0D, 8.0D, 16.0D);
	private static final VoxelShape SHAPE_WALL_N = Block.box( 0.0D, 0.0D, 13.0D, 16.0D, 8.0D, 16.0D);
	private static final VoxelShape SHAPE_WALL_E = Block.box( 0.0D, 0.0D,  0.0D,  3.0D, 8.0D, 16.0D);

	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos blockPos)
	{
		return this.ResolveShape(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
	}

	@Override
	public VoxelShape getBlockSupportShape(BlockState state, BlockGetter world, BlockPos blockPos)
	{
		return this.ResolveShape(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos blockPos, CollisionContext collisionContext)
	{
		return this.ResolveShape(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
	}

	private VoxelShape ResolveShape(Direction direction)
	{
		switch (direction)
		{
			case NORTH: return SHAPE_WALL_N;
			case WEST: return SHAPE_WALL_W;
			case SOUTH: return SHAPE_WALL_S;
			case EAST: return SHAPE_WALL_E;
		}
		return null;
	}



	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos)
	{
		if (state.getValue(AdvancedTableBottomPrimary.BEING_PLACED).booleanValue() == true)
		{
			return true; //canSurvive is disabled until we place the multiblock
		}
		Block below = world.getBlockState(pos.below()).getBlock();
		return below instanceof AdvancedTableBottomPrimary || below.equals(Registration.DUAL_TABLE_SECONDARY.get());
	}



	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
	{
		BlockPos posMain = pos.below();
		BlockState stateMain = level.getBlockState(posMain);
		Block blockMain = stateMain.getBlock();
		return blockMain.getCloneItemStack(stateMain, target, level, posMain, player);
	}
}
