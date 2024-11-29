package moonfather.workshop_for_handsome_adventurer.block_entities.container_translators;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class StorageDrawersSimpleTranslator extends BaseItemHandlerTranslator implements IExcessSlotManager
{
    public StorageDrawersSimpleTranslator(IItemHandler wrapped)
    {
        super(wrapped, wrapped.getSlots() - 1);
        /// put 999 in DisabledContainer.getMaxStackSize instead of super. reverted for now due to item dupe issue.
    }

    @Override
    public int getMaxStackSize()
    {
        return super.getMaxStackSize();
    }

    ////////////////////////

    @Override
    protected int translateVisibleToInternalSlot(int slot)
    {
        return slot + 1;
    }

    @Override
    protected int translateInternalToVisibleSlot(int slot)
    {
        return slot - 1;
    }

    @Override
    public boolean isSlotSpecificallyDisabled(int slotIndex)
    {
        return false;
    }
}
