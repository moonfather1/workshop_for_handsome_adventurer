package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import net.minecraft.network.FriendlyByteBuf;

public class TabChangeMessage
{
    private int value = -1;
    public TabChangeMessage(int value)
    {
        this.value = value;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(value);
    }

    public static TabChangeMessage decode(FriendlyByteBuf buffer) {
        TabChangeMessage result = new TabChangeMessage(-1);
        result.value = buffer.readInt();
        return result;
    }

    public int getValue() {
        return this.value;
    }

    public int getTab() {
        return this.value;
    }
}
