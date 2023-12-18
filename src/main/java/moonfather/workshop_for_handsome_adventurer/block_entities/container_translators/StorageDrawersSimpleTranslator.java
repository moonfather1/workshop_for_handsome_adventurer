package moonfather.workshop_for_handsome_adventurer.block_entities.container_translators;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class StorageDrawersSimpleTranslator extends BaseItemHandlerTranslator
{
    public StorageDrawersSimpleTranslator(IItemHandler wrapped)
    {
        super(wrapped, wrapped.getSlots() - 1);
        /// put 999 in DisabledContainer.getMaxStackSize instead of super. reverted for now due to item dupe issue.
    }

    @Override
    public ItemStack removeItem(int slot, int count)
    {
        int formalStackSize = this.getItem(slot).getMaxStackSize();
        return super.removeItem(slot, Math.min(count, formalStackSize));
    }

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
}
