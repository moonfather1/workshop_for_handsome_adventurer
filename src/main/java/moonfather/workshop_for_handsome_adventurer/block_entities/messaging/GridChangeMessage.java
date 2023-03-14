package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import net.minecraft.network.FriendlyByteBuf;

public class GridChangeMessage
{
    private int value = -1;
    public GridChangeMessage(int value)
    {
        this.value = value;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(value);
    }

    public static GridChangeMessage decode(FriendlyByteBuf buffer) {
        GridChangeMessage result = new GridChangeMessage(-1);
        result.value = buffer.readInt();
        return result;
    }

    public int getDestinationGrid() {
        return this.value;
    }
}
