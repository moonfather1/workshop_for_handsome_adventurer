package moonfather.workshop_for_handsome_adventurer.block_entities.messaging;

import net.minecraft.network.FriendlyByteBuf;

public class ClientRequestMessage
{
    private int value = -1;
    public ClientRequestMessage(int value)
    {
        this.value = value;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(value);
    }

    public static ClientRequestMessage decode(FriendlyByteBuf buffer) {
        ClientRequestMessage result = new ClientRequestMessage(-1);
        result.value = buffer.readInt();
        return result;
    }

    public int getValue() {
        return this.value;
    }

    public boolean isRemoteUpdateRequest() {
        return this.value == REQUEST_REMOTE_UPDATE;
    }
    public static final int REQUEST_REMOTE_UPDATE = 5;
}
