package moonfather.workshop_for_handsome_adventurer.dynamic_resources.config;

import moonfather.workshop_for_handsome_adventurer.dynamic_resources.WoodTypeCommonManager;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.*;

public class DynamicAssetCommonConfig
{
    private static final ModConfigSpec.Builder BUILDER;
    public static final ModConfigSpec.BooleanValue generate_blocks_for_mod_added_woods;
    public static final ModConfigSpec.ConfigValue<String> blacklist;
    public static final ModConfigSpec.ConfigValue<String> stripped_log_substitution_list_for_recipes;
    public static final ModConfigSpec.ConfigValue<String> blocks_with_dumbass_names;
    public static final ModConfigSpec SPEC;



    static //constructor
    {
        BUILDER = new ModConfigSpec.Builder();
        BUILDER.push("Master switch");
        generate_blocks_for_mod_added_woods = BUILDER
                .comment("Unicorn-magic-powered system that automatically adds tables/racks/shelves for all wood types in the game (yes, modded ones too, that is the point of this system). If you turn this off (does not work? please report!), workshop blocks will only be added in vanilla wood types.  Option requires game restart. Synchronization untested.")
                .define("Generate blocks for mod-added woods", true);
        BUILDER.pop();
        BUILDER.push("Other");
        blacklist = BUILDER
                .comment("First and obvious use is to blacklist wood types that you really, really hate to see. Second and non-obvious use: say you have a duplicate wood type; normally it just gets recipes that give blocks of other type of same name; but if you really, really wish to have blocks of this type, black-list them here so that they are not added to dupes list and in blocks_with_dumbass_names setting in this file, add them in format modid:planks/slab/strippedlog; good example is Vinery mod which insists on cherry wood even in 1.20; if you do this, you get workshop blocks in vanilla cherry (light pink) and Vinery's cherry (dark red) separately. Oh, and asterisk after the colon works.")
                .define("Blacklist", "vinery:cherry,  twilightforest:mangrove");
        stripped_log_substitution_list_for_recipes = BUILDER
                .comment("For wood types that do not have stripped logs, you can specify table top block here. If you do not, we are skipping that wood type.")
                .define("Stripped log substitution list for recipes", "bamboo=minecraft:stripped_bamboo_block, treated_wood_horizontal=minecraft:polished_blackstone,  crimson=minecraft:stripped_crimson_stem, warped=minecraft:stripped_warped_stem,  edelwood=forbidden_arcanus:edelwood_planks");
        blocks_with_dumbass_names = BUILDER
                .comment("This is a list of blocks that do not follow usual naming scheme. Set consists of planks, slab and log, separated by slashes. Separate all sets with comma. You can use stripped_log_substitution together with this. Example is IE's treated wood as it has no logs.")
                .define("Blocks with dumbass names", "immersiveengineering:treated_wood_horizontal/slab_treated_wood_horizontal/no_log_for_this_one, growthcraft_apples:apple_plank/apple_plank_slab/apple_wood_log_stripped,   vinery:cherry_planks/cherry_slab/stripped_cherry_log,   twilightforest:mangrove_planks/mangrove_slab/stripped_mangrove_log");
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    ////////////////////////////

    public static boolean masterLeverOn()
    {
        return generate_blocks_for_mod_added_woods == null || generate_blocks_for_mod_added_woods.isTrue();
    }

    public static Collection<WoodTypeCommonManager.WoodSet> getWoodSetsWithDumbassNames()
    {
        if (woodSetsWithDumbassNames.isEmpty())
        {
            String[] temp1 = blocks_with_dumbass_names.get().split(", *");
            for (String s : temp1)
            {
                String[] temp2 = s.split(":");
                if (temp2.length == 2)
                {
                    String[] temp3 = temp2[1].split("/");
                    if (temp3.length == 3)
                    {
                        woodSetsWithDumbassNames.add(new WoodTypeCommonManager.WoodSet(temp2[0], temp3[0], temp3[1], temp3[2]));
                    }
                }
            }
        }
        return woodSetsWithDumbassNames;
    }
    private static final Collection<WoodTypeCommonManager.WoodSet> woodSetsWithDumbassNames = new ArrayList<>();



    ////////////////////////////////////////////////////////////

    public static String getLogRecipeSubstitution(String wood)
    {
        if (subRecipeList.isEmpty())
        {
            String[] temp1 = stripped_log_substitution_list_for_recipes.get().split(", *");
            for (String s : temp1)
            {
                String[] temp2 = s.split(" *= *");
                if (temp2.length == 2)
                {
                    subRecipeList.put(temp2[0], temp2[1]);
                }
            }
        }
        return subRecipeList.getOrDefault(wood, null);
    }
    private static final Map<String, String> subRecipeList = new HashMap<>();

    ////////////////////////////////////////////////////////////////

    public static boolean isBlackListed(String modId, String wood)
    {
        if (blackListResolved == null)
        {
            blackListResolved = Arrays.asList(blacklist.get().split(", *"));
        }
        return blackListResolved.contains(modId + ":" + wood) || blackListResolved.contains(modId + ":*");
    }
    private static List<String> blackListResolved = null;
}
