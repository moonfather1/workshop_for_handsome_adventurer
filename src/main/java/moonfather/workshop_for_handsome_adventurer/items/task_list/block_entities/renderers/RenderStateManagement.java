package moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities.renderers;

import moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities.TaskListBlockEntity;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListMessaging;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public class RenderStateManagement
{
    public static RenderStateManagement.Page extract(TaskListBlockEntity blockEntity)
    {
        if (blockEntity.getCachedRenderState() != null)
        {
            return blockEntity.getCachedRenderState();
        }
        RenderStateManagement.Page newState = Page.create(blockEntity.getPageForDisplay(), blockEntity.getFooter());
        blockEntity.setCachedRenderState(newState);
        return newState;
    }

    ////////////////////////////

    public static record Page(Item[] items, FormattedCharSequence footer)
    {
        public static Page create(TaskListMessaging.TaskPageDTO page, String footer)
        {
            Item[] items0 = new Item[TaskListMessaging.ITEMS_PER_PAGE];
            for (int i = 0; i < items0.length; i++)
            {
                items0[i] = Item.create(page.items().get(i));
            }
            return new Page(items0, FormattedCharSequence.forward(footer, Style.EMPTY));
        }
    }
    public static record Item(String status, FormattedCharSequence line1, FormattedCharSequence line2)
    {
        public static Item create(String statusRaw, String line1Raw, String line2Raw)
        {
            return new Item(statusRaw, FormattedCharSequence.forward(line1Raw, Style.EMPTY), FormattedCharSequence.forward(line2Raw, Style.EMPTY));
        }
        public static Item create(TaskListMessaging.TaskItemDTO item)
        {
            return new Item(item.status(), FormattedCharSequence.forward(item.line1(), Style.EMPTY), FormattedCharSequence.forward(item.line2(), Style.EMPTY));
        }
    }
}
