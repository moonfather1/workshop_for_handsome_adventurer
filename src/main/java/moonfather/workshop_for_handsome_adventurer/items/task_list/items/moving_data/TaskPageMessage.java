package moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data;

import io.netty.handler.codec.DecoderException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;


public class TaskPageMessage
{
    private final TaskListMessaging.TaskPageDTO value;
    private final TaskListMessaging.TaskListExtraDTO extra;

    public TaskPageMessage(TaskListMessaging.TaskPageDTO page, TaskListMessaging.TaskListExtraDTO extra)
    {
        this.value = page;
        this.extra = extra;
    }


    public void encode(FriendlyByteBuf buf)
    {
        buf.writeInt(this.value.pageNumber());
        for (int i = 0; i < TaskListMessaging.ITEMS_PER_PAGE; i++)
        {
            buf.writeUtf(this.value.items().get(i).status());
            buf.writeUtf(this.value.items().get(i).line1());
            buf.writeUtf(this.value.items().get(i).line2());
        }
        buf.writeBoolean(this.extra.mainHand());
        buf.writeInt(this.extra.lastPage());
        buf.writeUtf(this.extra.itemName());
        buf.writeBoolean(this.extra.listIsInBlock());
        buf.writeBlockPos(this.extra.tilePos());
    }
    public static TaskPageMessage decode(FriendlyByteBuf buf)
    {
        int pageNumber = buf.readInt();
        List<TaskListMessaging.TaskItemDTO> items = new ArrayList<>(TaskListMessaging.ITEMS_PER_PAGE);
        for (int i = 0; i < TaskListMessaging.ITEMS_PER_PAGE; i++)
        {
            String s = silentRead(buf);
            String l1 = silentRead(buf);
            String l2 = silentRead(buf);
            items.add(new TaskListMessaging.TaskItemDTO(s, l1, l2));
        }
        TaskListMessaging.TaskPageDTO page = new TaskListMessaging.TaskPageDTO(pageNumber, items);
        boolean mainHand = buf.readBoolean();
        int lastPage = buf.readInt();
        String itemName = buf.readUtf();
        boolean inBlock = buf.readBoolean();
        BlockPos pos = buf.readBlockPos();
        TaskListMessaging.TaskListExtraDTO extra = new TaskListMessaging.TaskListExtraDTO(mainHand, lastPage, itemName, pos, inBlock);
        return new TaskPageMessage(page, extra);
    }
    private static String silentRead(FriendlyByteBuf buf)
    {
        try
        {
            return buf.readUtf();
        }
        catch (DecoderException e)
        {
            return "???";
        }
    }



    public TaskListMessaging.TaskListExtraDTO getExtra() { return this.extra; }
    public TaskListMessaging.TaskPageDTO getPage() { return this.value; }
}
