package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class PacketSender
{
    public static void sendTabChangeToServer(int newTab)
    {
        TabChangeMessage message = new TabChangeMessage(newTab);
        ClientPacketDistributor.sendToServer(message);
        // tells the server that the tab was clicked and container slots need to update.
    }

    public static void sendDestinationGridChangeToServer(int newDestination)
    {
        GridChangeMessage message = new GridChangeMessage(newDestination);
        ClientPacketDistributor.sendToServer(message);
        // tells the server that the destination grid for jei/rei/emi has changed.
    }

    public static void sendRemoteUpdateRequestToServer()
    {
        ClientRequestMessage message = new ClientRequestMessage(ClientRequestMessage.REQUEST_REMOTE_UPDATE);
        ClientPacketDistributor.sendToServer(message);
        // tells the server that the client needs up-to date values of item slots and data slots.
    }

    public static void sendRenameRequestToServer(String newName)
    {
        ChestRenameMessage message = new ChestRenameMessage(newName);
        ClientPacketDistributor.sendToServer(message);
        // tells the server that the rename button was clicked.
    }

    public static void sendCraftingResultUpdateRequestToServer(int slotIndex)
    {
        CraftingUpdateRequestMessage message = new CraftingUpdateRequestMessage(slotIndex);
        ClientPacketDistributor.sendToServer(message);
        // tells the server to update the crafting result slot because a recipe was chosen in polymorph widget.
    }
}