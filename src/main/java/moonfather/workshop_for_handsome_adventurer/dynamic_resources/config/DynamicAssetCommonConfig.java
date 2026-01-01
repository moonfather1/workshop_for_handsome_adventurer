package moonfather.workshop_for_handsome_adventurer.dynamic_resources.config;

import moonfather.workshop_for_handsome_adventurer.dynamic_resources.CustomTripletSupport;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.WoodTypeCommonManager;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.*;

public class DynamicAssetCommonConfig
{
    private static final ModConfigSpec.Builder BUILDER;
    public static final ModConfigSpec.BooleanValue generate_blocks_for_mod_added_woods;
    public static final ModConfigSpec.ConfigValue<String> blacklist, mergelist;
    public static final ModConfigSpec.ConfigValue<String> stripped_log_substitution_list_for_recipes;
    public static final ModConfigSpec.ConfigValue<String> blocks_with_dumbass_names, blocks_with_dumbass_names2;
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
        mergelist = BUILDER
                .comment("Wood types that you want merged with existing wood of the same name. Only works for wood listed at the bottom of this file (non-standard names).")
                .define("Mergelist", "tfc:oak, tfc:spruce, tfc:acacia, tfc:mangrove");
        stripped_log_substitution_list_for_recipes = BUILDER
                .comment("For wood types that do not have stripped logs, you can specify table top block here. If you do not, we are skipping that wood type.")
                .define("Stripped log substitution list for recipes", "bamboo=minecraft:stripped_bamboo_block, treated_wood_horizontal=minecraft:polished_blackstone,  crimson=minecraft:stripped_crimson_stem, warped=minecraft:stripped_warped_stem,  edelwood=forbidden_arcanus:edelwood_planks");
        blocks_with_dumbass_names = BUILDER
                .comment("This is a list of blocks that do not follow usual naming scheme. Set consists of planks, slab and log, separated by slashes. Separate all sets with comma. You can use stripped_log_substitution together with this. Example is IE's treated wood as it has no logs.")
                .define("Blocks with dumbass names", "immersiveengineering:treated_wood_horizontal/slab_treated_wood_horizontal/no_log_for_this_one, growthcraft_apples:apple_plank/apple_plank_slab/apple_wood_log_stripped,   vinery:cherry_planks/cherry_slab/stripped_cherry_log,   twilightforest:mangrove_planks/mangrove_slab/stripped_mangrove_log");
        blocks_with_dumbass_names2 = BUILDER
                .comment("Same as above, but more; this way when author adds some defaults, you don't have to delete the config to get them.")
                .define("Blocks with dumbass names part 2", "tfc:wood/planks/acacia/wood/planks/acacia_slab/wood/stripped_log/acacia, tfc:wood/planks/ash/wood/planks/ash_slab/wood/stripped_log/ash, tfc:wood/planks/aspen/wood/planks/aspen_slab/wood/stripped_log/aspen, tfc:wood/planks/birch/wood/planks/birch_slab/wood/stripped_log/birch, tfc:wood/planks/blackwood/wood/planks/blackwood_slab/wood/stripped_log/blackwood, tfc:wood/planks/chestnut/wood/planks/chestnut_slab/wood/stripped_log/chestnut"
                        + ",  tfc:wood/planks/douglas_fir/wood/planks/douglas_fir_slab/wood/stripped_log/douglas_fir, tfc:wood/planks/hickory/wood/planks/hickory_slab/wood/stripped_log/hickory, tfc:wood/planks/kapok/wood/planks/kapok_slab/wood/stripped_log/kapok, tfc:wood/planks/mangrove/wood/planks/mangrove_slab/wood/stripped_log/mangrove, tfc:wood/planks/maple/wood/planks/maple_slab/wood/stripped_log/maple, tfc:wood/planks/oak/wood/planks/oak_slab/wood/stripped_log/oak"
                        + ",  tfc:wood/planks/palm/wood/planks/palm/wood/stripped_log/palm, tfc:wood/planks/pine/wood/planks/pine_slab/wood/stripped_log/pine, tfc:wood/planks/rosewood/wood/planks/rosewood_slab/wood/stripped_log/rosewood, tfc:wood/planks/sequoia/wood/planks/sequoia_slab/wood/stripped_log/sequoia, tfc:wood/planks/spruce/wood/planks/spruce_slab/wood/stripped_log/spruce, tfc:wood/planks/sycamore/wood/planks/sycamore_slab/wood/stripped_log/sycamore"
                        + ",  tfc:wood/planks/white_cedar/wood/planks/white_cedar/wood/stripped_log/white_cedar, tfc:wood/planks/willow/wood/planks/willow_slab/wood/stripped_log/willow");
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
            processArray(blocks_with_dumbass_names.get());
            processArray(blocks_with_dumbass_names2.get());
        }
        return woodSetsWithDumbassNames;
    }
    public static void processArray(String configLine)
    {
        String[] temp1 = configLine.split(", *");
        for (String s : temp1)
        {
            String[] temp2 = s.split(":");
            if (temp2.length == 2)
            {
                String[] temp3 = temp2[1].split("/");
                if (temp3.length % 3 != 0)
                {
                    continue;
                }
                if (temp3.length > 3)
                {
                    gluePartsBackTogether(temp3);
                }
                String woodId = temp3[0];
                if (woodId.contains("/")) { woodId = woodId.substring(woodId.lastIndexOf('/') + 1); }
                if (woodId.contains("_plank")) { woodId = woodId.substring(0, woodId.indexOf("_plank")); }
                woodId = CustomTripletSupport.addPrefixTo(woodId);
                woodSetsWithDumbassNames.add(new WoodTypeCommonManager.WoodSet(temp2[0], woodId,  temp3[0], temp3[1], temp3[2]));
            }
        }
    }
    private static final Collection<WoodTypeCommonManager.WoodSet> woodSetsWithDumbassNames = new ArrayList<>();

    private static void gluePartsBackTogether(String[] parts)
    {
        int partsPerPath = parts.length / 3;
        for (int i = 1; i < partsPerPath; i++)
        {
            parts[0] = parts[0] + "/" + parts[i];
        }
        parts[1] = parts[partsPerPath];
        for (int i = 1; i < partsPerPath; i++)
        {
            parts[1] = parts[1] + "/" + parts[partsPerPath + i];
        }
        parts[2] = parts[partsPerPath * 2];
        for (int i = 1; i < partsPerPath; i++)
        {
            parts[2] = parts[2] + "/" + parts[partsPerPath * 2 + i];
        }
    }

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

    ////////////////////////////////////////////////////////////////

    public static boolean isToBeMerged(String modId, String wood)
    {
        if (mergeListResolved == null)
        {
            mergeListResolved = Arrays.asList(mergelist.get().split(", *"));
        }
        return mergeListResolved.contains(modId + ":" + CustomTripletSupport.stripPrefix(wood)) || mergeListResolved.contains(modId + ":*");
    }
    private static List<String> mergeListResolved = null;
}
