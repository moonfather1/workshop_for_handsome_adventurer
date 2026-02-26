package moonfather.workshop_for_handsome_adventurer.other;


import moonfather.workshop_for_handsome_adventurer.CommonConfig;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.initialization.ContentRegistration;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class UnsupportedWoodRecipe extends CustomRecipe
{
    public UnsupportedWoodRecipe()
    {
        super(CraftingBookCategory.MISC);
    }

    public UnsupportedWoodRecipe(CraftingBookCategory craftingBookCategory)
    {
        super(CraftingBookCategory.MISC);
    }

    @Override
    public boolean matches(CraftingInput craftingInput, Level level)
    {
        if (!CommonConfig.SimpleTableReplacesVanillaTable.get())
        {
            return false;
        }
        if (craftingInput.height() < 2 || craftingInput.width() < 2)
        {
            return false;
        }
        for (int ys = 0; ys <= craftingInput.height() - 2; ys++)
        {
            for (int xs = 0; xs <= craftingInput.width() - 2; xs++)
            {
                boolean ok = true;
                for (int y = 0; y <= craftingInput.height() - 1; y++)
                {
                    for (int x = 0; x <= craftingInput.width() - 1; x++)
                    {
                        boolean isPlank = isAnUnsupportedPlank(craftingInput.getItem(y * craftingInput.width() + x));
                        boolean isEmpty = craftingInput.getItem(y * craftingInput.width() + x).isEmpty();
                        if (x >= xs && x <= xs + 1 && y >= ys && y <= ys + 1)
                        {
                            ok = ok && isPlank;
                        }
                        else
                        {
                            ok = ok && isEmpty;
                        }
                    }
                }
                if (ok)
                {
                    return true;
                }
            }
        }
        return false;
    }



    private final TagKey<Item> supportedPlanks = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MODID, "supported_planks"));

    private boolean isAnUnsupportedPlank(ItemStack item)
    {
        return item.is(ItemTags.PLANKS) && !item.is(supportedPlanks);
    }


    @Override
    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider lookupProvider)
    {
        if (!CommonConfig.SimpleTableReplacesVanillaTable.get())
        {
            return null;
        }
        return new ItemStack(Blocks.CRAFTING_TABLE);
    }

    @Override
    public boolean showNotification() { return false; }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer()
    {
        return ContentRegistration.TABLE_RECIPE.get();
    }
}
