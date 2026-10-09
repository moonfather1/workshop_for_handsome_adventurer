package moonfather.workshop_for_handsome_adventurer.dynamic_resources.config;

import com.mojang.logging.LogUtils;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.ModWorkshop;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.WoodTypeCommonManager;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
                .define("Blacklist", "vinery:cherry");
        stripped_log_substitution_list_for_recipes = BUILDER
                .comment("For wood types that do not have stripped logs, you can specify table top block here. If you do not, we are skipping that wood type.")
                .define("Stripped log substitution list for recipes", "treated_wood_horizontal=minecraft:polished_blackstone,  crimson=minecraft:stripped_crimson_stem");
        blocks_with_dumbass_names = BUILDER
                .comment("This is a list of blocks that do not follow usual naming scheme. Set consists of planks, slab and log, separated by slashes. Separate all sets with comma. You can use stripped_log_substitution together with this. Example is IE's treated wood as it has no logs.")
                .define("Blocks with dumbass names", "immersiveengineering:treated_wood_horizontal/slab_treated_wood_horizontal/no_log_for_this_one, growthcraft_apples:apple_plank/apple_plank_slab/apple_wood_log_stripped,   vinery:cherry_planks/cherry_slab/stripped_cherry_log");
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
            List<String> entries = new ArrayList<>();
            String[] temp1 = blocks_with_dumbass_names.get().split(", *");
            Collections.addAll(entries, temp1);
            HashSet<String> idsWeHave = new HashSet<>();
            read("config/workshop/factory_defaults__nonstandard_names.dat", entries);
            for (String s : entries)
            {
                String[] temp2 = s.split(":");
                if (temp2.length == 2)
                {
                    String[] temp3 = temp2[1].split("/");
                    if (temp3.length == 6)
                    {
                        temp3[0] = temp3[0] + '/' + temp3[1];
                        temp3[1] = temp3[2] + '/' + temp3[3];
                        temp3[2] = temp3[4] + '/' + temp3[5];
                    }
                    if (temp3.length == 9)
                    {
                        temp3[0] = temp3[0] + '/' + temp3[1] + '/' + temp3[2];
                        temp3[1] = temp3[3] + '/' + temp3[4] + '/' + temp3[5];
                        temp3[2] = temp3[6] + '/' + temp3[7] + '/' + temp3[8];
                    }
                    if (temp3.length == 3 || temp3.length == 6 || temp3.length == 9)
                    {
                        if (! idsWeHave.contains(temp3[0]))
                        {
                            woodSetsWithDumbassNames.add(new WoodTypeCommonManager.WoodSet(temp2[0], temp3[0], temp3[1], temp3[2]));
                            idsWeHave.add(temp3[0]);
                        }
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
            List<String> entries = new ArrayList<>();
            String[] temp1 = stripped_log_substitution_list_for_recipes.get().split(", *");
            Collections.addAll(entries, temp1);
            HashSet<String> idsWeHave = new HashSet<>();
            read("config/workshop/factory_defaults__dont_have_logs.dat", entries);
            for (String s : entries)
            {
                String[] temp2 = s.split(" *= *");
                if (temp2.length == 2)
                {
                    if (! idsWeHave.contains(temp2[0]))
                    {
                        subRecipeList.put(temp2[0], temp2[1]);
                        idsWeHave.add(temp2[0]);
                    }
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
            blackListResolved = new ArrayList<>();
            Collections.addAll(blackListResolved, blacklist.get().split(", *"));
            read("config/workshop/factory_defaults__blacklist.dat", blackListResolved);
        }
        return blackListResolved.contains(modId + ":" + wood) || blackListResolved.contains(modId + ":*");
    }
    private static List<String> blackListResolved = null;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static void read(String path, List<String> entries)
    {
        if (! ModList.get().getModFileById(Constants.MODID).getFile().getContents().containsFile("/"+path))
        {
            return;
        }
        try
        {
            InputStream file1 = ModList.get().getModFileById(Constants.MODID).getFile().getContents().openFile("/"+path);
            var reader = new BufferedReader(new InputStreamReader(file1, StandardCharsets.UTF_8));
            do {
                String s = reader.readLine();
                if (s == null)
                {
                    LogUtils.getLogger().info("[Workshop] Done reading factory defaults. Path:  \"{}\".", path);
                    return;
                }
                s = s.trim();
                if (! s.isEmpty())
                {
                    entries.add(s);
                }
            }
            while (true);
        }
        catch (IOException e)
        {
            LogUtils.getLogger().error("[Workshop] Error reading factory defaults. Error:  \"{}\".", e.getMessage());
        }
    }
}
