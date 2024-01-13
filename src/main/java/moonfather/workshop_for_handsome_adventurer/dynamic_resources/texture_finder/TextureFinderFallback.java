package moonfather.workshop_for_handsome_adventurer.dynamic_resources.texture_finder;

public class TextureFinderFallback implements ITextureFinder
{
    @Override
    public String getTexturePathForPlanks(String modId, String wood, String blockNameTemplate)
    {
        return null;
    }

    @Override
    public String getTexturePathForLogs(String modId, String wood, String blockNameTemplate) { return null; }
}
