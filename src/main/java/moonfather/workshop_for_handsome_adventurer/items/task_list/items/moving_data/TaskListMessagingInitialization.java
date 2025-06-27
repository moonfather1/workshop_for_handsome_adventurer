package moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class TaskListMessagingInitialization
{
    public static void register(final RegisterPayloadHandlersEvent event)
    {
        final PayloadRegistrar registrar = event.registrar(Constants.MODID + "_tl_v1");
        registrar.playToServer(TaskPageMessage.TYPE, TaskPageMessage.STREAM_CODEC, TaskListMessageHandler::handleItemMessage);
        registrar.playToServer(BlockPagingMessage.TYPE, BlockPagingMessage.STREAM_CODEC, TaskListMessageHandler::handleBlockPagingMessage);
        registrar.playToServer(BlockCheckmarkMessage.TYPE, BlockCheckmarkMessage.STREAM_CODEC, TaskListMessageHandler::handleBlockCheckmarkMessage);
    }
}
