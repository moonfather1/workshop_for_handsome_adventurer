package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import moonfather.workshop_for_handsome_adventurer.dynamic_resources.config.DynamicAssetCommonConfig;

import java.util.Collection;

public class WoodTypeCommonManager
{
    public static String getLogRecipeSubstitute(String wood) { return DynamicAssetCommonConfig.getLogRecipeSubstitution(CustomTripletSupport.stripPrefix(wood)); }

    ////////////////////////////

    public static Collection<WoodSet> getWoodSetsWithDumbassNames()
    {
        return DynamicAssetCommonConfig.getWoodSetsWithDumbassNames();
    }

    public static WoodSet getWoodSet(String wood)
    {
        for (WoodSet set: DynamicAssetCommonConfig.getWoodSetsWithDumbassNames())
        {
            if (set.woodId.equals(wood)) { return  set; }
        }
        return null;
    }
    public static WoodSet getWoodSetForDuplicate(String modId, String wood)
    {
        for (WoodSet set: DynamicAssetCommonConfig.getWoodSetsWithDumbassNames())
        {
            if (set.woodId().equals(wood) && set.modId().equals(modId)) { return  set; }
        }
        return null;
    }

    public record WoodSet(String modId, String woodId, String planks, String slab, String log) { }
}
