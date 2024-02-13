package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record ChestRenameMessage(String value) implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MODID, "message_rename");

    public ChestRenameMessage(final FriendlyByteBuf buffer)
    {
        this(Optional.of(buffer.readUtf()).orElse(""));
    }

    @Override
    public void write(final FriendlyByteBuf buffer) {
        buffer.writeUtf(value);
    }

    @Override
    public ResourceLocation id()
    {
        return ID;
    }
}
