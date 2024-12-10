package moonfather.workshop_for_handsome_adventurer.dynamic_resources.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashMap;
import java.util.Map;

public class DynamicAssetClientConfig
{
    private static final ModConfigSpec.Builder BUILDER;
    public static final ModConfigSpec.IntValue MinimumNumberOfSetsForSeparateCreativeTab;
    public static final ModConfigSpec.BooleanValue DisableAutosearchingForTextures;
    public static final ModConfigSpec.ConfigValue<String> StrippedLogSubstitutionListForTextures;
    public static final ModConfigSpec.ConfigValue<String> TextureTemplate1List;
    public static final ModConfigSpec.ConfigValue<String> TextureTemplate2List;
    public static final ModConfigSpec.ConfigValue<String> UseDarkerWorkstationModel;
    public static final ModConfigSpec.BooleanValue UseDAG;
    public static final ModConfigSpec SPEC;



    static //constructor
    {
        BUILDER = new ModConfigSpec.Builder();
        BUILDER.push("Tab in creative inventory");
            MinimumNumberOfSetsForSeparateCreativeTab = BUILDER
                    .comment("How many wood sets are needed for this mod to use a second tab in creative mode for dynamically created blocks. Set to a high number to disable second tab and shove everything into first.")
                    .defineInRange("Minimum number of sets for separate creative_tab", 4+4, 0, 1000);
        BUILDER.pop();
        BUILDER.push("Texture paths");
            DisableAutosearchingForTextures = BUILDER
                    .comment("No reason to disable this. If you do, you need to set texture paths like in this file. While this is enabled, settings below that are texture paths are unused and unneeded.")
                    .define("Disable autosearching for textures", false);
        BUILDER.push("Texture paths - manual, if autosearching is disabled");
            StrippedLogSubstitutionListForTextures = BUILDER
                    .comment("For wood types that do not have stripped logs, you can specify table top block here. If you do not, we are skipping that wood type.")
                    .define("Stripped log substitution list for textures", "embur=embur, sythian=sythian, bamboo=stripped_bamboo_block, bulbis=bulbis");
            TextureTemplate1List = BUILDER
                    .comment("Tells us where to find plank textures, in case mod uses subdirectories (like byg) or different file names. Second %s below is the wood type. Separate using commas.")
                    .define("Texture template1 list", "biomeswevegone=%s:block/%s/planks, aether=%s:block/construction/%s_planks");
            TextureTemplate2List = BUILDER
                    .comment("Tells us where to find stripped log textures, in case mod uses subdirectories (like byg) or different file names. Second %s below is the wood type. Separate using commas.")
                    .define("Texture template2 list", "biomeswevegone=%s:block/%s/stripped_log,  aether=%s:block/natural/stripped_%s_log");
            UseDarkerWorkstationModel = BUILDER
                    .comment("Slightly different model. Do not worry about this. Or just list dark woods here.")
                    .define("Use darker workstation model", "embur,hellbark,bulbis,cika,lament,dead,blackwood");
        BUILDER.pop();
        BUILDER.push("Autosearching method");
            UseDAG = BUILDER
                    .comment("Use the DAG mod to obtain models and textures. There is a backup system, clearly, but DAG should work for cases when this mods resources are overridden by resource packs. Game loading speed may vary.")
                    .define("Use Dynamic Asset Generator", false);
        BUILDER.pop();
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    ///////////////////////////////////////

    public static boolean autoSearchEnabled()
    {
        return DisableAutosearchingForTextures.isFalse();
    }
    public static String getLogTexSubstitution(String wood)
    {
        return getFromSplitConfig(StrippedLogSubstitutionListForTextures.get(), subTextureList, wood);
    }
    public static String getPlankPath(String modId)
    {
        return getFromSplitConfig(TextureTemplate1List.get(), plankPathList, modId);
    }
    public static String getLogPath(String modId)
    {
        return getFromSplitConfig(TextureTemplate2List.get(), logPathList, modId);
    }
    public static boolean isUsingDarkerWorkstation(String wood)
    {
        return UseDarkerWorkstationModel.get().contains(wood);
    }

    ///////////////////////////////////////////////////////

    private static final Map<String, String> plankPathList = new HashMap<>();
    private static final Map<String, String> subTextureList = new HashMap<>();
    private static final Map<String, String> logPathList = new HashMap<>();

    private static String getFromSplitConfig(String input, Map<String, String> list, String wood)
    {
        if (list.isEmpty())
        {
            String[] temp1 = input.split(", *");
            for (String s : temp1)
            {
                String[] temp2 = s.split(" *= *");
                if (temp2.length == 2)
                {
                    list.put(temp2[0], temp2[1]);
                }
            }
        }
        return list.getOrDefault(wood, null);
    }
}
