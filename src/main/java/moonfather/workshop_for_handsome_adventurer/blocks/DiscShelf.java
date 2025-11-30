package moonfather.workshop_for_handsome_adventurer.blocks;

import moonfather.workshop_for_handsome_adventurer.block_entities.DiscShelfBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class DiscShelf extends PotionShelf
{
    public DiscShelf(Properties properties)
    {
        super(SLOT_COUNT, "disc_shelf", null, properties);
        ShelfMessage = Component.translatable("message.workshop_for_handsome_adventurer.shelf_invalid_item2");
        MaxedMessage = Component.translatable("message.workshop_for_handsome_adventurer.shelf_slot_maxed");
        RemainingItemsKey = "message.workshop_for_handsome_adventurer.shelf_remaining_items2";
        NotTheSameTypeMessage = Component.translatable("message.workshop_for_handsome_adventurer.shelf_wrong_potion2");
        HintMessage = Component.translatable("message.workshop_for_handsome_adventurer.shelf_hint2");
    }
    public static final int SLOT_COUNT = 9;



    @Override
    protected boolean canDepositItem(@NotNull ItemStack mainHandItem)
    {
        if (mainHandItem.isEmpty())
        {
            return true;
        }
        if (mainHandItem.is(Tags.Items.MUSIC_DISCS) || mainHandItem.is(ItemTags.CREEPER_DROP_MUSIC_DISCS))
        {
            return true;
        }
        return false;
    }



    @Override
    protected boolean canInteractWithOffhand()
    {
        return true;
    }
    @Override
    public int getShelfSlot(BlockHitResult blockHitResult)
    {
        return getDiscShelfSlot(blockHitResult);
    }

    public static int getDiscShelfSlot(BlockHitResult blockHitResult)
    {
        return getDiscShelfSlot(blockHitResult, blockHitResult.getBlockPos(), blockHitResult.getDirection());
    }

    public static int getDiscShelfSlot(HitResult hitResult, BlockPos blockPos, Direction direction)
    {
        int aboveThisRow = 0;
        double frac = hitResult.getLocation().y - blockPos.getY();
        if (frac < 11 / 16d)
        {
            aboveThisRow = 3; /* row2*/
        }
        if (frac < 7 / 16d)
        {
            aboveThisRow = 6; /* row3*/
        }

        int integral;
        integral = (int) hitResult.getLocation().z;
        frac = (hitResult.getLocation().z - integral) * direction.getStepX();
        integral = (int) hitResult.getLocation().x;
        frac -= (hitResult.getLocation().x - integral) * direction.getStepZ();
        int horizontalIndex;
        if ((frac >= -1 / 3d && frac < 0) || (frac >= 2 / 3d && frac < 1))
        {
            horizontalIndex = 0; //left
        }
        else if ((frac >= 1 / 3d && frac < 2 / 3d) || (frac - 1 >= 1 / 3d && frac - 1 < 2 / 3d) || (frac + 1 >= 1 / 3d && frac + 1 < 2 / 3d))
        {
            horizontalIndex = 1; //mid
        }
        else
        {
            horizontalIndex = 2; //right
        }
        return aboveThisRow + horizontalIndex;
    }

    

    @Override
    public InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos pos, Player player, BlockHitResult blockHitResult)
    {
        if (blockHitResult.getDirection().equals(Direction.UP) || blockHitResult.getDirection().equals(Direction.DOWN))
        {
            return InteractionResult.PASS; // allow from sides; prevent weirdness from above
        }
        return super.useWithoutItem(blockState, level, pos, player, blockHitResult);  // same behavior
    }

    /////////////////////////////////////

    private static final VoxelShape SHAPE_FRAME1N = Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 2.0D);
    private static final VoxelShape SHAPE_FRAME1E = Block.box(14.0D, 1.0D, 1.0D, 16.0D, 15.0D, 15.0D);
    private static final VoxelShape SHAPE_FRAME1S = Block.box(1.0D, 1.0D, 14.0D, 15.0D, 15.0D, 16.0D);
    private static final VoxelShape SHAPE_FRAME1W = Block.box(0.0D, 1.0D, 1.0D, 2.0D, 15.0D, 15.0D);
    private static final VoxelShape SHAPE_FRAME2N = Block.box(1.0D, 1.0D, 2.0D, 15.0D, 7.0D, 5.0D);
    private static final VoxelShape SHAPE_FRAME2E = Block.box(11.0D, 1.0D, 1.0D, 14.0D, 7.0D, 15.0D);
    private static final VoxelShape SHAPE_FRAME2S = Block.box(1.0D, 1.0D, 11.0D, 15.0D, 7.0D, 14.0D);
    private static final VoxelShape SHAPE_FRAME2W = Block.box(2.0D, 1.0D, 1.0D, 5.0D, 7.0D, 15.0D);

    @Override
    protected void PrepareListOfShapes()
    {
        this.shapes.clear();
        this.shapes.put(Direction.NORTH, Shapes.or(SHAPE_FRAME1N, SHAPE_FRAME2N));
        this.shapes.put(Direction.EAST, Shapes.or(SHAPE_FRAME1E, SHAPE_FRAME2E));
        this.shapes.put(Direction.SOUTH, Shapes.or(SHAPE_FRAME1S, SHAPE_FRAME2S));
        this.shapes.put(Direction.WEST, Shapes.or(SHAPE_FRAME1W, SHAPE_FRAME2W));
    }



    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState blockState)
    {
        return new DiscShelfBlockEntity(pos, blockState);
    }
}
