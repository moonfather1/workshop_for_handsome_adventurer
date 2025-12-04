package moonfather.workshop_for_handsome_adventurer.blocks;

import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableBlockEntity;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import moonfather.workshop_for_handsome_adventurer.integration.TetraHammerSupport;
import moonfather.workshop_for_handsome_adventurer.other.TableLockManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimpleTable extends Block implements EntityBlock, IBlockWithCleverHoverText
{
    private final Component MessageInaccessible = Component.translatable("message.workshop_for_handsome_adventurer.workshop_table_obscured");

    public SimpleTable(Properties properties)
    {
        super(properties);
        registerDefaultState(this.defaultBlockState().setValue(SimpleTable.HAS_INVENTORY, false));
        this.Tooltip1 = Component.translatable("block.workshop_for_handsome_adventurer.simple_table.tooltip1").withStyle(Style.EMPTY.withItalic(true).withColor(0x9966cc));
        this.Tooltip2 = Component.translatable("block.workshop_for_handsome_adventurer.simple_table.tooltip2").withStyle(Style.EMPTY.withItalic(true).withColor(0x9966cc));
    }

    public static Block.Properties getDefaultProperties()
    {
        return Properties.of().strength(2f, 3f).sound(SoundType.WOOD).ignitedByLava().mapColor(MapColor.COLOR_BROWN).pushReaction(PushReaction.DESTROY);
    }

    private MutableComponent Tooltip1, Tooltip2;
    private static final VoxelShape SHAPE_TOP = Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_LEG1 = Block.box(0.0D, 0.0D, 13.0D, 3.0D, 12.0D, 16.0D);
    private static final VoxelShape SHAPE_LEG2 = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 12.0D, 3.0D);
    private static final VoxelShape SHAPE_LEG3 = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 12.0D, 3.0D);
    private static final VoxelShape SHAPE_LEG4 = Block.box(13.0D, 0.0D, 13.0D, 16.0D, 12.0D, 16.0D);
    private static final VoxelShape SHAPE_TABLE = Shapes.or(SHAPE_TOP, SHAPE_LEG1, SHAPE_LEG2, SHAPE_LEG3, SHAPE_LEG4);

    @Override
    public VoxelShape getOcclusionShape(BlockState p_60578_)
    {
        return SHAPE_TABLE;
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState p_60581_, BlockGetter p_60582_, BlockPos p_60583_)
    {
        return SHAPE_TABLE;
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
    {
        return SHAPE_TABLE;
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

    @Override
    public List<Component> getTooltipLines()
    {
        List<Component> result = new ArrayList<>(2);
        result.add(this.Tooltip1);
        result.add(this.Tooltip2);
        return result;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult blockHitResult)
    {
        if (this.isObscured(level, pos))
        {
            if (level.isClientSide())
            {
                player.displayClientMessage(MessageInaccessible, true);
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }
        else if (level.isClientSide())
        {
            return InteractionResult.SUCCESS;
        }
        else if (TableLockManager.isLocked(level, pos))
        {
            player.displayClientMessage(Component.translatable("message.workshop_for_handsome_adventurer.workshop_table_in_use", TableLockManager.getPlayerName(level, pos).copy().withStyle(Style.EMPTY.withColor(0xffeeee11))), true);
            return InteractionResult.CONSUME;
        }
        else if (ModList.get().isLoaded("tetra_tables") && ! player.isCrouching() && TetraHammerSupport.isHammer(player.getMainHandItem()))
        {
            String id = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
            String wood = id.substring(id.indexOf("simple_table") + 13);
            String newName = "tetra_table_" + wood;
            ResourceLocation key = ResourceLocation.fromNamespaceAndPath("tetra_tables", newName);
            Optional<Holder.Reference<Block>> wrapper = BuiltInRegistries.BLOCK.get(key);
            if (wrapper.isPresent())
            {
                level.setBlockAndUpdate(pos, wrapper.get().value().defaultBlockState());
                return InteractionResult.CONSUME;
            }
            return InteractionResult.FAIL;
        }
        else if (ModList.get().isLoaded("tetra") && ! player.isCrouching() && TetraHammerSupport.isHammer(player.getMainHandItem()))
        {
            level.setBlockAndUpdate(pos, TetraHammerSupport.getWorkBench());
            return InteractionResult.CONSUME;
        }
        else
        {
            player.openMenu(state.getMenuProvider(level, pos));
            player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            return InteractionResult.CONSUME;
        }
    }


    private boolean isObscured(Level level, BlockPos pos)
    {
        if (!level.getFluidState(pos.above()).getType().equals(Fluids.EMPTY))
        {
            return true;
        }
        VoxelShape s = level.getBlockState(pos.above()).getFaceOcclusionShape(Direction.DOWN);
        if (s.isEmpty())
        {
            return false;
        }
        double area = (s.max(Direction.Axis.X) - s.min(Direction.Axis.X)) * (s.max(Direction.Axis.Z) - s.min(Direction.Axis.Z));
        double lesserDim = Math.min(s.max(Direction.Axis.X) - s.min(Direction.Axis.X), s.max(Direction.Axis.Z) - s.min(Direction.Axis.Z));
        return area > 0.1875 || lesserDim > 0.1875;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState)
    {
        return new SimpleTableBlockEntity(pos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_)
    {
        return null;
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos blockPos)
    {
        return new SimpleMenuProvider((containerId, inventory, p_52231_) ->
        {
            return new SimpleTableMenu(containerId, inventory, ContainerLevelAccess.create(level, blockPos), Registration.CRAFTING_SINGLE_MENU_TYPE.get());
        }, CONTAINER_TITLE);
    }

    private static final Component CONTAINER_TITLE = Component.translatable("container.crafting");



    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(SimpleTable.HAS_INVENTORY);
    }

    public static final BooleanProperty HAS_INVENTORY = BooleanProperty.create("has_inventory"); // has drawer
}
