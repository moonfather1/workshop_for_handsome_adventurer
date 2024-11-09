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
    public void setItem(int slot, ItemStack itemStack)
    {
        ItemStack old = this.getItem(slot);
        ItemStack resto;
        if (old.isEmpty())
        {
            resto = this.internal.insertItem(this.translateVisibleToInternalSlot(slot), itemStack, false);
        }
        else if (ItemStack.isSameItemSameComponents(itemStack, old))
        {
            if (itemStack.getCount() > old.getCount())
            {
                itemStack.shrink(old.getCount());
                resto = this.internal.insertItem(this.translateVisibleToInternalSlot(slot), itemStack, false);
            }
            else
            {
                resto = this.internal.extractItem(this.translateVisibleToInternalSlot(slot), old.getCount() - itemStack.getCount(), false);
            }
        }
    }

    @Override
    public int getMaxStackSize()
    {
        return super.getMaxStackSize();
    }

    @Override
    public int getMaxStackSize(ItemStack itemStack)
    {
        for (int i = 0; i < this.internal.getSlots(); i++)
        {
            ItemStack old = this.getItem(i);
            if (ItemStack.isSameItemSameComponents(itemStack, old))
            {
                return this.internal.getSlotLimit(i);
            }
        }
        return super.getMaxStackSize();
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
