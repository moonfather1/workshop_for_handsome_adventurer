package moonfather.workshop_for_handsome_adventurer.blocks;

import moonfather.workshop_for_handsome_adventurer.CommonConfig;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.PotionShelfBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class PotionShelf extends ToolRack
{
    public PotionShelf(Block.Properties properties)
    {
        this(SLOT_COUNT, "potion_shelf", null, properties);
    }
    public PotionShelf(int itemCount, String mainType, @Nullable String subType, Properties properties)
    {
        super(itemCount, mainType, subType, properties);
        ShelfMessage = Component.translatable("message.workshop_for_handsome_adventurer.shelf_invalid_item");
        MaxedMessage = Component.translatable("message.workshop_for_handsome_adventurer.shelf_slot_maxed");
        RemainingRoomKey = "message.workshop_for_handsome_adventurer.shelf_remaining_room";
        RemainingItemsKey = "message.workshop_for_handsome_adventurer.shelf_remaining_items";
        NotTheSameTypeMessage = Component.translatable("message.workshop_for_handsome_adventurer.shelf_wrong_potion");
        HintMessage = Component.translatable("message.workshop_for_handsome_adventurer.shelf_hint");
    }
    public static final int SLOT_COUNT = 6;

    protected MutableComponent ShelfMessage, MaxedMessage, NotTheSameTypeMessage, HintMessage;
    protected String RemainingItemsKey, RemainingRoomKey;



    @Override
    protected boolean canDepositItem(@NotNull ItemStack mainHandItem)
    {
        if (mainHandItem.isEmpty())
        {
            return true;
        }
        if (mainHandItem.get(DataComponents.POTION_CONTENTS) != null)
        {
            return true;
        }
        if (mainHandItem.is(Items.GLASS_BOTTLE))
        {
            return true;
        }
        if (mainHandItem.is(Constants.Tags.ALLOWED_ON_POTION_SHELF))
        {
            return true;
        }
        return false;
    }



    protected boolean canInteractWithOffhand()
    {
        return CommonConfig.OffhandInteractsWithPotionShelf.isTrue();
    }

    public int getShelfSlot(BlockHitResult blockHitResult)
    {
        return getPotionShelfSlot(blockHitResult);
    }

    public static int getPotionShelfSlot(BlockHitResult blockHitResult)
    {
        return getPotionShelfSlot(blockHitResult, blockHitResult.getBlockPos(), blockHitResult.getDirection());
    }

    public static int getPotionShelfSlot(HitResult hitResult, BlockPos blockPos, Direction direction)
    {
        int aboveThisRow = 0;
        double frac = hitResult.getLocation().y - blockPos.getY();
        if (frac < 8 / 16d)
        {
            aboveThisRow = 3; /* row2*/
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
        if (! this.canDepositItem(player.getMainHandItem())
                &&
                ! (this.canInteractWithOffhand() && ! player.getOffhandItem().isEmpty() && this.canDepositItem(player.getOffhandItem())))
        {
            player.sendOverlayMessage(ShelfMessage);
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }

        if (level.isClientSide())
        {
            return InteractionResult.SUCCESS;
        }
        int slot = this.getShelfSlot(blockHitResult);
        if (slot >= this.itemCount)
        {
            slot -= this.itemCount;
        }
        PotionShelfBlockEntity BE = ((PotionShelfBlockEntity) level.getBlockEntity(pos));
        ItemStack existing = BE.GetItem(slot);
        if (existing.isEmpty() && ((! player.getMainHandItem().isEmpty() && this.canDepositItem(player.getMainHandItem()))
                || (this.canInteractWithOffhand() && ! player.getOffhandItem().isEmpty() && this.canDepositItem(player.getOffhandItem()))))
        {
            //System.out.println("~~~~~DEPOSIT TO EMPTY");
            ItemStack itemToDeposit;
            if (this.canDepositItem(player.getMainHandItem()) && ! player.getMainHandItem().isEmpty())
            {
                itemToDeposit = player.getMainHandItem();
            }
            else
            {
                itemToDeposit = player.getOffhandItem();
            }
            boolean showHintAboutOtherSlot = BE.hasSlotWithSameItemWithRemainingRoom(slot, itemToDeposit);
            if (itemToDeposit.getMaxStackSize() > 1 && player.isCrouching())
            {
                BE.DepositPotionStack(slot, itemToDeposit);
            }
            else
            {
                BE.DepositPotion(slot, itemToDeposit);
            }  // done depositing, now a message
            if (showHintAboutOtherSlot)
            {
                player.sendOverlayMessage(HintMessage);
            }
            else if (BE.GetRemainingRoom(slot) + BE.GetRemainingItems(slot) > 1)  // no message if no stacking
            {
                player.playSound(SoundEvents.WOOD_PLACE, 0.5f, 0.7f);
                MutableComponent remainingRoomMessage = Component.translatable(RemainingRoomKey);
                player.sendOverlayMessage(remainingRoomMessage.append(BE.GetRemainingRoom(slot).toString()));
            }
        }
        else if (existing.isEmpty() && player.getMainHandItem().isEmpty() && (! this.canInteractWithOffhand() || player.getOffhandItem().isEmpty()))
        {
            //System.out.println("~~~~~EMPTY TO EMPTY");
        }
        else if (! existing.isEmpty() && player.getMainHandItem().isEmpty())
        {
            if (existing.getMaxStackSize() > 1 && player.isCrouching())
            {
                //System.out.println("~~~~~TAKEN STACK");
                player.setItemInHand(InteractionHand.MAIN_HAND, BE.TakeOutPotionStack(slot));
            }
            else
            {
                //System.out.println("~~~~~TAKEN");
                player.setItemInHand(InteractionHand.MAIN_HAND, BE.TakeOutPotion(slot));
            }
            player.playSound(SoundEvents.ITEM_PICKUP, 0.5f, 1);
            if (BE.GetRemainingItems(slot) > 0)
            {
                MutableComponent remainingItemsMessage = Component.translatable(RemainingItemsKey);
                player.sendOverlayMessage(remainingItemsMessage.append(BE.GetRemainingItems(slot).toString()));
            }
        }
        else
        {
            //System.out.println("~~~~~BOTH FULL");
            if (ItemStack.isSameItemSameComponents(existing, player.getMainHandItem()))
            {
                if (BE.IsSlotMaxed(slot))
                {
                    player.sendOverlayMessage(MaxedMessage);
                    return InteractionResult.SUCCESS_SERVER;
                }
                if (player.getMainHandItem().getMaxStackSize() > 1 && player.isCrouching())
                {
                    BE.DepositPotionStack(slot, player.getMainHandItem());
                }
                else
                {
                    BE.DepositPotion(slot, player.getMainHandItem());
                }
                player.playSound(SoundEvents.WOOD_PLACE, 0.5f, 0.7f);
                MutableComponent remainingRoomMessage = Component.translatable(RemainingRoomKey);
                player.sendOverlayMessage(remainingRoomMessage.append(BE.GetRemainingRoom(slot).toString()));
            }
            else if (! player.getOffhandItem().isEmpty() && this.canInteractWithOffhand() && ItemStack.isSameItemSameComponents(existing, player.getOffhandItem()))
            {
                if (BE.IsSlotMaxed(slot))
                {
                    player.sendOverlayMessage(MaxedMessage);
                    return InteractionResult.SUCCESS_SERVER;
                }
                if (player.getOffhandItem().getMaxStackSize() > 1 && player.isCrouching())
                {
                    BE.DepositPotionStack(slot, player.getOffhandItem());
                }
                else
                {
                    BE.DepositPotion(slot, player.getOffhandItem());
                }
                player.playSound(SoundEvents.WOOD_PLACE, 0.5f, 0.7f);
                MutableComponent remainingRoomMessage = Component.translatable(RemainingRoomKey);
                player.sendOverlayMessage(remainingRoomMessage.append(BE.GetRemainingRoom(slot).toString()));
            }
            else
            {
                player.sendOverlayMessage(NotTheSameTypeMessage);
            }
        }
        level.sendBlockUpdated(pos, blockState, blockState, 2);
        return InteractionResult.SUCCESS_SERVER;
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getEntity().isCrouching())
        {
            BlockState state = event.getLevel().getBlockState(event.getPos());
            if (state.getBlock() instanceof PotionShelf && event.getFace() == state.getValue(FACING).getOpposite())
            {
                if (! event.getEntity().getMainHandItem().isEmpty())
                {
                    event.setUseBlock(TriState.TRUE);
                }
            }
        }
    }
    /////////////////////////////////////

    private static final VoxelShape SHAPE_FRAME1N = Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 3.0D);
    private static final VoxelShape SHAPE_FRAME1E = Block.box(13.0D, 1.0D, 1.0D, 16.0D, 15.0D, 15.0D);
    private static final VoxelShape SHAPE_FRAME1S = Block.box(1.0D, 1.0D, 13.0D, 15.0D, 15.0D, 16.0D);
    private static final VoxelShape SHAPE_FRAME1W = Block.box(0.0D, 1.0D, 1.0D, 3.0D, 15.0D, 15.0D);

    @Override
    protected void PrepareListOfShapes()
    {
        this.shapes.clear();
        this.shapes.put(Direction.NORTH, SHAPE_FRAME1N);
        this.shapes.put(Direction.EAST, SHAPE_FRAME1E);
        this.shapes.put(Direction.SOUTH, SHAPE_FRAME1S);
        this.shapes.put(Direction.WEST, SHAPE_FRAME1W);
    }



    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState blockState)
    {
        return new PotionShelfBlockEntity(pos, blockState);
    }
}
