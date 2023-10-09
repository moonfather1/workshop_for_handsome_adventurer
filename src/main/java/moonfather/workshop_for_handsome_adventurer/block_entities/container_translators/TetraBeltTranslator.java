package moonfather.workshop_for_handsome_adventurer.block_entities.container_translators;

import net.minecraft.world.Container;

public class TetraBeltTranslator extends BaseContainerTranslator implements IExcessSlotManager
{
    private final int tetraRows;
    public TetraBeltTranslator(Container wrapped)
    {
        super(wrapped, wrapped.getContainerSize() / TetraBeltTranslator.GetRowWidth(wrapped) * 9);
        this.tetraRows = wrapped.getContainerSize() / GetRowWidth(wrapped);
    }

    public static int GetRowWidth(Container belt)
    {
        if (belt.getContainerSize() % 8 == 0)
        {
            return 8;
        }
        else if (belt.getContainerSize() % 6 == 0)
        {
            return 6;
        }
        else
        {
            return 4;
        }
    }


    @Override
    protected int translateVisibleToInternalSlot(int slot)
    {
        int row = this.tetraRows - (slot / 9) - 1;    if (row<0) row=0;
        int slotsPerRow = this.internal.getContainerSize() / this.tetraRows; // usually 8
        return row * slotsPerRow + slot % 9;
    }



    @Override
    protected int translateInternalToVisibleSlot(int slot)
    {
        int slotsPerRow = this.internal.getContainerSize() / this.tetraRows; // usually 8
        int row = this.tetraRows - (slot / slotsPerRow) - 1;
        return row * 9 + slot % slotsPerRow;
    }



    @Override
    public boolean isSlotSpecificallyDisabled(int slotIndex)
    {
        int slotsPerRow = this.internal.getContainerSize() / this.tetraRows; // usually 8
        return slotIndex % 9 >= slotsPerRow;
    }
}