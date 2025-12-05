package moonfather.workshop_for_handsome_adventurer.blocks;

import moonfather.workshop_for_handsome_adventurer.CommonConfig;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.BaseContainerBlockEntity;
import moonfather.workshop_for_handsome_adventurer.block_entities.ToolRackBlockEntity;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import moonfather.workshop_for_handsome_adventurer.integration.PackingTape;
import moonfather.workshop_for_handsome_adventurer.integration.TetraCompatibleToolRackHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
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
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public class ToolRack extends Block implements EntityBlock, IBlockWithCleverHoverText
{
    public static Block.Properties getDefaultProperties()
    {
        return Properties.of().strength(2f, 3f).sound(SoundType.WOOD).ignitedByLava().mapColor(MapColor.COLOR_BROWN).pushReaction(PushReaction.DESTROY);
    }

    public ToolRack(int itemCount, String type, Properties properties)
    {
        this(itemCount, "tool_rack", type, properties);
    }

    public ToolRack(int itemCount, String mainType, @Nullable String subType, Properties properties)
    {
        super(properties);
        this.itemCount = itemCount;

        registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
        this.PrepareListOfShapes();

        String translationKeyStructure = "block.{0}.{1}_{2}.tooltip{3}";
        if (subType == null)
        {
            translationKeyStructure = "block.{0}.{1}.tooltip{3}";
        }
        String translationKey = MessageFormat.format(translationKeyStructure, Constants.MODID, mainType, subType, 1);
        this.Tooltip1 = Component.translatable(translationKey).withStyle(Style.EMPTY.withItalic(true).withColor(0xaa77dd));
        translationKey = MessageFormat.format(translationKeyStructure, Constants.MODID, mainType, subType, 2);
        this.Tooltip2 = Component.translatable(translationKey).withStyle(Style.EMPTY.withItalic(true).withColor(0xaa77dd));
    }

    public static ToolRack create(int itemCount, String type, Properties properties)
    {
        if (ModList.get().isLoaded("tetra"))
        {
            return TetraCompatibleToolRackHelper.create(false, itemCount, type, properties);
        }
        else
        {
            return new ToolRack(itemCount, type, properties);
        }
    }

    ////////////////////////////////////////////

    protected final int itemCount;
    protected MutableComponent Tooltip1;
    protected MutableComponent Tooltip2;

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_PLANK1N = Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 1.0D);
    private static final VoxelShape SHAPE_PLANK1E = Block.box(15.0D, 1.0D, 1.0D, 16.0D, 15.0D, 15.0D);
    private static final VoxelShape SHAPE_PLANK1S = Block.box(1.0D, 1.0D, 15.0D, 15.0D, 15.0D, 16.0D);
    private static final VoxelShape SHAPE_PLANK1W = Block.box(0.0D, 1.0D, 1.0D, 1.0D, 15.0D, 15.0D);



    @Override
    public VoxelShape getOcclusionShape(BlockState state)
    {
        return this.shapes.get(state.getValue(FACING));
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter p_60582_, BlockPos p_60583_)
    {
        return this.shapes.get(state.getValue(FACING));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
    {
        return this.shapes.get(state.getValue(FACING));
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter p_60548_, BlockPos p_60549_)
    {
        return this.shapes.get(state.getValue(FACING));
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }



    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        if (context.getClickedFace() == Direction.DOWN || context.getClickedFace() == Direction.UP)
        {
            return null;
        }
        BlockState result = this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
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
        Direction d = state.getValue(FACING).getOpposite();
        BlockPos target = pos.relative(d.getOpposite());
        return world.getBlockState(target).isFaceSturdy(world, target, d);
    }



    protected final Map<Direction, VoxelShape> shapes = new HashMap<Direction, VoxelShape>(4);

    protected void PrepareListOfShapes()
    {
        this.shapes.put(Direction.NORTH, SHAPE_PLANK1N);
        this.shapes.put(Direction.EAST, SHAPE_PLANK1E);
        this.shapes.put(Direction.SOUTH, SHAPE_PLANK1S);
        this.shapes.put(Direction.WEST, SHAPE_PLANK1W);
    }



    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState)
    {
        if (! blockState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) || blockState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER)
        {
            return new ToolRackBlockEntity(pos, blockState);
        }
        else
        {
            return null;
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_)
    {
        return null;
    }


    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston)
    {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
        if (! this.canSurvive(state, level, pos))
        {
            level.destroyBlock(pos, true);
        }
    }



    private final MutableComponent RackMessage = Component.translatable("message.workshop_for_handsome_adventurer.invalid_item_for_rack");


    @Override
    public InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos pos, Player player, BlockHitResult blockHitResult)
    {
        if (blockState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) && blockState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
        {
            BlockPos above = pos.above();
            return this.useWithoutItem(level.getBlockState(above), level, above, player, blockHitResult.withPosition(above));
        }
        boolean doOffhand = CommonConfig.OffhandInteractsWithToolRack.get();
        if (level.isClientSide())
        {
            return InteractionResult.SUCCESS;
            // we were doing just fine without this statement, updating both sides in parallel, but then CarryOn caused desyncs.
            // implementing getUpdatePacket() to return ClientboundBlockEntityDataPacket.create instead of nothing fixed "empty block entity" issue but desyncs remained when clicking quickly. so we're trying server-only plus forced update.
        }

        int slot = this.getTargetedSlot(blockHitResult);
        if (slot >= this.itemCount)
        {
            slot -= this.itemCount;
        }
        ToolRackBlockEntity BE = ((ToolRackBlockEntity) level.getBlockEntity(pos));
        ItemStack existing = BE.GetItem(slot);
        ItemStack itemInMainHand = player.getMainHandItem();
        ItemStack itemInOffHand = player.getOffhandItem();
        if (existing.isEmpty() && ! itemInMainHand.isEmpty())
        {
            if (! this.canDepositItem(itemInMainHand))
            {
                player.displayClientMessage(RackMessage, true);
                return InteractionResult.CONSUME; // we're not on client.
            }
            //System.out.println("~~~~~ADDED FROM MAIN");
            ItemStack toStore = itemInMainHand.copy();
            toStore.setCount(1);
            BE.DepositItem(slot, toStore);
            itemInMainHand.shrink(1);
            player.playSound(SoundEvents.WOOD_PLACE, 0.5f, 0.7f);
        }
        else if (existing.isEmpty() && itemInMainHand.isEmpty() && (! doOffhand || itemInOffHand.isEmpty()))
        {
            //System.out.println("~~~~~EMPTY TO EMPTY");
        }
        else if (existing.isEmpty() && itemInMainHand.isEmpty() && doOffhand && ! itemInOffHand.isEmpty())
        {
            if (! this.canDepositItem(itemInOffHand))
            {
                player.displayClientMessage(RackMessage, true);
                return InteractionResult.CONSUME; // we're not on client.
            }
            //System.out.println("~~~~~ADDED FROM OFFHAND");
            ItemStack toStore = itemInOffHand.copy();
            toStore.setCount(1);
            BE.DepositItem(slot, toStore);
            itemInOffHand.shrink(1);
            player.playSound(SoundEvents.WOOD_PLACE, 0.5f, 0.7f);
        }
        else if (! existing.isEmpty() && itemInMainHand.isEmpty() && itemInOffHand.isEmpty() && existing.has(DataComponents.BLOCKS_ATTACKS))  // no more canPerformAction(ItemAbilities.SHIELD_BLOCK)
        {
            //System.out.println("~~~~~TAKEN SHIELD");
            //player.addItem(existing);
            player.setItemInHand(InteractionHand.OFF_HAND, existing);
            BE.ClearItem(slot);
            player.playSound(SoundEvents.ITEM_PICKUP, 0.5f, 1);
        }
        else if (! existing.isEmpty() && itemInMainHand.isEmpty())
        {
            //System.out.println("~~~~~TAKEN WITH MAIN");
            player.setItemInHand(InteractionHand.MAIN_HAND, existing);
            BE.ClearItem(slot);
            player.playSound(SoundEvents.ITEM_PICKUP, 0.5f, 1);
        }
        else if (! existing.isEmpty() && ! itemInMainHand.isEmpty() && doOffhand && itemInOffHand.isEmpty())
        {
            //System.out.println("~~~~~TAKEN WITH OFFHAND");
            if (! itemInMainHand.has(DataComponents.BLOCKS_ATTACKS))
            {
                player.setItemInHand(InteractionHand.OFF_HAND, existing); // normal
            }
            else
            {
                player.setItemInHand(InteractionHand.OFF_HAND, itemInMainHand); // exception: move shield to offhand
                player.setItemInHand(InteractionHand.MAIN_HAND, existing);
            }
            BE.ClearItem(slot);
            player.playSound(SoundEvents.ITEM_PICKUP, 0.5f, 1);
        }
        else
        {
            //System.out.println("~~~~~BOTH FULL");
        }
        level.sendBlockUpdated(pos, blockState, blockState, 2);
        return InteractionResult.CONSUME; // we're not on client.
        //return super.use(blockState, level, pos, player, hand, blockHitResult);
    }



    public static int getToolRackSlot(ToolRack block, BlockHitResult blockHitResult)
    {
        return block.getTargetedSlot(blockHitResult);
    }

    protected int getTargetedSlot(BlockHitResult blockHitResult)
    {
        int aboveThisRow = 0;
        double frac = blockHitResult.getLocation().y - blockHitResult.getBlockPos().getY();
        if (frac >= 5 / 16d)
        {
            aboveThisRow = 0; /* row1*/
        }
        if (frac < 5 / 16d && frac >= -5 / 16d)
        {
            aboveThisRow = 2; /* row2*/
        }
        if (frac < -5 / 16d && frac >= -15 / 16d)
        {
            aboveThisRow = 4; /* row3*/
        }

        int integral;
        integral = (int) blockHitResult.getLocation().z;
        frac = (blockHitResult.getLocation().z - integral) * blockHitResult.getDirection().getStepX();
        integral = (int) blockHitResult.getLocation().x;
        frac -= (blockHitResult.getLocation().x - integral) * blockHitResult.getDirection().getStepZ();
        boolean left = (frac >= -0.5 && frac < 0) || frac >= 0.5;

        return aboveThisRow + (left ? 0 : 1);
    }

    protected boolean canDepositItem(ItemStack mainHandItem)
    {
        if (mainHandItem == null || mainHandItem.isEmpty())
        {
            return true;
        }
        if (mainHandItem.is(Constants.Tags.NOT_ALLOWED_ON_TOOLRACK) || mainHandItem.is(ItemTags.BOOKSHELF_BOOKS) || mainHandItem.is(Constants.Tags.COMMON_BOOKS))
        {
            return false;
        }
        if (mainHandItem.has(DataComponents.BLOCKS_ATTACKS))
        {
            return true;  // i'm disallowing DataComponents.EQUIPPABLE below but we must allow shiolds
        }
        if (mainHandItem.getMaxStackSize() > 1 && ! (mainHandItem.getItem().equals(Items.LEAD) || PackingTape.isTape(mainHandItem)))
        {
            return false;
        }
        if (mainHandItem.getItem() instanceof BlockItem || mainHandItem.has(DataComponents.EQUIPPABLE) )
        {
            // can not do  instanceof ArmorItem  and  instanceof AnimalArmorItem anymore.  might rule out some things i want on the rack.
            return false;
        }
        if (mainHandItem.get(DataComponents.FOOD) != null || mainHandItem.getItem() instanceof BucketItem || mainHandItem.getItem() instanceof MinecartItem || mainHandItem.getItem() instanceof BoatItem)
        {
            return false;
        }
        if (mainHandItem.get(DataComponents.POTION_CONTENTS) != null)
        {
            return false;
        }
        return true;
    }
}
