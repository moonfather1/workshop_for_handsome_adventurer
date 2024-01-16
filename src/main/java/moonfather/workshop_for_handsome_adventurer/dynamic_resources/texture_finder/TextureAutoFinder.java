package moonfather.workshop_for_handsome_adventurer.dynamic_resources.texture_finder;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
        return getTexturePathInternal(modId, wood, blockNameTemplate, false);
    }
    public String getTexturePathForLogs(String modId, String wood, String blockNameTemplate)
    {
        return getTexturePathInternal(modId, wood, blockNameTemplate, true);
    }
    private static String getTexturePathInternal(String modId, String wood, String blockTemplate, boolean textureIsSide)
    {
        String key = "%s:%s/%s".formatted(modId, wood, textureIsSide ? "s" : "a");
        if (PATH_CACHE.containsKey(key))
        {
            return PATH_CACHE.get(key);
        }
        try
        {
            Optional<? extends ModContainer> mod1 = ModList.get().getModContainerById(modId);
            if (! mod1.isPresent())
            {
                return null;
            }
            InputStream is1 = mod1.get().getMod().getClass().getResourceAsStream("/assets/%s/blockstates/%s.json".formatted(modId, blockTemplate.formatted(wood)));
            BufferedReader br1 = new BufferedReader(new InputStreamReader(is1));
            String file1 = br1.lines().collect(Collectors.joining("\n"));
            Matcher m1 = PATTERN_IN_BLOCKSTATE.matcher(file1);
            m1.find();
            String path1 = m1.group(3); // model

            InputStream is2 = mod1.get().getMod().getClass().getResourceAsStream("/assets/%s/models/%s.json".formatted(modId, path1)); //let's assume it's in the same mod
            BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));
            String file2 = br2.lines().collect(Collectors.joining("\n"));
            Matcher m2 = (textureIsSide ? PATTERN_IN_MODEL_SIDE : PATTERN_IN_MODEL_ALL).matcher(file2); // %s is all for planks and side for logs
            m2.find();
            String result = m2.group(3); // texture

            PATH_CACHE.put(key, result);
            return result;
        }
        catch (Exception ignored) {}
        return null;
    }
    private static final Pattern PATTERN_IN_BLOCKSTATE = Pattern.compile("\"(model)\"\\s*:\\s*\"([a-z0-9_]+:)?(.+?)\"");
    private static final Pattern PATTERN_IN_MODEL_ALL = Pattern.compile("\"(all|south)\"\\s*:\\s*\"([a-z0-9_]+:)?(.+?)\"");
    private static final Pattern PATTERN_IN_MODEL_SIDE = Pattern.compile("\"(side|south)\"\\s*:\\s*\"([a-z0-9_]+:)?(.+?)\"");

    //////////////////////////////

    private static final Map<String, String> PATH_CACHE = new HashMap<>();
}
