package moonfather.workshop_for_handsome_adventurer.block_entities.container_translators;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class BaseItemHandlerWrapper extends SimpleContainer implements IExcessSlotManager
{
    public BaseItemHandlerWrapper(IItemHandler wrapped, int totalSize)
    {
        super(totalSize);
        this.internal = wrapped;
        if (wrapped instanceof IExcessSlotManager aesm)
        {
            this.esm = aesm;
        }
        else
        {
            this.esm = null;
        }
    }
    public BaseItemHandlerWrapper(IItemHandler wrapped)
    {
        this(wrapped, wrapped.getSlots());
    }
    ///////////////////////////////////////////////////
    protected final IItemHandler internal;
    private final IExcessSlotManager esm;

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
            ItemStack old = internal.getStackInSlot(slot); // can't call virt methods here (like getItem, they cause double transl)
            ItemStack resto;
            if (old.isEmpty())
            {
                resto = this.internal.insertItem(slot, itemStack, false);
            }
            else if (itemStack.isEmpty())
            {
                resto = this.internal.extractItem(slot, old.getCount(), false);
            }
            else if (ItemStack.isSameItemSameTags(itemStack, old))
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
    public boolean isSlotSpecificallyDisabled(int slotIndex) { return esm != null && esm.isSlotSpecificallyDisabled(slotIndex); }
}
