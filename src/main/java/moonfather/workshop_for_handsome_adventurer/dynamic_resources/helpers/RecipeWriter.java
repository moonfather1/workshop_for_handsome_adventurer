package moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.List;
import java.util.Map;

public class RecipeWriter
{
    public static void writeFiles(Map<ResourceLocation, String> cache)
    {
        final String SPRUCE = "spruce";
        for (String file: allRecipes)
        {
            String original = AssetReader.getInstance(PackType.SERVER_DATA, Constants.MODID).getText(new ResourceLocation(Constants.MODID, file));
            // it never will be null, won't even check
            String filePrefix = file.substring("recipe/".length()+1, file.length() - "spruce.json".length()); // strip  recipe/    and     spruce.json
            for (String wood: WoodTypeLister.getWoodIds())
            {
                StringBuilder newRecipe = new StringBuilder(original);
                replace(newRecipe, "minecraft:stripped_spruce_log", getStrippedLog(wood));
                replace(newRecipe, "minecraft:spruce_slab", getSlab(wood));
                replace(newRecipe, "minecraft:spruce_planks", getPlanks(wood));
                replace(newRecipe, filePrefix+SPRUCE, filePrefix+wood);  // now we need another  .replace(SPRUCE, wood);  to take care of recipe result but vampirism's cursed spruce throws a wrench into that
                conversionNames.forEach(n -> replace(newRecipe, n, n.replace("spruce", wood)) );
                cache.put(new ResourceLocation(Constants.MODID, file.replace(SPRUCE, wood)), newRecipe.toString());
            }
            if (! conversionRecipes.contains(file))
            {
                for (ResourceLocation duplicate : WoodTypeLister.getDuplicateWoods()) // once again, with feeling
                {
                    if (! CustomTripletSupport.isSpecial(duplicate.getPath()))
                    {
                        StringBuilder newRecipe = new StringBuilder(original);
                        replace(newRecipe, "minecraft:stripped_spruce", duplicate.getNamespace() + ":stripped_" + duplicate.getPath()); // these will have logs
                        replace(newRecipe, "minecraft:spruce", duplicate.toString());
                        replace(newRecipe, filePrefix + SPRUCE, filePrefix + duplicate.getPath());
                        conversionNames.forEach(n -> replace(newRecipe, n, n.replace("spruce", duplicate.getPath())));
                        cache.put(new ResourceLocation(Constants.MODID, file.replace(SPRUCE, duplicate.getPath() + "_" + duplicate.getNamespace())), newRecipe.toString());
                    }
                    else
                    {
                        DynamicAssetConfig.WoodSet wood = DynamicAssetConfig.getWoodSet(duplicate.getPath());
                        StringBuilder newRecipe = new StringBuilder(original);
                        replace(newRecipe, "minecraft:stripped_spruce_log", wood.modId() + ":" + wood.log()); // these will have logs
                        replace(newRecipe, "minecraft:spruce_planks", wood.modId() + ":" + wood.planks());
                        replace(newRecipe, "minecraft:spruce_slab", wood.modId() + ":" + wood.slab());
                        replace(newRecipe, filePrefix + SPRUCE, filePrefix + CustomTripletSupport.stripPrefix(wood.woodId()));
                        conversionNames.forEach(n -> replace(newRecipe, n, n.replace("spruce", CustomTripletSupport.stripPrefix(wood.woodId()))));
                        cache.put(new ResourceLocation(Constants.MODID, file.replace(SPRUCE, wood.woodId() + "_" + wood.modId())), newRecipe.toString());
                    }
                }
            }
        }
    }
    private static void replace(StringBuilder sb, String toFind, String replacement)
    {
        int pos = sb.indexOf(toFind);
        while (pos != -1)
        {
            sb.replace(pos, pos + toFind.length(), replacement);
            pos = sb.indexOf(toFind, pos + 1);
        }
    }



    private static String getStrippedLog(String wood)
    {
        String sub = WoodTypeManager.getLogRecipeSubstitute(wood);
        if (sub != null)
        {
            return sub;
        }
        DynamicAssetConfig.WoodSet specialSet = DynamicAssetConfig.getWoodSet(wood);
        if (specialSet != null)
        {
            return JOIN.formatted(specialSet.modId(), specialSet.log());
        }
        return TEMPLATE_LOG.formatted(WoodTypeLister.getHostMod(wood), wood);
    }
    private static final String JOIN = "%s:%s";
    private static final String JOIN3 = "%s:%s%s";
    private static final String TEMPLATE_LOG = "%s:stripped_%s_log";



    private static String getSlab(String wood)
    {
        DynamicAssetConfig.WoodSet specialSet = DynamicAssetConfig.getWoodSet(wood);
        if (specialSet != null)
        {
            return JOIN.formatted(specialSet.modId(), specialSet.slab());
        }
        return JOIN3.formatted(WoodTypeLister.getHostMod(wood), wood, "_slab");
    }



    static String getPlanks(String wood)
    {
        DynamicAssetConfig.WoodSet specialSet = DynamicAssetConfig.getWoodSet(wood);
        if (specialSet != null)
        {
            return JOIN.formatted(specialSet.modId(), specialSet.planks());
        }
        return JOIN3.formatted(WoodTypeLister.getHostMod(wood), wood, "_planks");
    }



    private static final String[] allRecipes = {
            "recipes/book_shelf_double_spruce.json",
            "recipes/book_shelf_minimal_spruce.json",
            "recipes/book_shelf_open_double_spruce.json",
            "recipes/book_shelf_open_minimal_from_double_spruce.json",
            "recipes/book_shelf_open_minimal_spruce.json",
            "recipes/book_shelf_with_lanterns_spruce.json",
            "recipes/potion_shelf_spruce.json",
            "recipes/simple_table_normal_spruce.json",
            "recipes/simple_table_replacement_spruce.json",
            "recipes/tool_rack_double_spruce.json",
            "recipes/tool_rack_framed_spruce.json",
            "recipes/tool_rack_pframed_spruce.json",
            "recipes/tool_rack_single_from_multi_spruce.json",
            "recipes/tool_rack_single_spruce.json",
            "recipes/workstation_placer_spruce.json"
    };
    private static final List<String> conversionRecipes = List.of(
            "recipes/book_shelf_minimal_spruce.json",
            "recipes/book_shelf_open_double_spruce.json",
            "recipes/book_shelf_open_minimal_from_double_spruce.json",
            "recipes/book_shelf_open_minimal_spruce.json",
            "recipes/book_shelf_with_lanterns_spruce.json",
            "recipes/tool_rack_double_spruce.json",
            "recipes/tool_rack_pframed_spruce.json",
            "recipes/tool_rack_single_from_multi_spruce.json"
    );
    private static final List<String> conversionNames = List.of(
            "book_shelf_double_spruce",      // there were better ways to do this
            "book_shelf_minimal_spruce",
            "book_shelf_open_double_spruce",
            "book_shelf_open_minimal_spruce",
            "tool_rack_double_spruce",
            "tool_rack_pframed_spruce",
            "tool_rack_framed_spruce",
            "tool_rack_single_spruce",
            "simple_table_spruce"
    );
}
