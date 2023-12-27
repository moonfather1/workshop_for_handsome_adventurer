package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
import moonfather.workshop_for_handsome_adventurer.OptionsHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WoodTypeLister
{
    public static void reset()
    {
        ids = null;
    }
    public static List<String> getWoodIds()
    {
        if (ids == null)
        {
            Stopwatch s = Stopwatch.createStarted();
            ids = new ArrayList<>();
            if (! /*OptionsHolder.COMMON.DynamicResourceGeneration.get()*/true)
            {
                return ids;
            }
            ids.add("acacia");
            woodToHostMap.put("acacia", "minecraft");
            final String mc = "minecraft";
            final String planks = "_planks";
            final String slab = "_slab";
            final String vertical = "vertical";
            final String LOG1 = "stripped_";
            final String LOG2 = "_log";
            for (ResourceLocation id: ForgeRegistries.BLOCKS.getKeys())
            {
                if (! id.getNamespace().equals(mc) && id.getPath().endsWith(planks) && ! id.getPath().contains(vertical))
                {
                    // looks like wood so far. let's check for slabs and logs as we need them for recipes
                    String wood = id.getPath().replace(planks, "");
                    if (ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(id.getNamespace(), id.getPath().replace(planks, slab)))
                        && ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(id.getNamespace(), LOG1 + wood + LOG2)))
                    {
                        ids.add(wood);
                        woodToHostMap.put(wood, id.getNamespace());
                    }
                }
            }

            //ids.add("willow");
            //woodToHostMap.put("willow", "biomesoplenty");
            s.stop();
            LogUtils.getLogger().info("~~~ woods ids gathered in " + s.elapsed().toMillis() + "ms.");
        }
        return ids;
    }
    public static String getHostMod(String wood) { return woodToHostMap.get(wood); }

    private static List<String> ids = null;
    private static final HashMap<String, String> woodToHostMap = new HashMap<>();

    /////////////////////////////////////////////////////


}
