package moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data;

import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public class TaskListComponentStorage
{
    public static TaskListMessaging.TaskItemDTO readItem(CompoundTag source)
    {
        String status = source.getString("status");
        if (status.equals("")) { status = "e"; }
        return new TaskListMessaging.TaskItemDTO(status, source.getString("line1"), source.getString("line2"));
    }

    public static TaskListMessaging.TaskPageDTO readPage(CompoundTag source)
    {
        int pageNumber = source.getInt("number");
        List<TaskListMessaging.TaskItemDTO> list = new ArrayList<>(TaskListMessaging.ITEMS_PER_PAGE);
        for (int i = 0; i < TaskListMessaging.ITEMS_PER_PAGE; i++)
        {
            list.add(readItem(source.getCompound("item" + i)));
        }
        return new TaskListMessaging.TaskPageDTO(pageNumber, list);
    }

    public static TaskListComponent readComponent(CompoundTag source)
    {
        int pageCount = source.getInt("page_count");
        int lastPage = source.getInt("last_page");
        List<TaskListMessaging.TaskPageDTO> list = new ArrayList<>(TaskListComponent.MAX_PAGE_COUNT);
        for (int i = 0; i < TaskListComponent.MAX_PAGE_COUNT; i++)
        {
            list.add(readPage(source.getCompound("page" + i)));
        }
        return new TaskListComponent(pageCount, lastPage, list, false);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    public static CompoundTag storeItem(TaskListMessaging.TaskItemDTO item)
    {
        CompoundTag result = new CompoundTag();
        result.putString("status", item.status());
        result.putString("line1", item.line1());
        result.putString("line2", item.line2());
        return result;
    }


    public static CompoundTag storePage(TaskListMessaging.TaskPageDTO page)
    {
        CompoundTag result = new CompoundTag();
        result.putInt("number", page.pageNumber());
        for (int i = 0; i < TaskListMessaging.ITEMS_PER_PAGE; i++)
        {
            result.put("item" + i, storeItem(page.items().get(i)));
        }
        return result;
    }

    public static CompoundTag storeComponent(TaskListComponent component)
    {
        CompoundTag result = new CompoundTag();
        result.putInt("page_count", component.getPageCount());
        result.putInt("last_page", component.getLastPageNumber());
        for (int i = 0; i < TaskListComponent.MAX_PAGE_COUNT; i++)
        {
            result.put("page" + i, storePage(component.getAllPages().get(i)));
        }
        return result;
    }
}
