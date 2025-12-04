package moonfather.workshop_for_handsome_adventurer.dynamic_resources.texture_finder;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.javafmlmod.FMLModContainer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextureAutoFinderBackup implements ITextureFinder
{
    public static ITextureFinder create()
    {
        return new TextureAutoFinderBackup();
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
            // old style doesn't work in NF and i didn't notice
            Path blockStatePath = ModList.get().getModFileById(modId).getFile().findResource("assets", modId, "blockstates", blockTemplate.formatted(wood) + ".json");
            String file1 = Files.readString(blockStatePath);
            Matcher m1 = PATTERN_IN_BLOCKSTATE.matcher(file1);
            m1.find();
            String path1 = m1.group(3); // model

            Path modelPath = ModList.get().getModFileById(modId).getFile().findResource("/assets/%s/models/%s.json".formatted(modId, path1));
            String file2 = Files.readString(modelPath); //let's assume it's in the same mod
            Matcher m2 = (textureIsSide ? PATTERN_IN_MODEL_SIDE : PATTERN_IN_MODEL_ALL).matcher(file2); // %s is all for planks and side for logs
            m2.find();
            String result = m2.group(3); // texture

            PATH_CACHE.put(key, result);
            return result;
        }
        catch (Exception e)
        {
            System.out.println("!!~~ " + e.getMessage());
        }
        return null;
    }
    private static final Pattern PATTERN_IN_BLOCKSTATE = Pattern.compile("\"(model)\"\\s*:\\s*\"([a-z0-9_]+:)?(.+?)\"");
    private static final Pattern PATTERN_IN_MODEL_ALL = Pattern.compile("\"(all|south)\"\\s*:\\s*\"([a-z0-9_]+:)?(.+?)\"");
    private static final Pattern PATTERN_IN_MODEL_SIDE = Pattern.compile("\"(side|south)\"\\s*:\\s*\"([a-z0-9_]+:)?(.+?)\"");

    //////////////////////////////

    private static final Map<String, String> PATH_CACHE = new HashMap<>();
}
