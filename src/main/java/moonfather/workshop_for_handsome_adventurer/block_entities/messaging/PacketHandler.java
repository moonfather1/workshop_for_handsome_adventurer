package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import moonfather.workshop_for_handsome_adventurer.block_entities.DualTableMenu;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class PacketHandler
{
    public static void handleTabChange(TabChangeMessage msg, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            // Work that needs to be threadsafe (most work)
            ServerPlayer player = context.getSender(); // the client that sent this packet
            // do stuff
            if (msg.getTab() >= 0 && player.containerMenu instanceof SimpleTableMenu menu)
            {
                menu.changeTabTo(msg.getTab());
            }
        });
        context.setPacketHandled(true);
    }

    public static void handleGridChange(GridChangeMessage msg, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            // Work that needs to be threadsafe (most work)
            ServerPlayer player = context.getSender(); // the client that sent this packet
            // do stuff
            if (player.containerMenu instanceof DualTableMenu menu)
            {
                menu.changeRecipeTargetGridTo(msg.getDestinationGrid());
            }
        });
        context.setPacketHandled(true);
    }

    public static void handleClientRequest(ClientRequestMessage msg, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            // Work that needs to be threadsafe (most work)
            ServerPlayer player = context.getSender(); // the client that sent this packet
            // do stuff
            if (player.containerMenu instanceof SimpleTableMenu menu)
            {
                if (msg.isRemoteUpdateRequest()) {
                    menu.sendAllDataToRemote();
                    menu.broadcastChanges();
                }
            }
        });
        context.setPacketHandled(true);
    }

    public static void handleChestRename(ChestRenameMessage msg, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            // Work that needs to be threadsafe (most work)
            ServerPlayer player = context.getSender(); // the client that sent this packet
            // do stuff
            if (player.containerMenu instanceof SimpleTableMenu menu)
            {
                menu.renameChest(msg.getValue());
            }
        });
        context.setPacketHandled(true);
    }
}