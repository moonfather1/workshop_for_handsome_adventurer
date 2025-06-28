package moonfather.workshop_for_handsome_adventurer.items.task_list.items.crafting;

import moonfather.workshop_for_handsome_adventurer.items.task_list.RegistrationForTaskList;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.TaskListItem;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListComponent;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;


public class TaskListPlusPaperRecipe extends CustomRecipe
{
    public TaskListPlusPaperRecipe(ResourceLocation id) {
        super(id, CraftingBookCategory.EQUIPMENT);
    }

    public TaskListPlusPaperRecipe(ResourceLocation id, CraftingBookCategory craftingBookCategory) { super(id, craftingBookCategory); }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level)
    {
        boolean havePaper = false;
        boolean haveClipboard = false;

        for (int i = 0; i < craftingContainer.getItems().size(); ++i)
        {
            ItemStack itemstack = craftingContainer.getItem(i);
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
                if (! TaskListItem.Utility.isOurItem(itemstack))
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

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess)
    {
        int paper = 0;
        ItemStack result = null;
        for (int i = 0; i < craftingContainer.getItems().size(); i++)
        {
            ItemStack current = craftingContainer.getItem(i);
            if (current.is(Items.PAPER))
            {
                paper++;
            }
            else if (TaskListItem.Utility.isOurItem(current))
            {
                result = current.copy();
            }
        }
        if (result != null && paper > 0)
        {
            TaskListComponent c = TaskListItem.Component.get(result);
            if (c == null) { c = new TaskListComponent(1, 1, null); }
            int currentPaperCount = c.getPageCount();
            if (currentPaperCount + paper > TaskListComponent.MAX_PAGE_COUNT)
            {
                return ItemStack.EMPTY;
            }
            c = c.addPages(paper);;
            TaskListItem.Component.set(result, c);
            return result;
        }
        return ItemStack.EMPTY;
    }

    public boolean canCraftInDimensions(int width, int height)
    {
        return width * height >= 2;
    }

    ///////////////////////////////

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return RegistrationForTaskList.TASK_LIST_EXPANSION_RECIPE.get();
    }
}
