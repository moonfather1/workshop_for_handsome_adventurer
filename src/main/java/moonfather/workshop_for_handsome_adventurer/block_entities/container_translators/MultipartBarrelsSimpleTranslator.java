package moonfather.workshop_for_handsome_adventurer.block_entities.container_translators;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class MultipartBarrelsSimpleTranslator extends BaseItemHandlerTranslator implements IExcessSlotManager
{
    public MultipartBarrelsSimpleTranslator(IItemHandler wrapped)
    {
        super(wrapped, wrapped.getSlots() - 1);
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
        return slot;
    }

    @Override
    protected int translateInternalToVisibleSlot(int slot)
    {
        return slot;
    }

    @Override
    public boolean isSlotSpecificallyDisabled(int slotIndex)
    {
        return false;
    }

    ////////////////////////////

//    // this prevents completely replacing an item.
//    // problem fixed in BaseItemHandlerWrapper, but we'll prevent that still.
//    @Override
//    public boolean canPlaceItem(int slot, ItemStack itemStack)
//    {
//        ItemStack old = this.getItem(slot);
//        if (old.isEmpty() || itemStack.isEmpty())
//        {
//            return true;
//        }
//        if (! ItemStack.isSameItemSameTags(itemStack, old))
//        {
//            return false;  // prevents replacing through our table.
//        }
//        return super.canPlaceItem(slot, itemStack);
//    }
}
