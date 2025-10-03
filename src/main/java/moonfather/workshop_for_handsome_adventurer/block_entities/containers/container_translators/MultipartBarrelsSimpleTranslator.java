package moonfather.workshop_for_handsome_adventurer.block_entities.containers.container_translators;


import net.neoforged.neoforge.items.IItemHandler;

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
}
