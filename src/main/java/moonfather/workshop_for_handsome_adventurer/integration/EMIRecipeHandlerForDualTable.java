package moonfather.workshop_for_handsome_adventurer.integration;

import com.google.common.collect.Lists;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import moonfather.workshop_for_handsome_adventurer.block_entities.DualTableMenu;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public class EMIRecipeHandlerForDualTable implements StandardRecipeHandler<DualTableMenu>
{
    @Override
    public List<net.minecraft.world.inventory.Slot> getInputSources(DualTableMenu menu)
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
    public List<net.minecraft.world.inventory.Slot> getCraftingSlots(DualTableMenu menu)
    {
        List<Slot> list = Lists.newArrayList();
        int start = DualTableMenu.CRAFT_SLOT_START;
        if (menu.getRecipeTargetGrid() == 2)
        {
            start = DualTableMenu.CRAFT_SECONDARY_SLOT_START;
        }
        for (int i = 0; i < 9; i++)
        {
            list.add(menu.getSlot(start + i));
        }
        return list;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe)
    {
        return recipe.getCategory() == VanillaEmiRecipeCategories.CRAFTING;
    }
}
