package moonfather.workshop_for_handsome_adventurer.blocks;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.PotionShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PotionShelf extends ToolRack
{
    public PotionShelf() {
        super(PotionShelfBlockEntity.CAPACITY, "----");
        String translationKeyStructure = "block.%s.potion_shelf.tooltip%d";
        String translationKey = String.format(translationKeyStructure, Constants.MODID, 1);
        this.Tooltip1 = new TranslatableComponent(translationKey).withStyle(Style.EMPTY.withItalic(true).withColor(0xaa77dd));
        translationKey = String.format(translationKeyStructure, Constants.MODID, 2);
        this.Tooltip2 = new TranslatableComponent(translationKey).withStyle(Style.EMPTY.withItalic(true).withColor(0xaa77dd));
        this.PrepareListOfShapes();
    }



    @Override
    protected boolean canDepositItem(ItemStack mainHandItem)
    {
        if (mainHandItem == null || mainHandItem.isEmpty())
        {
            return true;
        }
        if (mainHandItem.getTag() != null && mainHandItem.getTag().contains("Potion"))
        {
            return true;
        }
        if (mainHandItem.is(Items.GLASS_BOTTLE))
        {
            return true;
        }
        return false;
    }



    public static int getTargetedSlot(BlockHitResult blockHitResult)
    {
        int aboveThisRow = 0;
        double frac = blockHitResult.getLocation().y - blockHitResult.getBlockPos().getY();
        if (frac < 8/16d) { aboveThisRow = 3; /* row2*/ }

        int integral;
        integral = (int) blockHitResult.getLocation().z;
        frac = (blockHitResult.getLocation().z - integral) * blockHitResult.getDirection().getStepX();
        integral = (int) blockHitResult.getLocation().x;
        frac -= (blockHitResult.getLocation().x - integral) * blockHitResult.getDirection().getStepZ();
        int horizontalIndex;
        if ((frac >= -1/3d && frac < 0) || (frac >= 2/3d && frac < 1))
        {
            horizontalIndex = 0; //left
        }
        else if ((frac >= 1/3d && frac < 2/3d) || (frac - 1 >= 1/3d && frac - 1 < 2/3d) || (frac + 1 >= 1/3d && frac + 1 < 2/3d))
        {
            horizontalIndex = 1; //mid
        }
        else
        {
            horizontalIndex = 2; //right
        }
        return aboveThisRow + horizontalIndex;
    }



    private final TranslatableComponent ShelfMessage = new TranslatableComponent("message.workshop_for_handsome_adventurer.shelf_invalid_item");
    private final TranslatableComponent MaxedMessage = new TranslatableComponent("message.workshop_for_handsome_adventurer.shelf_slot_maxed");
    private final TranslatableComponent RemainingRoomMessage = new TranslatableComponent("message.workshop_for_handsome_adventurer.shelf_remaining_room");
    private final TranslatableComponent RemainingItemsMessage = new TranslatableComponent("message.workshop_for_handsome_adventurer.shelf_remaining_items");
    private final TranslatableComponent WrongPotionMessage = new TranslatableComponent("message.workshop_for_handsome_adventurer.shelf_wrong_potion");


    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult)
    {
        if (hand == InteractionHand.OFF_HAND)
        {
            return InteractionResult.PASS;
        }
        if (! this.canDepositItem(player.getMainHandItem()))
        {
            player.displayClientMessage(ShelfMessage, true);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (level.isClientSide)
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
        PotionShelfBlockEntity BE = ((PotionShelfBlockEntity)level.getBlockEntity(pos));
        ItemStack existing = BE.GetItem(slot);
        if (existing.isEmpty() && ! player.getMainHandItem().isEmpty())
        {
            //System.out.println("~~~~~DEPOSIT TO EMPTY");
            if (player.getMainHandItem().getMaxStackSize() > 1 && player.isCrouching()) {
                BE.DepositPotionStack(slot, player.getMainHandItem());
            }
            else {
                BE.DepositPotion(slot, player.getMainHandItem());
            }
            player.playSound(SoundEvents.WOOD_PLACE, 0.5f, 0.7f);
            TranslatableComponent remainingRoomMessage = new TranslatableComponent("message.workshop_for_handsome_adventurer.shelf_remaining_room");
            player.displayClientMessage(remainingRoomMessage.append(BE.GetRemainingRoom(slot).toString()), true);
        }
        else if (existing.isEmpty() && player.getMainHandItem().isEmpty())
        {
            //System.out.println("~~~~~EMPTY TO EMPTY");
        }
        else if (! existing.isEmpty() && player.getMainHandItem().isEmpty())
        {
            if (existing.getMaxStackSize() > 1 && player.isCrouching()) {
                //System.out.println("~~~~~TAKEN STACK");
                player.setItemInHand(InteractionHand.MAIN_HAND, BE.TakeOutPotionStack(slot));
            }
            else {
                //System.out.println("~~~~~TAKEN");
                player.setItemInHand(InteractionHand.MAIN_HAND, BE.TakeOutPotion(slot));
            }
            player.playSound(SoundEvents.ITEM_PICKUP, 0.5f, 1);
            if (BE.GetRemainingItems(slot) > 0) {
                TranslatableComponent remainingItemsMessage = new TranslatableComponent("message.workshop_for_handsome_adventurer.shelf_remaining_items");
                player.displayClientMessage(remainingItemsMessage.append(BE.GetRemainingItems(slot).toString()), true);
            }
        }
        else
        {
            //System.out.println("~~~~~BOTH FULL");
            if (ItemStack.isSameItemSameTags(existing, player.getMainHandItem()))
            {
                if (BE.IsSlotMaxed(slot))
                {
                    player.displayClientMessage(MaxedMessage, true);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
                if (player.getMainHandItem().getMaxStackSize() > 1 && player.isCrouching()) {
                    BE.DepositPotionStack(slot, player.getMainHandItem());
                }
                else {
                    BE.DepositPotion(slot, player.getMainHandItem());
                }
                player.playSound(SoundEvents.WOOD_PLACE, 0.5f, 0.7f);
                TranslatableComponent remainingRoomMessage = new TranslatableComponent("message.workshop_for_handsome_adventurer.shelf_remaining_room");
                player.displayClientMessage(remainingRoomMessage.append(BE.GetRemainingRoom(slot).toString()), true);
            }
            else {
                player.displayClientMessage(WrongPotionMessage, true);
            }
        }
        level.sendBlockUpdated(pos, blockState, blockState, 2);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getPlayer().isCrouching()) {
            BlockState state = event.getWorld().getBlockState(event.getPos());
            if (state.getBlock() instanceof PotionShelf && event.getFace() == state.getValue(FACING).getOpposite()) {
                if (! event.getPlayer().getMainHandItem().isEmpty()) {
                    event.setUseBlock(Event.Result.ALLOW);
                }
            }
        }
    }
    /////////////////////////////////////

    private static final VoxelShape SHAPE_FRAME1N = Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 3.0D);
    private static final VoxelShape SHAPE_FRAME1E = Block.box(13.0D, 1.0D, 1.0D, 16.0D, 15.0D, 15.0D);
    private static final VoxelShape SHAPE_FRAME1S = Block.box(1.0D, 1.0D, 13.0D, 15.0D, 15.0D, 16.0D);
    private static final VoxelShape SHAPE_FRAME1W = Block.box(0.0D, 1.0D, 1.0D, 3.0D, 15.0D, 15.0D);
    private final Map<Direction, VoxelShape> frameShapes = new HashMap<Direction, VoxelShape>(4);

    private void PrepareListOfShapes()
    {
        this.frameShapes.put(Direction.NORTH, SHAPE_FRAME1N);
        this.frameShapes.put(Direction.EAST,  SHAPE_FRAME1E);
        this.frameShapes.put(Direction.SOUTH, SHAPE_FRAME1S);
        this.frameShapes.put(Direction.WEST,  SHAPE_FRAME1W);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter p_60579_, BlockPos p_60580_)
    {
        return this.frameShapes.get(state.getValue(FACING));
    }


    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter p_60582_, BlockPos p_60583_)
    {
        return this.frameShapes.get(state.getValue(FACING));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
    {
        return this.frameShapes.get(state.getValue(FACING));
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter p_60548_, BlockPos p_60549_)
    {
        return super.getInteractionShape(state, p_60548_, p_60549_);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return Registration.POTION_SHELF_BE.get().create(pos, blockState);
    }

    ///////////////////////////////////////////////////////////////

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        PotionShelfBlockEntity BE = ((PotionShelfBlockEntity)level.getBlockEntity(pos));
        if (BE == null || ! state.hasProperty(FACING)) { return Items.STICK.getDefaultInstance(); }
        BlockHitResult bhr = new BlockHitResult(target.getLocation(), state.getValue(FACING).getOpposite(), pos, true);
        int slot = PotionShelf.getTargetedSlot(bhr);
        ItemStack existing = BE.GetItem(slot);
        if (! existing.isEmpty())
        {
            return existing.copy();
        }
        String wood = this.getRegistryName().getPath();
        wood = wood.substring(wood.indexOf("_", 8) + 1);
        return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Registration.getHostMod(wood), "potion_shelf_" + wood)));
    }
}
