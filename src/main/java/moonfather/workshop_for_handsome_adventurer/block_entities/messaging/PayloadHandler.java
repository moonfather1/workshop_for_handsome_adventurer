package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import moonfather.workshop_for_handsome_adventurer.block_entities.DualTableMenu;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class PayloadHandler
{
    private PayloadHandler() {}

    public static PayloadHandler getInstance()
    {
        return INSTANCE;
    }
    private static final PayloadHandler INSTANCE = new PayloadHandler();

    public void handleMessage(ChestRenameMessage message, PlayPayloadContext playPayloadContext)
    {
        if (playPayloadContext.player().isEmpty() || ! (playPayloadContext.player().get().containerMenu instanceof SimpleTableMenu))
        {
            return;
        }
        playPayloadContext.workHandler().submitAsync(
                () -> {
                    if (playPayloadContext.player().get().containerMenu instanceof SimpleTableMenu menu)
                    {
                        menu.renameChest(message.value());
                    }
                }
        );
    }

    public void handleMessage(ClientRequestMessage message, PlayPayloadContext playPayloadContext)
    {
        if (playPayloadContext.player().isEmpty() || ! (playPayloadContext.player().get().containerMenu instanceof SimpleTableMenu))
        {
            return;
        }
        playPayloadContext.workHandler().submitAsync(
                () -> {
                    if (playPayloadContext.player().get().containerMenu instanceof SimpleTableMenu menu)
                    {
                        if (message.value() == ClientRequestMessage.REQUEST_REMOTE_UPDATE)
                        {
                            menu.sendAllDataToRemote();
                            menu.broadcastChanges();
                        }
                    }
                }
        );
    }

    public void handleMessage(GridChangeMessage message, PlayPayloadContext playPayloadContext)
    {
        if (playPayloadContext.player().isEmpty() || ! (playPayloadContext.player().get().containerMenu instanceof DualTableMenu))
        {
            return;
        }
        playPayloadContext.workHandler().submitAsync(
                () -> {
                    if (playPayloadContext.player().get().containerMenu instanceof DualTableMenu menu)
                    {
                        menu.changeRecipeTargetGridTo(message.destination());
                    }
                }
        );
    }

    public void handleMessage(TabChangeMessage message, PlayPayloadContext playPayloadContext)
    {
        if (playPayloadContext.player().isEmpty() || ! (playPayloadContext.player().get().containerMenu instanceof SimpleTableMenu))
        {
            return;
        }
        playPayloadContext.workHandler().submitAsync(
            () -> {
                if (playPayloadContext.player().get().containerMenu instanceof SimpleTableMenu menu)
                {
                    menu.changeTabTo(message.tab());
                }
            }
        );
    }
}
