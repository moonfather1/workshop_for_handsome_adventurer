package moonfather.workshop_for_handsome_adventurer.other;


import moonfather.workshop_for_handsome_adventurer.CommonConfig;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class UnsupportedWoodRecipe extends CustomRecipe
{
    public UnsupportedWoodRecipe() { super(CraftingBookCategory.MISC); }
    public UnsupportedWoodRecipe(CraftingBookCategory craftingBookCategory) { super(CraftingBookCategory.MISC); }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level)
    {
        if (! CommonConfig.SimpleTableReplacesVanillaTable.get())
        {
            return false;
        }
        if (craftingContainer.getHeight() < 2 || craftingContainer.getWidth() < 2)
        {
            return false;
        }
        int minx = 15, miny = 16, maxx = -1, maxy = -1;
        for (int ys = 0; ys <= craftingContainer.getHeight() - 2; ys++) {
            for (int xs = 0; xs <= craftingContainer.getWidth() - 2; xs++) {
                boolean ok = true;
                for (int y = 0; y <= craftingContainer.getHeight() - 1; y++) {
                    for (int x = 0; x <= craftingContainer.getWidth() - 1; x++) {
                        boolean isPlank = isAnUnsupportedPlank(craftingContainer.getItem(y * craftingContainer.getWidth() + x));
                        boolean isEmpty = craftingContainer.getItem(y * craftingContainer.getWidth() + x).isEmpty();
                        if (x >= xs && x <= xs+1 && y >= ys && y <= ys+1) {
                            ok = ok && isPlank;
                        }
                        else {
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


    private final TagKey<Item> supportedPlanks = TagKey.create(Registries.ITEM, new ResourceLocation(Constants.MODID, "supported_planks"));
    private boolean isAnUnsupportedPlank(ItemStack item) {
        return item.is(ItemTags.PLANKS) && ! item.is(supportedPlanks);
    }



    @Override
    public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess access)
    {
        if (! CommonConfig.SimpleTableReplacesVanillaTable.get())
        {
            return null;
        }
        return new ItemStack(Blocks.CRAFTING_TABLE);
    }

    @Override
    public boolean canCraftInDimensions(int d1, int d2)
    {
        return d1 >= 2 && d2 >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return Registration.TABLE_RECIPE.get();
    }
}
