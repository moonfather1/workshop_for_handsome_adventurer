package moonfather.workshop_for_handsome_adventurer.block_entities.containers.container_translators;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

///
///  remaps slot numbers between visible and internal
///
public abstract class BaseItemHandlerTranslator extends BaseItemHandlerWrapper
{
    public BaseItemHandlerTranslator(IItemHandler wrapped)
    {
        super(wrapped);
    }

    public BaseItemHandlerTranslator(IItemHandler wrapped, int usableSlotCount)
    {
        super(wrapped, usableSlotCount, false);
    }

    protected abstract int translateVisibleToInternalSlot(int slot);
    protected abstract int translateInternalToVisibleSlot(int slot);

    ///////////////////////////////////////////////////

    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack) { return super.canPlaceItem(this.translateVisibleToInternalSlot(slot), itemStack); }

    @Override
    public ItemStack getItem(int slot) { return super.getItem(this.translateVisibleToInternalSlot(slot)); }

    @Override
    public ItemStack removeItem(int slot, int count) { return super.removeItem(this.translateVisibleToInternalSlot(slot), count); }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {	return super.removeItemNoUpdate(this.translateVisibleToInternalSlot(slot)); }

    @Override
    public void setItem(int slot, ItemStack itemStack) { super.setItem(this.translateVisibleToInternalSlot(slot), itemStack); }

    @Override
    public boolean isEmpty()
    {
        for (int i = 0; i < internal.getSlots(); i++)
        {
            if (! internal.getStackInSlot(i).isEmpty()) // no trans
            {
                return false;
            }
        }  return true;
    }

    @Override
    public void setChanged() {  }

    @Override
    public int getMaxStackSize() { return internal.getSlots() > 1 ? internal.getSlotLimit(1) : 64; }

    @Override
    public int getMaxStackSize(ItemStack itemStack)
    {
        for (int i = 0; i < this.internal.getSlots(); i++)
        {
            ItemStack old = this.getItem(i);
            if (ItemStack.isSameItemSameComponents(itemStack, old))
            {
                return this.internal.getSlotLimit(this.translateVisibleToInternalSlot(i));
            }
        }
        return this.getMaxStackSize();
    }
}
