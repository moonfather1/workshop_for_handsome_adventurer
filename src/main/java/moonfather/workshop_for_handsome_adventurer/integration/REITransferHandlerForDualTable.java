package moonfather.workshop_for_handsome_adventurer.integration;

import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor;
import moonfather.workshop_for_handsome_adventurer.block_entities.DualTableMenu;

import java.util.ArrayList;
import java.util.List;

public class REITransferHandlerForDualTable extends REITransferHandlerForSmallTable
{
    public REITransferHandlerForDualTable()
    {
        this.containerClass = DualTableMenu.class;
    }



    @Override
    public Iterable<SlotAccessor> getInputSlots(Context context)
    {
        List<SlotAccessor> result = new ArrayList<SlotAccessor>(9);
        DualTableMenu menu = (DualTableMenu) context.getMenu();
        int start = DualTableMenu.CRAFT_SLOT_START;
        if (menu.getRecipeTargetGrid() == 2)
        {
            start = DualTableMenu.CRAFT_SECONDARY_SLOT_START;
        }
        for (int i = 0; i < 9; i++)
        {
            result.add(SlotAccessor.fromSlot(menu.getSlot(start + i)));
        }
        return result;
    }
}
