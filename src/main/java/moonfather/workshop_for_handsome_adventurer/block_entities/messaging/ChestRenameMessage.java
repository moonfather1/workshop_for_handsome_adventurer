package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import net.minecraft.network.FriendlyByteBuf;

public class ChestRenameMessage
{
    private String value = "";
    public ChestRenameMessage(String value)
    {
        this.value = value;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(value);
    }

    public static ChestRenameMessage decode(FriendlyByteBuf buffer) {
        ChestRenameMessage result = new ChestRenameMessage("");
        result.value = buffer.readUtf();
        return result;
    }

    public String getValue() {
        return this.value;
    }
}
