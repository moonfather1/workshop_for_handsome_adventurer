package moonfather.workshop_for_handsome_adventurer.dynamic_resources.texture_finder;

import moonfather.workshop_for_handsome_adventurer.dynamic_resources.CustomTripletSupport;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.WoodTypeCommonManager;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.config.DynamicAssetCommonConfig;

public interface ITextureFinder
{
    default String getTexturePathForPlanks(String modId, String wood)
    {
        if (! CustomTripletSupport.isSpecial(wood))
        {
            return getTexturePathForPlanks(modId, wood, "%s_planks");
        }
        else
        {
            WoodTypeCommonManager.WoodSet set = WoodTypeCommonManager.getWoodSet(wood);
            return getTexturePathForPlanks(modId, wood, set.planks());
        }
    }

    default String getTexturePathForLogs(String modId, String wood)
    {
        if (! CustomTripletSupport.isSpecial(wood))
        {
            return getTexturePathForLogs(modId, wood, "stripped_%s_log");
        }
        else
        {
            WoodTypeCommonManager.WoodSet set = WoodTypeCommonManager.getWoodSet(wood);
            return getTexturePathForLogs(modId, wood, set.log());
        }
    }

    String getTexturePathForPlanks(String modId, String wood, String blockNameTemplate);
    String getTexturePathForLogs(String modId, String wood, String blockNameTemplate);
}
