package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class MessagingInitialization
{
    public static void register(final RegisterPayloadHandlerEvent event)
    {
        final IPayloadRegistrar registrar = event.registrar(Constants.MODID);
        registrar.play(ChestRenameMessage.ID, ChestRenameMessage::new, handler -> handler.server(PayloadHandler.getInstance()::handleMessage));
        registrar.play(ClientRequestMessage.ID, ClientRequestMessage::new, handler -> handler.server(PayloadHandler.getInstance()::handleMessage));
        registrar.play(GridChangeMessage.ID, GridChangeMessage::new, handler -> handler.server(PayloadHandler.getInstance()::handleMessage));
        registrar.play(TabChangeMessage.ID, TabChangeMessage::new, handler -> handler.server(PayloadHandler.getInstance()::handleMessage));
    }
}
