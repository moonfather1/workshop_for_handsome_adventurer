package moonfather.workshop_for_handsome_adventurer.block_entities.container_translators;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public abstract class BaseContainerTranslator extends SimpleContainer
{
    public BaseContainerTranslator(Container wrapped, int totalSize)
    {
        super(totalSize);
        this.internal = wrapped;
    }

    public BaseContainerTranslator(Container wrapped)
    {
        super(wrapped.getContainerSize());
        this.internal = wrapped;
    }

    protected abstract int translateVisibleToInternalSlot(int slot);
    protected abstract int translateInternalToVisibleSlot(int slot);

    ///////////////////////////////////////////////////
    ///////////////////////////////////////////////////
    protected final Container internal;

    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack) { return internal.canPlaceItem(this.translateVisibleToInternalSlot(slot), itemStack); }

    @Override
    public ItemStack getItem(int slot) { return internal.getItem(this.translateVisibleToInternalSlot(slot)); }

    @Override
    public ItemStack removeItem(int slot, int count) { return internal.removeItem(this.translateVisibleToInternalSlot(slot), count); }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {	return internal.removeItemNoUpdate(this.translateVisibleToInternalSlot(slot)); }

    @Override
    public void setItem(int slot, ItemStack itemStack) { internal.setItem(this.translateVisibleToInternalSlot(slot), itemStack); }

    @Override
    public boolean isEmpty() { return internal.isEmpty(); }

    @Override
    public void setChanged() { internal.setChanged(); }

    @Override
    public int getMaxStackSize() { return internal.getMaxStackSize(); }
}
