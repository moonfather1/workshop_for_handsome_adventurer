package moonfather.workshop_for_handsome_adventurer.items.task_list.blocks;


import moonfather.workshop_for_handsome_adventurer.ClientConfig;
import moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities.TaskListBlockEntity;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.TaskListItem;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListMessaging;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TaskListPanel extends Block implements EntityBlock
{
    public TaskListPanel()
    {
        super(Properties.of().strength(0.3f, 0.6f).sound(SoundType.WOOD).mapColor(MapColor.COLOR_BROWN).pushReaction(PushReaction.DESTROY));
        registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(EMPTY, true));
        this.PrepareListOfShapes();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
        builder.add(EMPTY);
    }

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty EMPTY = BooleanProperty.create("empty");

    private static final VoxelShape SHAPE_PLANK1S = Block.box(1.5D, 0.0D, 0.0D, 14.5D, 16.0D, 1.0D);
    private static final VoxelShape SHAPE_PLANK1W = Block.box(15.0D, 0.0D, 1.5D, 16.0D, 16.0D, 14.5D);
    private static final VoxelShape SHAPE_PLANK1N = Block.box(1.5D, 0.0D, 15.0D, 14.5D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_PLANK1E = Block.box(0.0D, 0.0D, 1.5D, 1.0D, 16.0D, 14.5D);

    protected final Map<Direction, VoxelShape> shapes = new HashMap<Direction, VoxelShape>(4);
    protected void PrepareListOfShapes()
    {
        this.shapes.put(Direction.NORTH, SHAPE_PLANK1N);
        this.shapes.put(Direction.EAST, SHAPE_PLANK1E);
        this.shapes.put(Direction.SOUTH, SHAPE_PLANK1S);
        this.shapes.put(Direction.WEST, SHAPE_PLANK1W);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
    {
        return this.shapes.get(state.getValue(FACING));
    }

    //////////////////////////////////////////////////////////////

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        if (context.getClickedFace() == Direction.DOWN || context.getClickedFace() == Direction.UP)
        {
            return null;
        }
        BlockState result = this.defaultBlockState().setValue(FACING, context.getClickedFace());
        if (this.canSurvive(result, context.getLevel(), context.getClickedPos()))
        {
            return result;
        }
        else
        {
            return null;
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos)
    {
        BlockPos target = pos.relative(state.getValue(FACING).getOpposite());
        return world.getBlockState(target).isFaceSturdy(world, target, state.getValue(FACING));
    }

    ////////////////////////////////////////////////////////////////////////////////////

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return new TaskListBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_)
    {
        return null;
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
        {
            BlockEntity be = worldIn.getBlockEntity(pos);
            if (be instanceof TaskListBlockEntity panel)
            {
                Block.popResourceFromFace(worldIn, pos, state.getValue(FACING), panel.getItemForDrop());
            }
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos rackPos, Block block, BlockPos wallPos, boolean something)
    {
        super.neighborChanged(state, level, rackPos, block, wallPos, something);
        if (! this.canSurvive(state, level, rackPos))
        {
            level.destroyBlock(rackPos, false);
        }
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState p_60584_)
    {
        return PushReaction.DESTROY;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) { return TaskListItem.Utility.createInstance(); }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.core.Direction direction)
    {
        if (level.getBlockEntity(pos) instanceof TaskListBlockEntity tile)
        {
            return ! tile.isFireResistant();
        }
        return super.isFlammable(state, level, pos, direction);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.core.Direction direction)
    {
        if (level.getBlockEntity(pos) instanceof TaskListBlockEntity tile && tile.isFireResistant())
        {
            return 0;
        }
        return 20;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
    {
        if (! ClientConfig.taskListItemsAreDrawnOnWall.getAsBoolean())
        {
            // create mode - just open the gui
            if (level.isClientSide)
            {
                if (level.getBlockEntity(pos) instanceof TaskListBlockEntity tile)
                {
                    tile.invokeGUI();
                    return InteractionResult.SUCCESS_NO_ITEM_USED;
                }
                return InteractionResult.FAIL;
            }
            else
            {
                player.awardStat(Stats.ITEM_USED.get(TaskListItem.Utility.ourItem()));
                return InteractionResult.CONSUME;
            }
        }
        if (! level.isClientSide)
        {
            return InteractionResult.CONSUME;
        }
        double y = hitResult.getLocation().y - hitResult.getBlockPos().getY(); // within block, 0 is bottom, 1 is top;
        double x = 0;
        if (hitResult.getDirection().getAxis().equals(Direction.Axis.X))
        {
            x = hitResult.getLocation().z()-hitResult.getBlockPos().getZ();
            if (hitResult.getDirection().equals(Direction.EAST))
            {
                x = 1 - x;
            }
        }
        else
        {
            x = hitResult.getLocation().x()-hitResult.getBlockPos().getX();
            if (hitResult.getDirection().equals(Direction.NORTH))
            {
                x = 1 - x;
            }
        }
        // ready. check page op.
        if (y <= 0.15 && x <= 0.25)
        {
            if (level.getBlockEntity(pos) instanceof TaskListBlockEntity tile)
            {
                tile.onClientArrowPrev();
                return InteractionResult.SUCCESS;
            }
        }
        // check page down.
        if (y <= 0.15 && x >= 0.75)
        {
            if (level.getBlockEntity(pos) instanceof TaskListBlockEntity tile)
            {
                tile.onClientArrowNext();
                return InteractionResult.SUCCESS;
            }
        }
        // check checknoxes.
        double yFromTop = 1 - y;  // 0 is top now
        float vOffset = -0.04321f; //visuals are a little higher than exact pixels.
        for (int i = 0; i < TaskListMessaging.ITEMS_PER_PAGE; i++)
        {
            if (x >= 2/16f && x <= 4/16f && yFromTop >= (3+2*i)/16f + vOffset && yFromTop <= (3+2*i+2)/16f + vOffset)
            {
                if (level.getBlockEntity(pos) instanceof TaskListBlockEntity tile)
                {
                    tile.checkmarkClickedOnBlock(i);
                }
                break;
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
}
