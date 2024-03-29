package moonfather.workshop_for_handsome_adventurer.integration;

import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import moonfather.workshop_for_handsome_adventurer.block_entities.DualTableMenu;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;

public class JEITransferInfoForDualTable implements IRecipeTransferInfo {
    @Override
    public Class getContainerClass() {
        return DualTableMenu.class;
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeTypes.CRAFTING;
    }

    @Override
    public boolean canHandle(AbstractContainerMenu container, Object recipe) {
        return container instanceof DualTableMenu && recipe instanceof Recipe<?>;
    }

    @Override
    public List<Slot> getRecipeSlots(AbstractContainerMenu container, Object recipe) {
        List<Slot> result = new ArrayList<Slot>(9);
        DualTableMenu menu = (DualTableMenu) container;
        int start = DualTableMenu.CRAFT_SLOT_START;
        if (menu.getRecipeTargetGrid() == 2) {
            start = DualTableMenu.CRAFT_SECONDARY_SLOT_START;
        }
        for (int i = 0; i < 9; i++) {
            result.add(menu.getSlot(start + i));
        }
        return result;
    }

    @Override
    public List<Slot> getInventorySlots(AbstractContainerMenu container, Object recipe) {
        SimpleTableMenu menu = (SimpleTableMenu) container;
        boolean showingChestInventories = menu.showInventoryAccess();
        List<Slot> result = new ArrayList<Slot>(9*4 + (showingChestInventories ? 54 : 0));
        for (int i = SimpleTableMenu.INV_SLOT_START; i <= SimpleTableMenu.INV_SLOT_END; i++) {
            result.add(menu.getSlot(i));
        }
        if (showingChestInventories) {
            for (int i = SimpleTableMenu.ACCESS_SLOT_START; i <= SimpleTableMenu.ACCESS_SLOT_END; i++) {
                result.add(menu.getSlot(i));
            }
        }
        for (int i = SimpleTableMenu.HOTBAR_ROW_SLOT_START; i <= SimpleTableMenu.HOTBAR_ROW_SLOT_END; i++) {
            result.add(menu.getSlot(i));
        }
        return result;
    }

    @Override
    public Class getRecipeClass() {
        return null;
    }

    @Override
    public ResourceLocation getRecipeCategoryUid() {
        return null;
    }
}
