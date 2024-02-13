package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TabChangeMessage(int tab) implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MODID, "message_tabchange");

    public TabChangeMessage(final FriendlyByteBuf buffer)
    {
        this(buffer.readInt());
    }

    @Override
    public void write(final FriendlyByteBuf buffer) {
        buffer.writeInt(tab);
    }

    @Override
    public ResourceLocation id()
    {
        return ID;
    }
}
