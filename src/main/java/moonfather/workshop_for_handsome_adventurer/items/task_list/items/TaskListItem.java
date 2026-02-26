package moonfather.workshop_for_handsome_adventurer.items.task_list.items;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.items.task_list.RegistrationForTaskList;
import moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities.TaskListBlockEntity;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListComponent;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListMessaging;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.screens.TaskListClientInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DamageResistant;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

public class TaskListItem extends Item
{
    public TaskListItem(String id)
    {
        super((new Properties()).stacksTo(1).setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MODID, id))));
    }
    //  abandoned, nice to have: tooltip line saying Completed: (orange) 0/6 (spacing) (greenish)5/6
    //  abandoned, consideration: consider keyboard paging
    //  less imp: make it a furnace fuel?
    //  https://www.curseforge.com/minecraft/mc-mods/modopedia


    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand)
    {
        if (level.isClientSide())
        {
            TaskListComponent data = player.getItemInHand(hand).get(TaskListItem.Utility.ourComponent());
            if (data == null)
            {
                data = new TaskListComponent(1, 1, null);
            }
            String itemNameAsText = TaskListItem.Utility.getTitle(player.getItemInHand(hand));
            TaskListMessaging.TaskListExtraDTO extra = new TaskListMessaging.TaskListExtraDTO(hand.equals(InteractionHand.MAIN_HAND), data.getLastPageNumber(), itemNameAsText);
            TaskListClientInvoker.invokeScreen(data.getAllPages(), data.getPageCount(), extra, TaskListItem.Utility.isFireImmune(player.getItemInHand(hand)));
            return InteractionResult.SUCCESS;
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResult.CONSUME;
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
    public boolean canBeHurtBy(ItemStack stack, DamageSource source) { return ! source.is(Tags.DamageTypes.IS_MAGIC) && ! source.is(DamageTypeTags.IS_EXPLOSION) && ! source.is(DamageTypes.CACTUS) && super.canBeHurtBy(stack, source); }

    /////////////////

    public static class Utility
    {
        public static String getTitle(ItemStack taskList)
        {
            Component itemName = taskList.get(DataComponents.CUSTOM_NAME);
            return itemName != null ? itemName.getString() : taskList.getHoverName().getString();
        }

        public static void setTitle(ItemStack taskList, String title)
        {
            taskList.set(DataComponents.CUSTOM_NAME, Component.literal(title));
        }

        public static boolean isFireImmune(ItemStack taskList)
        {
            return taskList.has(RegistrationForTaskList.FIRE_RESISTANT);
        }

        public static void setFireImmune(ItemStack taskList)
        {
            taskList.set(RegistrationForTaskList.FIRE_RESISTANT, Unit.INSTANCE);  // don't ask.  this wasn't retarded in 1.21.1, but now it is.
            taskList.set(DataComponents.DAMAGE_RESISTANT, new DamageResistant(DamageTypeTags.IS_FIRE));
        }

        public static ItemStack createInstance()
        {
            return RegistrationForTaskList.TASK_LIST.get().getDefaultInstance();
        }

        public static Item ourItem()
        {
            return RegistrationForTaskList.TASK_LIST.get();
        }

        public static DataComponentType<TaskListComponent> ourComponent()
        {
            return RegistrationForTaskList.TASK_LIST_CONTENT.get();
        }
    }
}
