package moonfather.workshop_for_handsome_adventurer.block_entities.containers.container_translators;

import moonfather.workshop_for_handsome_adventurer.block_entities.containers.ResourceHandlerWrapper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

///
///  remaps slot numbers between visible and internal
///
public abstract class BaseResourceHandlerTranslator extends ResourceHandlerWrapper
{
    public BaseResourceHandlerTranslator(ResourceHandler<ItemResource> wrapped)
    {
        super(wrapped);
    }

    public BaseResourceHandlerTranslator(ResourceHandler<ItemResource> wrapped, int usableSlotCount, boolean allowPlaceShulkerBoxes)
    {
        super(wrapped, usableSlotCount, allowPlaceShulkerBoxes);
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
        for (int i = 0; i < internal.size(); i++)
        {
            if (internal.getAmountAsInt(i) > 0) // no translation!
            {
                return false;
            }
        }  return true;
    }

    @Override
    public void setChanged() {  }

    @Override
    public int getMaxStackSize() { return super.getMaxStackSize(); }

    @Override
    public int getMaxStackSize(ItemStack itemStack)
    {
        for (int i = 0; i < this.internal.size(); i++)
        {
            ItemStack old = this.getItem(i);
            if (ItemStack.isSameItemSameComponents(itemStack, old))
            {
                return this.internal.getCapacityAsInt(this.translateVisibleToInternalSlot(i), ItemResource.of(itemStack));
            }
        }
        return this.getMaxStackSize();
    }
}
