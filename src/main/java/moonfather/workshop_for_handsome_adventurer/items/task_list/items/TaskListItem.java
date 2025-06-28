package moonfather.workshop_for_handsome_adventurer.items.task_list.items;

import moonfather.workshop_for_handsome_adventurer.items.task_list.RegistrationForTaskList;
import moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities.TaskListBlockEntity;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListComponent;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListComponentStorage;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListMessaging;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.screens.TaskListClientInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class TaskListItem extends Item
{
    public TaskListItem(boolean fireImmune)
    {
        super(fireImmune ? (new Properties()).stacksTo(1).fireResistant() : (new Properties()).stacksTo(1));
    }



    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        if (level.isClientSide())
        {
            TaskListComponent data = TaskListItem.Component.get(player.getItemInHand(hand));
            if (data == null)
            {
                data = new TaskListComponent(1, 1, null);
            }
            String itemNameAsText = TaskListItem.Utility.getTitle(player.getItemInHand(hand));
            TaskListMessaging.TaskListExtraDTO extra = new TaskListMessaging.TaskListExtraDTO(hand.equals(InteractionHand.MAIN_HAND), data.getLastPageNumber(), itemNameAsText);
            TaskListClientInvoker.invokeScreen(data.getAllPages(), data.getPageCount(), extra, TaskListItem.Utility.isFireImmune(player.getItemInHand(hand)));
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        if (context.getPlayer() != null && context.getPlayer().isCrouching())
        {
            if (! context.getClickedFace().equals(Direction.UP) && ! context.getClickedFace().equals(Direction.DOWN))
            {
                if (! context.getLevel().isClientSide())
                {
                    BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
                    BlockState existing = context.getLevel().getBlockState(pos);
                    if (! existing.isAir() && ! existing.canBeReplaced())
                    {
                        return InteractionResult.FAIL;
                    }
                    BlockState newState = RegistrationForTaskList.TASK_LIST_PANEL.get().getStateForPlacement(new BlockPlaceContext(context));
                    if (newState == null)
                    {
                        return InteractionResult.FAIL;
                    }
                    context.getLevel().setBlockAndUpdate(pos, newState);
                    TaskListBlockEntity tile = (TaskListBlockEntity) context.getLevel().getBlockEntity(pos);
                    if (tile == null)
                    {
                        context.getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        return InteractionResult.FAIL;
                    }
                    tile.setItem(context.getItemInHand().copy());
                    tile.sendUpdated();
                    context.getItemInHand().shrink(1);
                    return InteractionResult.CONSUME;
                }
                else
                {
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.useOn(context);
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, Level level)
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isNotReplaceableByPickAction(ItemStack stack, Player player, int inventorySlot) { return true; }

    @Override
    public boolean canBeHurtBy(DamageSource source) { return ! source.is(DamageTypes.MAGIC) && ! source.is(DamageTypeTags.IS_EXPLOSION) && ! source.is(DamageTypes.CACTUS) && super.canBeHurtBy(source); }

    /////////////////

    public static class Utility
    {
        public static String getTitle(ItemStack taskList)
        {
            return taskList.getHoverName().getString();
        }

        public static void setTitle(ItemStack taskList, String title)
        {
            taskList.setHoverName(net.minecraft.network.chat.Component.literal(title));
        }

        public static boolean isFireImmune(ItemStack taskList)
        {
            return taskList.is(RegistrationForTaskList.TASK_LIST_FI.get());
        }

        public static ItemStack createInstance()
        {
            return RegistrationForTaskList.TASK_LIST.get().getDefaultInstance();
        }

        public static boolean isOurItem(ItemStack something)
        {
            return something.is(RegistrationForTaskList.TASK_LIST.get()) || something.is(RegistrationForTaskList.TASK_LIST_FI.get());
        }
        public static boolean isOurItemBasic(ItemStack something)
        {
            return something.is(RegistrationForTaskList.TASK_LIST.get());
        }

        public static ItemStack createInstanceFireImmune() { return RegistrationForTaskList.TASK_LIST_FI.get().getDefaultInstance(); }

        public static Item ourItemBasic() { return RegistrationForTaskList.TASK_LIST_FI.get(); }
    }



    public static class Component
    {
        public static TaskListComponent get(ItemStack itemStack)
        {
            if (! TaskListItem.Utility.isOurItem(itemStack) || ! itemStack.hasTag() || ! itemStack.getTag().contains("task_list")) return null;
            return TaskListComponentStorage.readComponent(itemStack.getTag().getCompound("task_list"));
        }

        public static void set(ItemStack itemStack, TaskListComponent c)
        {
            itemStack.getOrCreateTag().put("task_list", TaskListComponentStorage.storeComponent(c));
        }
    }
}
