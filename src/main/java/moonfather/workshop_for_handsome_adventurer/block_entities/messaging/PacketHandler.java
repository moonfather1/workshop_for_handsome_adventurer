package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import moonfather.workshop_for_handsome_adventurer.block_entities.DualTableMenu;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketHandler
{
    public static void handleTabChange(TabChangeMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            // Work that needs to be threadsafe (most work)
            ServerPlayer player = contextSupplier.get().getSender(); // the client that sent this packet
            // do stuff
            if (msg.getTab() >= 0 && player.containerMenu instanceof SimpleTableMenu menu)
            {
                menu.changeTabTo(msg.getTab());
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }

    public static void handleGridChange(GridChangeMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            // Work that needs to be threadsafe (most work)
            ServerPlayer player = contextSupplier.get().getSender(); // the client that sent this packet
            // do stuff
            if (player.containerMenu instanceof DualTableMenu menu)
            {
                menu.changeRecipeTargetGridTo(msg.getDestinationGrid());
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
