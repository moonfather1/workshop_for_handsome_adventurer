package moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data;

import moonfather.workshop_for_handsome_adventurer.Constants;
import io.netty.handler.codec.DecoderException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListMessaging.ITEMS_PER_PAGE;

public record TaskPageMessage(TaskListMessaging.TaskPageDTO value, TaskListMessaging.TaskListExtraDTO extra) implements CustomPacketPayload
{
    private static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MODID, "message_task_page");
    public static final Type<TaskPageMessage> TYPE = new Type<>(ID);



    @Override
    public @NotNull Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, TaskPageMessage> STREAM_CODEC = StreamCodec.of(
            TaskPageMessage::encode,
            TaskPageMessage::decode
    );



    private static void encode(RegistryFriendlyByteBuf buf, TaskPageMessage msg)
    {
        buf.writeInt(msg.value.pageNumber());
        for (int i = 0; i < ITEMS_PER_PAGE; i++)
        {
            buf.writeUtf(msg.value.items().get(i).status());
            buf.writeUtf(msg.value.items().get(i).line1());
            buf.writeUtf(msg.value.items().get(i).line2());
        }
        buf.writeBoolean(msg.extra.mainHand());
        buf.writeInt(msg.extra.lastPage());
        buf.writeUtf(msg.extra.itemName());
        buf.writeBoolean(msg.extra.listIsInBlock());
        buf.writeBlockPos(msg.extra.tilePos());
    }
    private static @NotNull TaskPageMessage decode(RegistryFriendlyByteBuf buf)
    {
        int pageNumber = buf.readInt();
        List<TaskListMessaging.TaskItemDTO> items = new ArrayList<>(ITEMS_PER_PAGE);
        for (int i = 0; i < ITEMS_PER_PAGE; i++)
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
}
