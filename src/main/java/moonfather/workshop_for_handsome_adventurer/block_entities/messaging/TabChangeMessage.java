package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import net.minecraft.network.FriendlyByteBuf;

public class TabChangeMessage
{
    private int newTab = -1;
    public TabChangeMessage(int tab)
    {
        this.newTab = tab;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(newTab);
    }

    public static TabChangeMessage decode(FriendlyByteBuf buffer) {
        TabChangeMessage result = new TabChangeMessage(-1);
        result.newTab = buffer.readInt();
        return result;
    }

    public int getTab() {
        return this.newTab;
    }
}
