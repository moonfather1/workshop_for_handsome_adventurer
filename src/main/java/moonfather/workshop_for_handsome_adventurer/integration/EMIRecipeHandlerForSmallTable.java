package moonfather.workshop_for_handsome_adventurer.integration;

import com.google.common.collect.Lists;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public class EMIRecipeHandlerForSmallTable implements StandardRecipeHandler<SimpleTableMenu>
{
    @Override
    public List<Slot> getInputSources(SimpleTableMenu menu)
    {
        boolean showingChestInventories = menu.showInventoryAccess();
        List<Slot> result = new ArrayList<Slot>(9 * 4 + (showingChestInventories ? 54 : 0));
        for (int i = SimpleTableMenu.INV_SLOT_START; i <= SimpleTableMenu.INV_SLOT_END; i++)
        {
            result.add(menu.getSlot(i));
        }
        if (showingChestInventories)
        {
            for (int i = SimpleTableMenu.ACCESS_SLOT_START; i <= SimpleTableMenu.ACCESS_SLOT_END; i++)
            {
                result.add(menu.getSlot(i));
            }
        }
        for (int i = SimpleTableMenu.HOTBAR_ROW_SLOT_START; i <= SimpleTableMenu.HOTBAR_ROW_SLOT_END; i++)
        {
            result.add(menu.getSlot(i));
        }
        return result;
    }

    @Override
    public List<Slot> getCraftingSlots(SimpleTableMenu menu)
    {
        List<Slot> list = Lists.newArrayList();
        for (int i = SimpleTableMenu.CRAFT_SLOT_START; i <= SimpleTableMenu.CRAFT_SLOT_END; ++i)
        {
            list.add(menu.getSlot(i));
        }
        return list;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe)
    {
        return recipe.getCategory() == VanillaEmiRecipeCategories.CRAFTING;
    }
}
