package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import moonfather.workshop_for_handsome_adventurer.block_entities.DualTableMenu;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PayloadHandler
{
    private PayloadHandler() {}

    public static void handleMessage(final ChestRenameMessage message, final IPayloadContext context)
    {
        // as of 1.20.6, things are by default handled on main thread
        try
        {
            if (context.player() instanceof ServerPlayer sp         // the client that sent this packet
                && sp.containerMenu instanceof SimpleTableMenu menu)
            {
                menu.renameChest(message.value());
            }
        }
        catch (Exception e)
        {
            context.disconnect(Component.literal("Networking error in WFHA mod, msg1:  \n" + e.getMessage()));
        }
    }

    public static void handleMessage(final ClientRequestMessage message, final IPayloadContext context)
    {
        // as of 1.20.6, things are by default handled on main thread
        try
        {
            if (context.player() instanceof ServerPlayer sp         // the client that sent this packet
                    && sp.containerMenu instanceof SimpleTableMenu menu)
            {
                if (message.value() == ClientRequestMessage.REQUEST_REMOTE_UPDATE)
                {
                    menu.sendAllDataToRemote();
                    menu.broadcastChanges();
                }
            }
        }
        catch (Exception e)
        {
            context.disconnect(Component.literal("Networking error in WFHA mod, msg2:  \n" + e.getMessage()));
        }
    }

    public static void handleMessage(final GridChangeMessage message, final IPayloadContext context)
    {
        try
        {
            if (context.player() instanceof ServerPlayer sp         // the client that sent this packet
                    && sp.containerMenu instanceof DualTableMenu menu)
            {
                menu.changeRecipeTargetGridTo(message.destination());
            }
        }
        catch (Exception e)
        {
            context.disconnect(Component.literal("Networking error in WFHA mod, msg3:  \n" + e.getMessage()));
        }
    }

    public static void handleMessage(final TabChangeMessage message, final IPayloadContext context)
    {
        try
        {
            if (context.player() instanceof ServerPlayer sp         // the client that sent this packet
                    && sp.containerMenu instanceof SimpleTableMenu menu)
            {
                menu.changeTabTo(message.tab());
            }
        }
        catch (Exception e)
        {
            context.disconnect(Component.literal("Networking error in WFHA mod, msg4:  \n" + e.getMessage()));
        }
    }

    public static void handleMessage(final CraftingUpdateRequestMessage message, final IPayloadContext context)
    {
        try
        {
            if (context.player() instanceof ServerPlayer sp         // the client that sent this packet
                    && sp.containerMenu instanceof SimpleTableMenu menu)
            {
                menu.handleCraftingUpdateRequest(message.value());
            }
        }
        catch (Exception e)
        {
            context.disconnect(Component.literal("Networking error in WFHA mod, msg5:  \n" + e.getMessage()));
        }
    }
}
