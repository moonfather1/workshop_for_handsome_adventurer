package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record GridChangeMessage(int destination) implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MODID, "message_gridchange");

    public GridChangeMessage(final FriendlyByteBuf buffer)
    {
        this(buffer.readInt());
    }

    @Override
    public void write(final FriendlyByteBuf buffer) {
        buffer.writeInt(destination);
    }

    @Override
    public ResourceLocation id()
    {
        return ID;
    }
}