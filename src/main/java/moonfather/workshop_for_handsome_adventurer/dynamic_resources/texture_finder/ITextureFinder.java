package moonfather.workshop_for_handsome_adventurer.dynamic_resources.texture_finder;

import moonfather.workshop_for_handsome_adventurer.dynamic_resources.CustomTripletSupport;

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
            return getTexturePathForPlanks(modId, wood, wood.substring(3));
        }    }

    default String getTexturePathForLogs(String modId, String wood)
    {
        if (! CustomTripletSupport.isSpecial(wood))
        {
            return getTexturePathForLogs(modId, wood, "stripped_%s_log");
        }
        else
        {
            return getTexturePathForLogs(modId, wood, wood.substring(3));
        }
    }

    String getTexturePathForPlanks(String modId, String wood, String blockNameTemplate);
    String getTexturePathForLogs(String modId, String wood, String blockNameTemplate);
}
