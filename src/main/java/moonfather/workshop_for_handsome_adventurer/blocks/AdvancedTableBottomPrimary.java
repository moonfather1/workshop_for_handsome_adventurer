package moonfather.workshop_for_handsome_adventurer.blocks;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.DualTableBlockEntity;
import moonfather.workshop_for_handsome_adventurer.block_entities.DualTableMenu;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableBlockEntity;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class AdvancedTableBottomPrimary extends DualTableBaseBlock implements EntityBlock
{
	public AdvancedTableBottomPrimary()
	{
		super();
		registerDefaultState(this.defaultBlockState().setValue(SimpleTable.HAS_INVENTORY, false).setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
	}

	/////////////////////////////////////////////////////////////////////////

	private static final VoxelShape SHAPE_TOP = Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape SHAPE_LEG1 = Block.box(1.0D, 0.0D, 1.0D, 4.0D, 12.0D, 4.0D);
	private static final VoxelShape SHAPE_LEG3 = Block.box(1.0D, 0.0D, 12.0D, 4.0D, 12.0D, 15.0D);
	private static final VoxelShape SHAPE_LEG2 = Block.box(12.0D, 0.0D, 1.0D, 15.0D, 12.0D, 4.0D);
	private static final VoxelShape SHAPE_LEG4 = Block.box(12.0D, 0.0D, 12.0D, 15.0D, 12.0D, 15.0D);
	private static final VoxelShape SHAPE_TABLE_S = Shapes.or(SHAPE_TOP, SHAPE_LEG1, SHAPE_LEG3);
	private static final VoxelShape SHAPE_TABLE_W = Shapes.or(SHAPE_TOP, SHAPE_LEG1, SHAPE_LEG2);
	private static final VoxelShape SHAPE_TABLE_N = Shapes.or(SHAPE_TOP, SHAPE_LEG2, SHAPE_LEG4);
	private static final VoxelShape SHAPE_TABLE_E = Shapes.or(SHAPE_TOP, SHAPE_LEG3, SHAPE_LEG4);

	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter p_60579_, BlockPos p_60580_)
	{
		return this.ResolveShape(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
	}

	@Override
	public VoxelShape getBlockSupportShape(BlockState state, BlockGetter p_60582_, BlockPos p_60583_)
	{
		return this.ResolveShape(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
	{
		return this.ResolveShape(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
	}

	private VoxelShape ResolveShape(Direction direction)
	{
		return switch (direction)
		{
			case NORTH -> SHAPE_TABLE_N;
			case WEST -> SHAPE_TABLE_W;
			case SOUTH -> SHAPE_TABLE_S;
			case EAST -> SHAPE_TABLE_E;
			default -> null;
		};
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos)
	{
		if (state.getValue(AdvancedTableBottomPrimary.BEING_PLACED).booleanValue() == true)
		{
			return true; //canSurvive is disabled until we place the multiblock
		}
		Block above = world.getBlockState(pos.above()).getBlock();
		if (! above.equals(Registration.DUAL_TABLE_TOP.get()))
		{
			return false;
		}
		Block right = world.getBlockState(pos.relative(state.getValue(BlockStateProperties.HORIZONTAL_FACING).getCounterClockWise())).getBlock();
		if (! right.equals(Registration.DUAL_TABLE_SECONDARY.get()))
		{
			return false;
		}
		return true;
	}



	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
	{
		String wood = this.getRegistryName().getPath();
		wood = wood.substring(wood.indexOf("_", 12) + 1);
		return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Constants.MODID, "workstation_placer_" + wood)));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult)
	{
		if (level.isClientSide)
		{
			return InteractionResult.SUCCESS;
		}
		else
		{
			player.openMenu(state.getMenuProvider(level, pos));
			player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
			return InteractionResult.CONSUME;
		}
	}



	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if (state.getBlock() != newState.getBlock())
		{
			BlockEntity te = worldIn.getBlockEntity(pos);
			if (te instanceof DualTableBlockEntity entity)
			{
				entity.DropAll();
			}
			super.onRemove(state, worldIn, pos, newState, isMoving);
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState)
	{
		return Registration.DUAL_TABLE_BE.get().create(pos, blockState);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_)
	{
		return null;
	}

	private static final Component CONTAINER_TITLE = new TranslatableComponent("container.crafting");
	public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos blockPos)
	{
		return new SimpleMenuProvider((containerId, inventory, p_52231_) ->
		{
			return new DualTableMenu(containerId, inventory, ContainerLevelAccess.create(level, blockPos), Registration.CRAFTING_DUAL_MENU_TYPE.get());
		}, CONTAINER_TITLE);
	}



	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(SimpleTable.HAS_INVENTORY);
	}
}
