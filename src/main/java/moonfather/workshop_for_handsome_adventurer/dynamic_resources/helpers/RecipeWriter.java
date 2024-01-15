package moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.AssetReader;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.DynamicAssetConfig;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.WoodTypeLister;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.WoodTypeManager;
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
            for (String wood: WoodTypeLister.getWoodIds())
            {
                String newRecipe = original
                        .replace("minecraft:stripped_spruce_log", getStrippedLog(wood))
                        .replace("minecraft:spruce_slab", getSlab(wood))
                        .replace("minecraft:spruce_planks", getPlanks(wood))
                        .replace(SPRUCE, wood);
                cache.put(new ResourceLocation(Constants.MODID, file.replace(SPRUCE, wood)), newRecipe);
            }
            if (! conversionRecipes.contains(file))
            {
                for (ResourceLocation duplicate : WoodTypeLister.getDuplicateWoods()) // once again, with feeling
                {
                    String newRecipe = original
                            .replace("minecraft:stripped_spruce", duplicate.getNamespace() + ":stripped_" + duplicate.getPath()) // these will have logs
                            .replace("minecraft:spruce", duplicate.toString())
                            .replace(SPRUCE, duplicate.getPath());
                    cache.put(new ResourceLocation(Constants.MODID, file.replace(SPRUCE, duplicate.getPath() + "_" + duplicate.getNamespace())), newRecipe);
                }
            }
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
            return JOIN.formatted(specialSet.getModId(), specialSet.getLog());
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
            return JOIN.formatted(specialSet.getModId(), specialSet.getSlab());
        }
        return JOIN3.formatted(WoodTypeLister.getHostMod(wood), wood, "_slab");
    }



    static String getPlanks(String wood)
    {
        DynamicAssetConfig.WoodSet specialSet = DynamicAssetConfig.getWoodSet(wood);
        if (specialSet != null)
        {
            return JOIN.formatted(specialSet.getModId(), specialSet.getPlanks());
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
}
