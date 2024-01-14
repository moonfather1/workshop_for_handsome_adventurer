package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import moonfather.workshop_for_handsome_adventurer.dynamic_resources.texture_finder.ITextureFinder;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.texture_finder.TextureAutoFinder;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.texture_finder.TextureFinderFallback;

public class WoodTypeManager
{
    public static ITextureFinder getFinder()
    {
        if (textureFinder == null)
        {
            if (DynamicAssetConfig.autoSearchEnabled())
            {
                textureFinder = TextureAutoFinder.create();
            }
            else
            {
                textureFinder = new TextureFinderFallback();
            }
        }
        return textureFinder;
    }
    private static ITextureFinder textureFinder = null;

    ////////////////////////////////////////////////////////////////////

    public static String getLogRecipeSubstitute(String wood) { return DynamicAssetConfig.getLogRecipeSubstitution(wood); }
    public static String getLogTextureSubstitute(String wood) { return DynamicAssetConfig.getLogTexSubstitution(wood); }
    public static String getTexture1Template(String wood) { return DynamicAssetConfig.getPlankPath(WoodTypeLister.getHostMod(wood)); }
    public static String getTexture2Template(String wood) { return DynamicAssetConfig.getLogPath(WoodTypeLister.getHostMod(wood)); }
    public static String getTexture2TemplateForMod(String namespace) { return DynamicAssetConfig.getLogPath(namespace); }
    public static boolean isUsingDarkerWorkstation(String wood) { return DynamicAssetConfig.isUsingDarkerWorkstation(wood); }
}
