package moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class TaskListMessageSender
{
    private static final String PROTOCOL_VERSION = "wfha_tl_1";
    private static final SimpleChannel CHANNEL_INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Constants.MODID, "tl"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );



    public static void sendBlockCheckmarkToServer(BlockPos blockPos, int index, int pageNumber)
    {
        BlockCheckmarkMessage message = new BlockCheckmarkMessage(blockPos, index, pageNumber);
        CHANNEL_INSTANCE.sendToServer(message);
        // tells the server that the block was clicked and checkmarks need to update.
    }



    public static void sendBlockClickPageRightToServer(BlockPos blockPos)
    {
        BlockPagingMessage message = new BlockPagingMessage(blockPos, true);
        CHANNEL_INSTANCE.sendToServer(message);
        // tells the server that the block was clicked and page needs to update.
    }



    public static void sendBlockClickPageLeftToServer(BlockPos blockPos)
    {
        BlockPagingMessage message = new BlockPagingMessage(blockPos, false);
        CHANNEL_INSTANCE.sendToServer(message);
        // tells the server that the block was clicked and page needs to update.
    }



    public static void sendTaskPageToServer(TaskListMessaging.TaskPageDTO page, TaskListMessaging.TaskListExtraDTO extra)
    {
        TaskPageMessage message = new TaskPageMessage(page, extra);
        CHANNEL_INSTANCE.sendToServer(message);
        // this on es sent from gui.
    }



    public static void registerMessages()
    {
        CHANNEL_INSTANCE.registerMessage(discriminator++, BlockPagingMessage.class, BlockPagingMessage::encode, BlockPagingMessage::decode, TaskListMessageHandler::handleBlockPagingMessage, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL_INSTANCE.registerMessage(discriminator++, BlockCheckmarkMessage.class, BlockCheckmarkMessage::encode, BlockCheckmarkMessage::decode, TaskListMessageHandler::handleBlockCheckmarkMessage, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL_INSTANCE.registerMessage(discriminator++, TaskPageMessage.class, TaskPageMessage::encode, TaskPageMessage::decode, TaskListMessageHandler::handleItemMessage, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
    private static int discriminator = 139;
}
