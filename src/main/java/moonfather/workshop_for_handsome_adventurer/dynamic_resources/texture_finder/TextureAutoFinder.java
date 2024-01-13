package moonfather.workshop_for_handsome_adventurer.dynamic_resources.texture_finder;

import dev.lukebemish.dynamicassetgenerator.api.ResourceCache;
import dev.lukebemish.dynamicassetgenerator.api.client.AssetResourceCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextureAutoFinder implements ITextureFinder
{
    public static ITextureFinder create()
    {
        return new TextureAutoFinder();
    }

    public String getTexturePathForPlanks(String modId, String wood, String blockNameTemplate)
    {
        return getTexturePathInternal(modId, wood, blockNameTemplate, "all");
    }
    public String getTexturePathForLogs(String modId, String wood, String blockNameTemplate)
    {
        return getTexturePathInternal(modId, wood, blockNameTemplate, "side");
    }
    private static String getTexturePathInternal(String modId, String wood, String blockTemplate, String textureNameInModelFile)
    {
        String key = "%s:%s/%s".formatted(modId, wood, textureNameInModelFile);
        if (PATH_CACHE.containsKey(key))
        {
            return PATH_CACHE.get(key);
        }
        AssetResourceCache cache = getCache(modId);
        try
        {
            ResourceLocation rl1 = new ResourceLocation(modId, "blockstates/" + blockTemplate.formatted(wood));
            IoSupplier<InputStream> sup1 = cache.getContext().getResource(rl1);
            BufferedReader br1 = new BufferedReader(new InputStreamReader(sup1.get()));
            String file1 = br1.lines().collect(Collectors.joining("\n"));
            Matcher m1 = PATTERN_IN_BLOCKSTATE.matcher(file1);
            m1.find();
            String path1 = m1.group(1); // model

            ResourceLocation rl2 = new ResourceLocation(modId, "models/" + path1 + ".json");
            IoSupplier<InputStream> sup2 = cache.getContext().getResource(rl2);
            BufferedReader br2 = new BufferedReader(new InputStreamReader(sup2.get()));
            String file2 = br2.lines().collect(Collectors.joining("\n"));
            Matcher m2 = (textureNameInModelFile.equals("all") ? PATTERN_IN_MODEL_ALL : PATTERN_IN_MODEL_SIDE).matcher(file2); // %s is all for planks and side for logs
            m2.find();
            String result = m2.group(1); // texture

            PATH_CACHE.put(key, result);
            return result;
        }
        catch (Exception ignored) {}
        return null;
    }
    private static final Pattern PATTERN_IN_BLOCKSTATE = Pattern.compile("\"model\"\\s*:\\s*\"[a-z0-9_]+:(.+?)\"");
    private static final Pattern PATTERN_IN_MODEL_ALL = Pattern.compile("\"all\"\\s*:\\s*\"[a-z0-9_]+:(.+?)\"");
    private static final Pattern PATTERN_IN_MODEL_SIDE = Pattern.compile("\"side\"\\s*:\\s*\"[a-z0-9_]+:(.+?)\"");

    //////////////////////////////

    private static final Map<String, String> PATH_CACHE = new HashMap<>();

    //////////////////////////////

    private static final Map<String, AssetResourceCache> CACHES = new HashMap<>();
    private static AssetResourceCache getCache(String namespace)
    {
        if (CACHES.containsKey(namespace))
        {
            return CACHES.get(namespace);
        }
        AssetResourceCache newOne = ResourceCache.register(new AssetResourceCache(new ResourceLocation(namespace, "assets")));
        CACHES.put(namespace, newOne);
        return newOne;
    }

}
