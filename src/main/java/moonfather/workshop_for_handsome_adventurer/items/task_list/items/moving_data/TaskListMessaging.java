package moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskListMessaging
{
    public static final int ITEMS_PER_PAGE = 6;

    public static void sendPageToServer(TaskPageDTO page, TaskListExtraDTO extra)
    {
        ClientPacketDistributor.sendToServer(new TaskPageMessage(page, extra));
        // sent to server on task list close or on page change
    }

    public static void sendBlockClickPageRightToServer(BlockPos pos)
    {
        ClientPacketDistributor.sendToServer(new BlockPagingMessage(pos, true));
    }

    public static void sendBlockClickPageLeftToServer(BlockPos pos)
    {
        ClientPacketDistributor.sendToServer(new BlockPagingMessage(pos, false));
    }
    public static void sendBlockClickCheckmarkServer(BlockPos pos, int index, int page)
    {
        ClientPacketDistributor.sendToServer(new BlockCheckmarkMessage(pos, index, page));
    }




    public record TaskListExtraDTO(boolean mainHand, int lastPage, String itemName, BlockPos tilePos, boolean listIsInBlock)
    {
        public TaskListExtraDTO(boolean mainHand, int lastPage, String itemName)
        {
            this(mainHand, lastPage, itemName, BlockPos.ZERO, false);
        }
        public TaskListExtraDTO(BlockPos tilePos, int lastPage, String itemName)
        {
            this(true, lastPage, itemName, tilePos, true);
        }

        public TaskListExtraDTO(TaskListExtraDTO original, int page, String itemName)
        {
            this(original.mainHand(), page, itemName, original.tilePos(), original.listIsInBlock());
        }
    }



    public record TaskPageDTO(int pageNumber, List<TaskItemDTO> items)
    {
        public TaskPageDTO(int pageNumber, List<TaskItemDTO> items)
        {
            this.pageNumber = pageNumber;
            this.items = new ArrayList<>(ITEMS_PER_PAGE);
            for (int k = 0; k < ITEMS_PER_PAGE; k++) { this.items.add(null); }
            for (int i = 0; i < ITEMS_PER_PAGE; i++)
            {
                if (items != null)
                {
                    this.items.set(i, new TaskItemDTO(items.get(i).status, items.get(i).line1, items.get(i).line2));
                }
                else
                {
                    this.items.set(i, TaskItemDTO.empty());
                }
            }
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(items);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof TaskPageDTO other)
            {
                for (int i = 0; i < ITEMS_PER_PAGE; i++) {
                    if (this.items.get(i) == null)
                    {
                        if (other.items.get(i) == null)
                        {
                            continue;
                        }
                        else
                        {
                            return false;
                        }
                    }
                    if (! this.items.get(i).equals(other.items.get(i)))
                    {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }

    public record TaskItemDTO(String status, String line1, String line2)
    {
        @Override
        public int hashCode()
        {
            return Objects.hash(status, line1, line2);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof TaskItemDTO other)
            {
                return this.status.equals(other.status) && this.line1.equals(other.line1) && this.line2.equals(other.line2);
            }
            return false;
        }

        public static TaskItemDTO empty()
        {
            return new TaskItemDTO("e", "", "");
        }
    }

    // -------------------------------- //

    public static final Codec<TaskItemDTO> CODEC_FOR_ITEM = RecordCodecBuilder.create(instance ->
            instance.group(
                    ExtraCodecs.NON_EMPTY_STRING.fieldOf("status").forGetter(TaskItemDTO::status),
                    Codec.STRING.fieldOf("line1").forGetter(TaskItemDTO::line1),
                    Codec.STRING.fieldOf("line2").forGetter(TaskItemDTO::line2)
            ).apply(instance, TaskItemDTO::new)
    );
    public static final StreamCodec<ByteBuf, TaskItemDTO> STREAM_CODEC_FOR_ITEM = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, TaskItemDTO::status,
            ByteBufCodecs.STRING_UTF8, TaskItemDTO::line1,
            ByteBufCodecs.STRING_UTF8, TaskItemDTO::line2,
            TaskItemDTO::new
    );

    // -------------------------------- //

    public static final Codec<TaskPageDTO> CODEC_FOR_PAGE = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("pageNumber").forGetter(TaskPageDTO::pageNumber),
                    Codec.list(CODEC_FOR_ITEM, ITEMS_PER_PAGE, ITEMS_PER_PAGE).fieldOf("value2").forGetter(TaskPageDTO::items)
            ).apply(instance, TaskPageDTO::new)
    );
    public static final StreamCodec<ByteBuf, TaskPageDTO> STREAM_CODEC_FOR_PAGE = StreamCodec.composite(
            ByteBufCodecs.INT, TaskPageDTO::pageNumber,
            STREAM_CODEC_FOR_ITEM.apply(ByteBufCodecs.list(ITEMS_PER_PAGE)), TaskPageDTO::items,
            TaskPageDTO::new
    );
}
