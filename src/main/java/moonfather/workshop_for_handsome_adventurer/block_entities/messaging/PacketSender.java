package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.*;
import net.minecraftforge.network.packets.LoginWrapper;

import java.util.Optional;

public class PacketSender
{
    private static final String PROTOCOL_VERSION = "wfha2";
    public static final SimpleChannel CHANNEL_INSTANCE = ChannelBuilder
            .named(new ResourceLocation(Constants.MODID, "main"))
            .networkProtocolVersion(2)
            .simpleChannel()

            .messageBuilder(TabChangeMessage.class, NetworkDirection.PLAY_TO_SERVER)
                .decoder(TabChangeMessage::decode)
                .encoder(TabChangeMessage::encode)
                .consumerMainThread(PacketHandler::handleTabChange)
                .add()

            .messageBuilder(GridChangeMessage.class, NetworkDirection.PLAY_TO_SERVER)
                .decoder(GridChangeMessage::decode)
                .encoder(GridChangeMessage::encode)
                .consumerMainThread(PacketHandler::handleGridChange)
                .add()

            .messageBuilder(ChestRenameMessage.class, NetworkDirection.PLAY_TO_SERVER)
                .decoder(ChestRenameMessage::decode)
                .encoder(ChestRenameMessage::encode)
                .consumerMainThread(PacketHandler::handleChestRename)
                .add()

            .messageBuilder(ClientRequestMessage.class, NetworkDirection.PLAY_TO_SERVER)
                .decoder(ClientRequestMessage::decode)
                .encoder(ClientRequestMessage::encode)
                .consumerMainThread(PacketHandler::handleClientRequest)
                .add();



    public static void sendTabChangeToServer(int newTab)
    {
        TabChangeMessage message = new TabChangeMessage(newTab);
        CHANNEL_INSTANCE.send(message, PacketDistributor.SERVER.noArg());
    }

    public static void sendDestinationGridChangeToServer(int newDestination)
    {
        GridChangeMessage message = new GridChangeMessage(newDestination);
        CHANNEL_INSTANCE.send(message, PacketDistributor.SERVER.noArg());
    }

    public static void sendRemoteUpdateRequestToServer()
    {
        ClientRequestMessage message = new ClientRequestMessage(ClientRequestMessage.REQUEST_REMOTE_UPDATE);
        CHANNEL_INSTANCE.send(message, PacketDistributor.SERVER.noArg());
    }

    public static void sendRenameRequestToServer(String newName)
    {
        ChestRenameMessage message = new ChestRenameMessage(newName);
        CHANNEL_INSTANCE.send(message, PacketDistributor.SERVER.noArg());
    }

    public static void registerMessages() {  }
}
