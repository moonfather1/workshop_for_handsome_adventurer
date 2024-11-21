package moonfather.workshop_for_handsome_adventurer.block_entities.container_translators;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public abstract class BaseItemHandlerTranslator extends SimpleContainer
{
    public BaseItemHandlerTranslator(IItemHandler wrapped, int totalSize)
    {
        super(totalSize);
        this.internal = wrapped;
    }

    public BaseItemHandlerTranslator(IItemHandler wrapped)
    {
        super(wrapped.getSlots());
        this.internal = wrapped;
    }

    protected abstract int translateVisibleToInternalSlot(int slot);
    protected abstract int translateInternalToVisibleSlot(int slot);

    ///////////////////////////////////////////////////
    ///////////////////////////////////////////////////
    protected final IItemHandler internal;

    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack) { return internal.isItemValid(this.translateVisibleToInternalSlot(slot), itemStack); }

    @Override
    public ItemStack getItem(int slot) { return internal.getStackInSlot(this.translateVisibleToInternalSlot(slot)); }

    @Override
    public ItemStack removeItem(int slot, int count)
    {
        int formalStackSize = this.getItem(slot).getMaxStackSize(); //todo zashto
        count =  Math.min(count, formalStackSize);  //todo zashto

        return internal.extractItem(this.translateVisibleToInternalSlot(slot), count, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {	return internal.extractItem(this.translateVisibleToInternalSlot(slot), 5555555, false); }

    @Override
    public void setItem(int slot, ItemStack itemStack)
    {
        if (internal instanceof IItemHandlerModifiable i2)
        {
            i2.setStackInSlot(this.translateVisibleToInternalSlot(slot), itemStack);  // todo UNTESTED!
        }
        else
        {
            ItemStack old = this.getItem(slot);
            ItemStack resto;
            if (old.isEmpty())
            {
                resto = this.internal.insertItem(this.translateVisibleToInternalSlot(slot), itemStack, false);
            }
            else if (itemStack.isEmpty())
            {
                resto = this.internal.extractItem(this.translateVisibleToInternalSlot(slot), old.getCount(), false);
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
    }

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
