package moonfather.workshop_for_handsome_adventurer.blocks;

import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.HitResult;

public class DualTableBaseBlock extends Block
{
	public DualTableBaseBlock()
	{
		super(Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN).strength(2f, 3f).sound(SoundType.WOOD));
		registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
	}



	@Override
	public PushReaction getPistonPushReaction(BlockState p_60584_)
	{
		return PushReaction.DESTROY;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
	{
		return 5;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
	{
		return 20;
	}


	public static final BooleanProperty BEING_PLACED = BooleanProperty.create("being_placed"); // ignore blocks around it not matching multiblock
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
		builder.add(DualTableBaseBlock.BEING_PLACED);
	}



	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos rackPos, Block block, BlockPos wallPos, boolean something)
	{
		super.neighborChanged(state, level, rackPos, block, wallPos, something);
		if (!this.canSurvive(state, level, rackPos))
		{
			level.destroyBlock(rackPos, true);
		}
	}



	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
	{
		return new ItemStack(Registration.DUAL_TABLE_PLACER_ITEM.get());
	}
}
