package moonfather.workshop_for_handsome_adventurer.blocks;

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
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;


public class DualToolRack extends ToolRack
{
    public DualToolRack(int itemCount)
    {
        super(itemCount);
        registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
    }



    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.DOUBLE_BLOCK_HALF);
        builder.add(DualTableBaseBlock.BEING_PLACED);
        super.createBlockStateDefinition(builder);
    }


    Map<VoxelShape, VoxelShape> shapeCache = new HashMap<VoxelShape, VoxelShape>();
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext collisionContext)
    {
        if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF).equals(DoubleBlockHalf.UPPER))
        {
            VoxelShape half = super.getShape(state, world, pos, collisionContext);
            if (! shapeCache.containsKey(half))
            {
                shapeCache.put(half, half.move(0, -1, 0));
            }
            return Shapes.or(half, shapeCache.get(half));
        }
        else
        {
            return Shapes.empty();
        }
    }
    public VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos)
    {
        return this.getShape(state, world, pos, null);
    }



    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockPos target = context.getClickedPos();
        Level level = context.getLevel();
        BlockPos below = target.below();
        if (target.getY() <= level.getMinBuildHeight() || ! level.getBlockState(below).canBeReplaced(context))
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



    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack itemStack)
    {
        level.setBlock(pos.below(), state.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER).setValue(DualTableBaseBlock.BEING_PLACED, false), 3);
        level.setBlock(pos, state.setValue(DualTableBaseBlock.BEING_PLACED, false), 3);
    }



    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean something)
    {
        super.neighborChanged(state, level, pos, block, pos2, something);
        if (!this.canSurvive(state, level, pos))
        {
            level.destroyBlock(pos, true);
        }
    }



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
}
