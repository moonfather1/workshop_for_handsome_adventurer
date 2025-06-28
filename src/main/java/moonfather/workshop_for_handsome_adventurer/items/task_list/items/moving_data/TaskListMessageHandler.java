package moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data;

import com.mojang.logging.LogUtils;
import moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities.BasicBlockEntity;
import moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities.TaskListBlockEntity;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.TaskListItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TaskListMessageHandler
{
    public static void handleItemMessage(final TaskPageMessage message, Supplier<NetworkEvent.Context> contextSupplier)
    {
        try
        {
            contextSupplier.get().enqueueWork(() -> {
                // Work that needs to be threadsafe (most work)
                ServerPlayer player = contextSupplier.get().getSender(); // the client that sent this packet
                // do stuff
                findAndUpdateItemStack(player, message.getPage(), message.getExtra());
            });
            contextSupplier.get().setPacketHandled(true);
        }
        catch (Exception e)
        {
            LogUtils.getLogger().error("Networking error in WFHA mod, msg654:  \n" + e.getMessage());
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
        if (! TaskListItem.Utility.isOurItem(itemStack))
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
            // sp.level().blockEntityChanged(extra.tilePos());    this doesn't work, call below does.
            BasicBlockEntity.sendUpdated(sp.level(), extra.tilePos());
        }
    }

    private static void updateItemStack(ItemStack itemStack, TaskListMessaging.TaskPageDTO value, TaskListMessaging.TaskListExtraDTO extra)
    {
        int pageNumber = value.pageNumber();
        TaskListComponent old = TaskListItem.Component.get(itemStack);
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
        TaskListItem.Component.set(itemStack, updated);
        TaskListItem.Utility.setTitle(itemStack, extra.itemName());
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    public static void handleBlockPagingMessage(BlockPagingMessage message, Supplier<NetworkEvent.Context> contextSupplier)
    {
        try
        {
            contextSupplier.get().enqueueWork(() -> {
                // Work that needs to be threadsafe (most work)
                ServerPlayer player = contextSupplier.get().getSender(); // the client that sent this packet
                // do stuff
                if (player.level().getBlockEntity(message.getPos()) instanceof TaskListBlockEntity tile)
                {
                    if (message.isMovingRight())
                    {
                        tile.onArrowNext();
                    }
                    else
                    {
                        tile.onArrowPrev();
                    }
                }
            });
            contextSupplier.get().setPacketHandled(true);
        }
        catch (Exception e)
        {
            LogUtils.getLogger().error("Networking error in WFHA mod, msg656:  \n" + e.getMessage());
        }
    }

    // ---------------------------------

    public static void handleBlockCheckmarkMessage(BlockCheckmarkMessage message, Supplier<NetworkEvent.Context> contextSupplier)
    {
        try
        {
            contextSupplier.get().enqueueWork(() -> {
                // Work that needs to be threadsafe (most work)
                ServerPlayer player = contextSupplier.get().getSender(); // the client that sent this packet
                // do stuff
                if (player.level().getBlockEntity(message.getPos()) instanceof TaskListBlockEntity tile)
                {
                    tile.updateCheckmark(message.getIndex(), message.getPageNumber());
                }
            });
            contextSupplier.get().setPacketHandled(true);
        }
        catch (Exception e)
        {
            LogUtils.getLogger().error("Networking error in WFHA mod, msg657:  \n" + e.getMessage());
        }
    }
}
