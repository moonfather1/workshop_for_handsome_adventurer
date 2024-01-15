package moonfather.workshop_for_handsome_adventurer.dynamic_resources.texture_finder;

public interface ITextureFinder
{
    default String getTexturePathForPlanks(String modId, String wood)
    {
        if (! wood.startsWith("sx_"))
        {
            return getTexturePathForPlanks(modId, wood, "%s_planks");
        }
        else
        {
            return getTexturePathForPlanks(modId, wood, wood.substring(3));
        }
    }

    default String getTexturePathForLogs(String modId, String wood)
    {
        if (! wood.startsWith("sx_"))
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