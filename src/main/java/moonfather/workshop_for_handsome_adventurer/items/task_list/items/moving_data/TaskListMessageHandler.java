package moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data;

import moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities.BasicBlockEntity;
import moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities.TaskListBlockEntity;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.TaskListItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class TaskListMessageHandler
{
    public static void handleItemMessage(final TaskPageMessage message, final IPayloadContext context)
    {
        // as of 1.20.6, things are by default handled on main thread
        try
        {
            if (context.player() instanceof ServerPlayer sp)         // corresponds to the client player that sent this packet
            {
                findAndUpdateItemStack(sp, message.value(), message.extra());
            }
        }
        catch (Exception e)
        {
            context.disconnect(Component.literal("Networking error in WFHA mod, msg654:  \n" + e.getMessage()));
        }
    }
    private static void findAndUpdateItemStack(ServerPlayer sp, TaskListMessaging.TaskPageDTO value, TaskListMessaging.TaskListExtraDTO extra)
    {
        ItemStack itemStack = ItemStack.EMPTY;
        TaskListBlockEntity tile = null;
        if (! extra.listIsInBlock())
        {
            itemStack = extra.mainHand() ? sp.getMainHandItem() : sp.getOffhandItem();
        }
        else
        {
            if (sp.level().getBlockEntity(extra.tilePos()) instanceof TaskListBlockEntity tileValue)
            {
                itemStack = tileValue.getItem();
                tile = tileValue;
            }
        }
        if (! itemStack.is(TaskListItem.Utility.ourItem()))
        {
            sp.displayClientMessage(Component.literal("error: didn't find task list."), false);
            return;
        }
        //////////
        updateItemStack(itemStack, value, extra);
        //////////
        if (extra.listIsInBlock() && tile != null)
        {
            tile.setItem(itemStack);
            BasicBlockEntity.sendUpdated(sp.level(), extra.tilePos());
        }
    }

    private static void updateItemStack(ItemStack itemStack, TaskListMessaging.TaskPageDTO value, TaskListMessaging.TaskListExtraDTO extra)
    {
        int pageNumber = value.pageNumber();
        TaskListComponent old = itemStack.get(TaskListItem.Utility.ourComponent());
        TaskListComponent updated;
        if (old != null)
        {
            updated = old.makeCopy(value.pageNumber(), extra.lastPage());
        }
        else
        {
            updated = new TaskListComponent(1, 1, null);
            pageNumber = 1;
        }
        updated.updatePage(pageNumber, value);
        updated.setLastPage(pageNumber);
        itemStack.set(TaskListItem.Utility.ourComponent(), updated);
        TaskListItem.Utility.setTitle(itemStack, extra.itemName());
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    public static void handleBlockPagingMessage(BlockPagingMessage message, IPayloadContext payloadContext)
    {
        try
        {
            if (payloadContext.player() instanceof ServerPlayer sp)         // corresponds to the client player that sent this packet
            {
                if (sp.level().getBlockEntity(message.pos()) instanceof TaskListBlockEntity tile)
                {
                    if (message.movingRight())
                    {
                        tile.onArrowNext();
                    }
                    else
                    {
                        tile.onArrowPrev();
                    }
                }
            }
        }
        catch (Exception e)
        {
            payloadContext.disconnect(Component.literal("Networking error in WFHA mod, msg656:  \n" + e.getMessage()));
        }
    }

    public static void handleBlockCheckmarkMessage(BlockCheckmarkMessage message, IPayloadContext payloadContext)
    {
        try
        {
            if (payloadContext.player() instanceof ServerPlayer sp)         // corresponds to the client player that sent this packet
            {
                if (sp.level().getBlockEntity(message.pos()) instanceof TaskListBlockEntity tile)
                {
                    tile.updateCheckmark(message.index(), message.pageNumber());
                }
            }
        }
        catch (Exception e)
        {
            payloadContext.disconnect(Component.literal("Networking error in WFHA mod, msg657:  \n" + e.getMessage()));
        }
    }
}
