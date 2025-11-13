package moonfather.workshop_for_handsome_adventurer.items.task_list.items.crafting;

import moonfather.workshop_for_handsome_adventurer.items.task_list.RegistrationForTaskList;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.TaskListItem;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListComponent;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;


public class TaskListPlusPaperRecipe extends CustomRecipe
{
    public TaskListPlusPaperRecipe() {
        super(CraftingBookCategory.EQUIPMENT);
    }

    public TaskListPlusPaperRecipe(CraftingBookCategory craftingBookCategory) { super(craftingBookCategory); }

    public boolean matches(CraftingInput input, Level level)
    {
        boolean havePaper = false;
        boolean haveClipboard = false;

        for (int i = 0; i < input.size(); ++i)
        {
            ItemStack itemstack = input.getItem(i);
            if (itemstack.isEmpty())
            {
                // continue;
            }
            else if (itemstack.is(Items.PAPER))
            {
                havePaper = true;
            }
            else
            {
                if (! itemstack.is(TaskListItem.Utility.ourItem()))
                {
                    return false; // unknown ingredient
                }
                if (haveClipboard)
                {
                    return false; // more than 1
                }
                haveClipboard = true;
            }
        }
        return haveClipboard && havePaper;
    }

    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries)
    {
        int paper = 0;
        ItemStack result = null;
        for (int i = 0; i < input.size(); i++)
        {
            ItemStack current = input.getItem(i);
            if (current.is(Items.PAPER))
            {
                paper++;
            }
            else if (current.is(TaskListItem.Utility.ourItem()))
            {
                result = current.copy();
            }
        }
        if (result != null && paper > 0)
        {
            TaskListComponent old = result.get(TaskListItem.Utility.ourComponent());
            int currentPaperCount = old != null ? old.getPageCount() : 1;
            if (currentPaperCount + paper > TaskListComponent.MAX_PAGE_COUNT)
            {
                return ItemStack.EMPTY;
            }
            int finalPaper = paper;
            result.update(TaskListItem.Utility.ourComponent(), new TaskListComponent(1, 1, null), c -> c.addPages(finalPaper));
            return result;
        }
        return ItemStack.EMPTY;
    }

    ///////////////////////////////

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer()
    {
        return RegistrationForTaskList.TASK_LIST_EXPANSION_RECIPE.get();
    }
}
