package moonfather.workshop_for_handsome_adventurer.items.task_list.items.crafting;

import moonfather.workshop_for_handsome_adventurer.items.task_list.RegistrationForTaskList;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.TaskListItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;


public class TaskListPlusCreamRecipe extends CustomRecipe
{
    public TaskListPlusCreamRecipe() {
        super(CraftingBookCategory.EQUIPMENT);
    }

    public TaskListPlusCreamRecipe(CraftingBookCategory craftingBookCategory) { super(craftingBookCategory); }

    public boolean matches(CraftingInput input, @NotNull Level level)
    {
        boolean haveCream = false;
        boolean haveClipboard = false;
        boolean alreadyCreamed = false;

        for (int i = 0; i < input.size(); ++i)
        {
            ItemStack itemStack = input.getItem(i);
            if (itemStack.isEmpty())
            {
                // continue;
            }
            else if (itemStack.is(Items.MAGMA_CREAM))
            {
                if (haveCream)
                {
                    return false; // more than 1
                }
                haveCream = true;
            }
            else if (itemStack.is(TaskListItem.Utility.ourItem()))
            {
                if (haveClipboard)
                {
                    return false; // more than 1
                }
                haveClipboard = true;
                alreadyCreamed = TaskListItem.Utility.isFireImmune(itemStack);
            }
            else
            {
                return false; // unknown ingredient
            }
        }
        return haveClipboard && haveCream && ! alreadyCreamed;
    }

    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries)
    {
        ItemStack result = null;
        for (int i = 0; i < input.size(); i++)
        {
            ItemStack current = input.getItem(i);
            if (current.is(TaskListItem.Utility.ourItem()))
            {
                result = current.copy();
                break;
            }
        }
        if (result == null)
        {
            return ItemStack.EMPTY;
        }
        TaskListItem.Utility.setFireImmune(result);
        return result;
    }

    ///////////////////////////////

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer()
    {
        return RegistrationForTaskList.TASK_LIST_CREAMING_RECIPE.get();
    }
}
