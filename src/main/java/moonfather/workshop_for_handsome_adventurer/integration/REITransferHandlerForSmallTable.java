package moonfather.workshop_for_handsome_adventurer.integration;

import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;

import java.util.ArrayList;
import java.util.List;

public class REITransferHandlerForSmallTable implements SimpleTransferHandler
{
    protected Class<? extends SimpleTableMenu> containerClass = SimpleTableMenu.class;
    private final CategoryIdentifier<?> categoryIdentifier = BuiltinPlugin.CRAFTING;

    @Override
    public ApplicabilityResult checkApplicable(Context context)
    {
        return containerClass.isInstance(context.getMenu())
                && categoryIdentifier.equals(context.getDisplay().getCategoryIdentifier())
                && context.getContainerScreen() != null
                ? ApplicabilityResult.createApplicable() : ApplicabilityResult.createNotApplicable();
    }



    @Override
    public Iterable<SlotAccessor> getInputSlots(Context context)
    {
        List<SlotAccessor> result = new ArrayList<SlotAccessor>(9);
        SimpleTableMenu menu = (SimpleTableMenu) context.getMenu();
        for (int i = 0; i < 9; i++)
        {
            result.add(SlotAccessor.fromSlot(menu.getSlot(SimpleTableMenu.CRAFT_SLOT_START + i)));
        }
        return result;
    }



    @Override
    public Iterable<SlotAccessor> getInventorySlots(Context context)
    {
        SimpleTableMenu menu = (SimpleTableMenu) context.getMenu();
        boolean showingChestInventories = menu.showInventoryAccess();
        List<SlotAccessor> result = new ArrayList<SlotAccessor>(9 * 4 + (showingChestInventories ? 54 : 0));
        for (int i = SimpleTableMenu.INV_SLOT_START; i <= SimpleTableMenu.INV_SLOT_END; i++)
        {
            result.add(SlotAccessor.fromSlot(menu.getSlot(i)));
        }
        if (showingChestInventories)
        {
            for (int i = SimpleTableMenu.ACCESS_SLOT_START; i <= SimpleTableMenu.ACCESS_SLOT_END; i++)
            {
                result.add(SlotAccessor.fromSlot(menu.getSlot(i)));
            }
        }
        for (int i = SimpleTableMenu.HOTBAR_ROW_SLOT_START; i <= SimpleTableMenu.HOTBAR_ROW_SLOT_END; i++)
        {
            result.add(SlotAccessor.fromSlot(menu.getSlot(i)));
        }
        return result;
    }
}
