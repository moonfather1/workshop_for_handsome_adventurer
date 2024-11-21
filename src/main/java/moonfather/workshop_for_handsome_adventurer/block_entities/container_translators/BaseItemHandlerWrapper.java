package moonfather.workshop_for_handsome_adventurer.block_entities.container_translators;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

///
/// turns item handler into a container. from horse to donkey.
///
public abstract class BaseItemHandlerWrapper extends SimpleContainer
{
    public BaseItemHandlerWrapper(IItemHandler wrapped, int totalSize)
    {
        super(totalSize);
        this.internal = wrapped;
    }

    public BaseItemHandlerWrapper(IItemHandler wrapped)
    {
        super(wrapped.getSlots());
        this.internal = wrapped;
    }

    ///////////////////////////////////////////////////

    protected final IItemHandler internal;

    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack) { return internal.isItemValid(slot, itemStack); }

    @Override
    public ItemStack getItem(int slot) { return internal.getStackInSlot(slot); }

    @Override
    public ItemStack removeItem(int slot, int count)
    {
        int formalStackSize = this.getItem(slot).getMaxStackSize();
        count =  Math.min(count, formalStackSize); // hmpf
        return internal.extractItem(slot, count, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {	return internal.extractItem(slot, 5555555, false); }

    @Override
    public void setItem(int slot, ItemStack itemStack)
    {
        if (internal instanceof IItemHandlerModifiable i2)
        {
            i2.setStackInSlot(slot, itemStack);  // todo UNTESTED!
        }
        else
        {
            ItemStack old = this.getItem(slot);
            ItemStack resto;
            if (old.isEmpty())
            {
                resto = this.internal.insertItem(slot, itemStack, false);
            }
            else if (itemStack.isEmpty())
            {
                resto = this.internal.extractItem(slot, old.getCount(), false);
            }
            else if (ItemStack.isSameItemSameComponents(itemStack, old))
            {
                if (itemStack.getCount() > old.getCount())
                {
                    itemStack.shrink(old.getCount());
                    resto = this.internal.insertItem(slot, itemStack, false);
                }
                else
                {
                    resto = this.internal.extractItem(slot, old.getCount() - itemStack.getCount(), false);
                }
            }
        }
    }

    @Override
    public boolean isEmpty()
    {
        for (int i = 0; i < internal.getSlots(); i++)
        {
            if (! internal.getStackInSlot(i).isEmpty()) // no translation
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
                return this.internal.getSlotLimit(i);
            }
        }
        return this.getMaxStackSize();
    }
}
