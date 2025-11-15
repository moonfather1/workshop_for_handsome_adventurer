package moonfather.workshop_for_handsome_adventurer.blocks;

import moonfather.workshop_for_handsome_adventurer.integration.TetraCompatibleToolRackHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.ModList;

import javax.annotation.Nullable;


public class DualToolRack extends ToolRack
{
    public DualToolRack(int itemCount, String type, Block.Properties properties)
    {
        super(itemCount, type, properties);
        registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
    }

    public static ToolRack create(int itemCount, String type, Block.Properties properties)
    {
        if (ModList.get().isLoaded("tetra"))
        {
            return TetraCompatibleToolRackHelper.create(true, itemCount, type, properties);
        }
        else
        {
            return new DualToolRack(itemCount, type, properties);
        }
    }

    ////////////////////////////////////

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.DOUBLE_BLOCK_HALF);
        builder.add(DualTableBaseBlock.BEING_PLACED);
        super.createBlockStateDefinition(builder);
    }



    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockPos target = context.getClickedPos();
        Level level = context.getLevel();
        BlockPos below = target.below();
        if (target.getY() <= level.getMinY() || ! level.getBlockState(below).canBeReplaced(context))
        {
            return null;
        }
        BlockPos back = target.relative(context.getClickedFace().getOpposite());
        if (! level.getBlockState(back).isFaceSturdy(level, back, context.getClickedFace()))
        {
            return null;
        }
        back = back.below();
        if (! level.getBlockState(back).isFaceSturdy(level, back, context.getClickedFace()))
        {
            return null;
        }
        return this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite()).setValue(DualTableBaseBlock.BEING_PLACED, true).setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER);
    }



    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack itemStack)
    {
        level.setBlock(pos.below(), state.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER).setValue(DualTableBaseBlock.BEING_PLACED, false), 3);
        level.setBlock(pos, state.setValue(DualTableBaseBlock.BEING_PLACED, false), 3);
    }



    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @org.jetbrains.annotations.Nullable Orientation orientation, boolean movedByPiston)
    {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
        if (! this.canSurvive(state, level, pos))
        {
            level.destroyBlock(pos, true);
        }
    }



    @Override
    public boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos pos)
    {
        BlockPos back = pos.relative(state.getValue(FACING));
        BlockState blockstate = levelReader.getBlockState(back);
        if (! blockstate.isFaceSturdy(levelReader, back, state.getValue(FACING).getOpposite()))
        {
            return false;
        }
        if (state.getValue(DualTableBaseBlock.BEING_PLACED) == false)
        {
            if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
            {
                return levelReader.getBlockState(pos.above()).is(this);
            }
            else
            {
                return levelReader.getBlockState(pos.below()).is(this);
            }
        }
        return true;
    }


    public PushReaction getPistonPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
    }


    ///////////////////////////////////////////////////////

    private static final VoxelShape SHAPE_PLANK1N = Block.box(1.0D, 1.0D, 0.0D, 15.0D, 16.0D, 1.0D);
    private static final VoxelShape SHAPE_PLANK1E = Block.box(15.0D, 1.0D, 1.0D, 16.0D, 16.0D, 15.0D);
    private static final VoxelShape SHAPE_PLANK1S = Block.box(1.0D, 1.0D, 15.0D, 15.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_PLANK1W = Block.box(0.0D, 1.0D, 1.0D, 1.0D, 16.0D, 15.0D);
    private static final VoxelShape SHAPE_PLANK2N = Block.box(1.0D, 0.0D, 0.0D, 15.0D, 15.0D, 1.0D);
    private static final VoxelShape SHAPE_PLANK2E = Block.box(15.0D, 0.0D, 1.0D, 16.0D, 15.0D, 15.0D);
    private static final VoxelShape SHAPE_PLANK2S = Block.box(1.0D, 0.0D, 15.0D, 15.0D, 15.0D, 16.0D);
    private static final VoxelShape SHAPE_PLANK2W = Block.box(0.0D, 0.0D, 1.0D, 1.0D, 15.0D, 15.0D);



    @Override
    public VoxelShape getOcclusionShape(BlockState state)
    {
        return resolveShape(state.getValue(ToolRack.FACING), state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF));
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter p_60582_, BlockPos p_60583_)
    {
        return resolveShape(state.getValue(ToolRack.FACING), state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
    {
        return resolveShape(state.getValue(ToolRack.FACING), state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF));
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter p_60548_, BlockPos p_60549_)
    {
        return resolveShape(state.getValue(ToolRack.FACING), state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF));
    }

    private static VoxelShape resolveShape(Direction direction, DoubleBlockHalf half)
    {
        if (direction.equals(Direction.NORTH) && half.equals(DoubleBlockHalf.LOWER)) return SHAPE_PLANK1N;
        if (direction.equals(Direction.WEST) && half.equals(DoubleBlockHalf.LOWER)) return SHAPE_PLANK1W;
        if (direction.equals(Direction.EAST) && half.equals(DoubleBlockHalf.LOWER)) return SHAPE_PLANK1E;
        if (direction.equals(Direction.SOUTH) && half.equals(DoubleBlockHalf.LOWER)) return SHAPE_PLANK1S;
        if (direction.equals(Direction.NORTH) && half.equals(DoubleBlockHalf.UPPER)) return SHAPE_PLANK2N;
        if (direction.equals(Direction.WEST) && half.equals(DoubleBlockHalf.UPPER)) return SHAPE_PLANK2W;
        if (direction.equals(Direction.EAST) && half.equals(DoubleBlockHalf.UPPER)) return SHAPE_PLANK2E;
        if (direction.equals(Direction.SOUTH) && half.equals(DoubleBlockHalf.UPPER)) return SHAPE_PLANK2S;
        return null;
    }
}
