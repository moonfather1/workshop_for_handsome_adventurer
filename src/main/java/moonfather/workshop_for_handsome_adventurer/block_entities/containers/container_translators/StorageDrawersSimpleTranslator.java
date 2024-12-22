package moonfather.workshop_for_handsome_adventurer.block_entities.containers.container_translators;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class StorageDrawersSimpleTranslator extends BaseItemHandlerTranslator
{
    public StorageDrawersSimpleTranslator(IItemHandler wrapped)
    {
        super(wrapped, wrapped.getSlots() - 1);
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

    ////////////////////////////////////////////////

    // this prevents completely replacing an item.
    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack)
    {
        ItemStack old = this.getItem(slot);
        if (old.isEmpty() || itemStack.isEmpty())
        {
            return true;
        }
        if (! ItemStack.isSameItemSameComponents(itemStack, old))
        {
            return false;  // prevents replacing through our table.
        }
        return super.canPlaceItem(slot, itemStack);
    }
}
