package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class PacketSender
{
    private static final String PROTOCOL_VERSION = "wfha1";
    private static final SimpleChannel CHANNEL_INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Constants.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void sendTabChangeToServer(int newTab)
    {
        TabChangeMessage message = new TabChangeMessage(newTab);
        CHANNEL_INSTANCE.sendToServer(message);
    }

    public static void sendDestinationGridChangeToServer(int newDestination)
    {
        GridChangeMessage message = new GridChangeMessage(newDestination);
        CHANNEL_INSTANCE.sendToServer(message);
    }

    public static void registerMessage() {
        CHANNEL_INSTANCE.registerMessage(discriminator++, TabChangeMessage.class, TabChangeMessage::encode, TabChangeMessage::decode, PacketHandler::handleTabChange, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL_INSTANCE.registerMessage(discriminator++, GridChangeMessage.class, GridChangeMessage::encode, GridChangeMessage::decode, PacketHandler::handleGridChange, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
    private static int discriminator = 123;
}
