package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.PlayNetworkDirection;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public class PacketSender
{
    private static final String PROTOCOL_VERSION = "wfha2";
    public static final SimpleChannel CHANNEL_INSTANCE;

    static // constructor
    {
        CHANNEL_INSTANCE = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Constants.MODID, "main"))
                .networkProtocolVersion(()->PROTOCOL_VERSION)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .simpleChannel();
        int messageIndexInCodec = 0;

        CHANNEL_INSTANCE.messageBuilder(TabChangeMessage.class, messageIndexInCodec++, PlayNetworkDirection.PLAY_TO_SERVER)
                        .decoder(TabChangeMessage::decode)
                        .encoder(TabChangeMessage::encode)
                        .consumerMainThread(PacketHandler::handleTabChange)
                        .add();

        CHANNEL_INSTANCE.messageBuilder(GridChangeMessage.class, messageIndexInCodec++, PlayNetworkDirection.PLAY_TO_SERVER)
                        .decoder(GridChangeMessage::decode)
                        .encoder(GridChangeMessage::encode)
                        .consumerMainThread(PacketHandler::handleGridChange)
                        .add();

        CHANNEL_INSTANCE.messageBuilder(ChestRenameMessage.class, messageIndexInCodec++, PlayNetworkDirection.PLAY_TO_SERVER)
                        .decoder(ChestRenameMessage::decode)
                        .encoder(ChestRenameMessage::encode)
                        .consumerMainThread(PacketHandler::handleChestRename)
                        .add();

        CHANNEL_INSTANCE.messageBuilder(ClientRequestMessage.class, messageIndexInCodec++, PlayNetworkDirection.PLAY_TO_SERVER)
                        .decoder(ClientRequestMessage::decode)
                        .encoder(ClientRequestMessage::encode)
                        .consumerMainThread(PacketHandler::handleClientRequest)
                        .add();
    }

    public static void sendTabChangeToServer(int newTab)
    {
        TabChangeMessage message = new TabChangeMessage(newTab);
        CHANNEL_INSTANCE.send(PacketDistributor.SERVER.noArg(), message);
    }

    public static void sendDestinationGridChangeToServer(int newDestination)
    {
        GridChangeMessage message = new GridChangeMessage(newDestination);
        CHANNEL_INSTANCE.send(PacketDistributor.SERVER.noArg(), message);
    }

    public static void sendRemoteUpdateRequestToServer()
    {
        ClientRequestMessage message = new ClientRequestMessage(ClientRequestMessage.REQUEST_REMOTE_UPDATE);
        CHANNEL_INSTANCE.send(PacketDistributor.SERVER.noArg(), message);
    }

    public static void sendRenameRequestToServer(String newName)
    {
        ChestRenameMessage message = new ChestRenameMessage(newName);
        CHANNEL_INSTANCE.send(PacketDistributor.SERVER.noArg(), message);
    }

    public static void registerMessages() {  }
}