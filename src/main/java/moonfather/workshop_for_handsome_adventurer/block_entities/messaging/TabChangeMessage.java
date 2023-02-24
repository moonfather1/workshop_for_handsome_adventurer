package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class TabChangeMessage
{
    private int newTab = -1;
    private UUID player = null;
    public TabChangeMessage(int tab, UUID clientPlayer)
    {
        this.newTab = tab;
        this.player = clientPlayer;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(newTab);
        buffer.writeUUID(player);
    }

    public static TabChangeMessage decode(FriendlyByteBuf buffer) {
        TabChangeMessage result = new TabChangeMessage(-1, null);
        result.newTab = buffer.readInt();
        result.player = buffer.readUUID();
        return result;
    }

    public int getTab() {
        return this.newTab;
    }
}
