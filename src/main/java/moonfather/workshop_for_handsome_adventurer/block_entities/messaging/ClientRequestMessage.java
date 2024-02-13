package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientRequestMessage(int value) implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MODID, "message_request");

    public ClientRequestMessage(final FriendlyByteBuf buffer)
    {
        this(buffer.readInt());
    }

    @Override
    public void write(final FriendlyByteBuf buffer) {
        buffer.writeInt(value);
    }

    @Override
    public ResourceLocation id()
    {
        return ID;
    }

    public static final int REQUEST_REMOTE_UPDATE = 5;
}