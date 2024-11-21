package moonfather.workshop_for_handsome_adventurer.block_entities.container_translators;

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
}
