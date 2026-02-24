package moonfather.workshop_for_handsome_adventurer.block_entities.containers.container_translators;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BackpackedVersion3Translator extends BaseContainerTranslator implements IExcessSlotManager
{
    public BackpackedVersion3Translator(Container wrapped, int totalSize)
    {
        super(wrapped, totalSize);
    }



    @Override
    protected int translateVisibleToInternalSlot(int slot)
    {
        return slot;
    }



    @Override
    protected int translateInternalToVisibleSlot(int slot)
    {
        return slot;
    }



    @Override
    public boolean isSlotSpecificallyDisabled(int slotIndex)
    {
        return ! this.internal.canPlaceItem(slotIndex, STICK);
    }
    private static final ItemStack STICK = Items.STICK.getDefaultInstance();
}
