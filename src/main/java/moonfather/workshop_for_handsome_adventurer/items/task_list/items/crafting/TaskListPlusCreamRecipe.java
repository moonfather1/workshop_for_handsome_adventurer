package moonfather.workshop_for_handsome_adventurer.items.task_list.items.crafting;

import moonfather.workshop_for_handsome_adventurer.items.task_list.RegistrationForTaskList;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.TaskListItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;


public class TaskListPlusCreamRecipe extends CustomRecipe
{
    public TaskListPlusCreamRecipe(ResourceLocation id) {
        super(id, CraftingBookCategory.EQUIPMENT);
    }

    public TaskListPlusCreamRecipe(ResourceLocation id, CraftingBookCategory craftingBookCategory) { super(id, craftingBookCategory); }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level)
    {
        boolean haveCream = false;
        boolean haveClipboard = false;
        boolean alreadyCreamed = false;

        for (int i = 0; i < craftingContainer.getItems().size(); ++i)
        {
            ItemStack itemStack = craftingContainer.getItem(i);
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
            else if (TaskListItem.Utility.isOurItem(itemStack))
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

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess)
    {
        ItemStack current = null;
        for (int i = 0; i < craftingContainer.getItems().size(); i++)
        {
            current = craftingContainer.getItem(i);
            if (TaskListItem.Utility.isOurItemBasic(current))
            {
                break;
            }
        }
        if (current == null)
        {
            return ItemStack.EMPTY;
        }
        ItemStack result = TaskListItem.Utility.createInstanceFireImmune();
        result.setTag(current.getTag());
        return result;
    }

    public boolean canCraftInDimensions(int width, int height)
    {
        return width * height >= 2;
    }

    ///////////////////////////////

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return RegistrationForTaskList.TASK_LIST_CREAMING_RECIPE.get();
    }
}
