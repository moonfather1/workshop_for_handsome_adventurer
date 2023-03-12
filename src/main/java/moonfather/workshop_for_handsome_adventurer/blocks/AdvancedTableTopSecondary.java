package moonfather.workshop_for_handsome_adventurer.blocks;

import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class AdvancedTableTopSecondary extends DualTableBaseBlock
{
	public AdvancedTableTopSecondary()
	{
		super();
		registerDefaultState(this.defaultBlockState()
				.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
				.setValue(AdvancedTableBottomPrimary.HAS_LANTERNS, false)
				.setValue(AdvancedTableBottomPrimary.LIGHTS_ON, false));
	}

	private static final VoxelShape SHAPE_WALL_S = Block.box( 0.0D, 0.0D,  0.0D, 16.0D, 8.0D, 3.0D);
	private static final VoxelShape SHAPE_WALL_W = Block.box(13.0D, 0.0D,  0.0D, 16.0D, 8.0D, 16.0D);
	private static final VoxelShape SHAPE_WALL_N = Block.box( 0.0D, 0.0D, 13.0D, 16.0D, 8.0D, 16.0D);
	private static final VoxelShape SHAPE_WALL_E = Block.box( 0.0D, 0.0D,  0.0D,  3.0D, 8.0D, 16.0D);
	private static final VoxelShape SHAPE_WALL_AND_LAMP_S = Block.box( 0.0D,  0.01D,  0.0D, 16.0D, 16.0D, 3.0D);
	private static final VoxelShape SHAPE_WALL_AND_LAMP_W = Block.box(13.0D, 0.01D,  0.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape SHAPE_WALL_AND_LAMP_N = Block.box( 0.0D, 0.01D, 13.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape SHAPE_WALL_AND_LAMP_E = Block.box( 0.0D, 0.01D,  0.0D,  3.0D, 16.0D, 16.0D);

	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos blockPos)
	{
		return this.ResolveShape(state.getValue(BlockStateProperties.HORIZONTAL_FACING), true);
	}

	@Override
	public VoxelShape getBlockSupportShape(BlockState state, BlockGetter world, BlockPos blockPos)
	{
		return this.ResolveShape(state.getValue(BlockStateProperties.HORIZONTAL_FACING), ! state.getValue(AdvancedTableBottomPrimary.HAS_LANTERNS));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos blockPos, CollisionContext collisionContext)
	{
		return this.ResolveShape(state.getValue(BlockStateProperties.HORIZONTAL_FACING), ! state.getValue(AdvancedTableBottomPrimary.HAS_LANTERNS));
	}

	private VoxelShape ResolveShape(Direction direction, boolean noLamps)
	{
		switch (direction)
		{
			case NORTH: return noLamps ? SHAPE_WALL_N : SHAPE_WALL_AND_LAMP_N;
			case WEST: return noLamps ? SHAPE_WALL_W : SHAPE_WALL_AND_LAMP_W;
			case SOUTH: return noLamps ? SHAPE_WALL_S : SHAPE_WALL_AND_LAMP_S;
			case EAST: return noLamps ? SHAPE_WALL_E : SHAPE_WALL_AND_LAMP_E;
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
		return below instanceof AdvancedTableBottomPrimary || below instanceof AdvancedTableBottomSecondary;
	}



	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
	{
		BlockPos posMain = pos.below();
		BlockState stateMain = level.getBlockState(posMain);
		Block blockMain = stateMain.getBlock();
		return blockMain.getCloneItemStack(stateMain, target, level, posMain, player);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult bhr1)
	{
		BlockPos posMain = pos.below();
		BlockState stateMain = level.getBlockState(posMain);
		if (player.isCrouching() && state.getValue(AdvancedTableBottomPrimary.HAS_LANTERNS))
		{
			((DualTableBaseBlock) stateMain.getBlock()).toggleLights(stateMain, level, posMain);
			return InteractionResult.sidedSuccess(level.isClientSide());
		}
		BlockHitResult bhr2 = new BlockHitResult(bhr1.getLocation(), bhr1.getDirection(), posMain, bhr1.isInside());
		return stateMain.getBlock().use(stateMain, level, posMain, player, hand, bhr2);
	}

	@Override
	protected void toggleLights(BlockState state, Level level, BlockPos pos)
	{
		BlockPos posMain = pos.below();
		BlockState stateMain = level.getBlockState(posMain);
		((DualTableBaseBlock) stateMain.getBlock()).toggleLights(stateMain, level, posMain);
	}



	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(AdvancedTableBottomPrimary.HAS_LANTERNS);
		builder.add(AdvancedTableBottomPrimary.LIGHTS_ON);
	}
}
