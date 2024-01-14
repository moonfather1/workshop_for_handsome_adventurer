package moonfather.workshop_for_handsome_adventurer.dynamic_resources.texture_finder;

public interface ITextureFinder
{
    default String getTexturePathForPlanks(String modId, String wood)
    {
        return getTexturePathForPlanks(modId, wood, "%s_planks");
    }

    default String getTexturePathForLogs(String modId, String wood)
    {
        return getTexturePathForLogs(modId, wood, "stripped_%s_log");
    }

    String getTexturePathForPlanks(String modId, String wood, String blockNameTemplate);
    String getTexturePathForLogs(String modId, String wood, String blockNameTemplate);
}