package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import com.google.common.base.Stopwatch;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.resources.ResourceLocation;
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



    public static List<String> getWoodIds(boolean includeSpecials)
    {
        generateIfNeeded();
        return includeSpecials ? idsWithSpecials : ids;
    }
    public static List<String> getWoodIds()
    {
        generateIfNeeded();
        return idsWithSpecials;
    }
    public static void generateIfNeeded()
    {
        if (ids == null)
        {
            Stopwatch s = Stopwatch.createStarted();
            ids = new ArrayList<>();
            ids.add("acacia");
            woodToHostMap.put("acacia", "minecraft");
            ids.add("bamboo");
            woodToHostMap.put("bamboo", "minecraft");

            // ready:
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
                    // looks like wood so far. let's check for slabs as we need them for recipes
                    String wood = id.getPath().replace(planks, "");
                    if (DynamicAssetConfig.isBlackListed(id.getNamespace(), wood))
                    {
                        continue;
                    }
                    if (ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(id.getNamespace(), id.getPath().replace(planks, slab))))
                    {
                        if (! ids.contains(wood) && ! Registration.woodTypes.contains(wood))  // normal dupes and vanilla dupes get recipes only
                        {
                            // check for stripped logs. if we don't have them, we allow a substitution:
                            if (! ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(id.getNamespace(), LOG1 + wood + LOG2)))
                            {
                                String substitute = DynamicAssetConfig.getLogRecipeSubstitution(wood);
                                if (substitute == null || ! ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(substitute)))
                                {
                                    continue;
                                }
                            }
                            ids.add(wood);
                            woodToHostMap.put(wood, id.getNamespace());
                        }
                        else
                        {
                            if (ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(id.getNamespace(), LOG1 + wood + LOG2)))
                            {
                                dupeIds.add(new ResourceLocation(id.getNamespace(), wood));
                                // don't care for the final case.
                            }
                        }
                    }
                }
            }
            s.stop();
            ///LogUtils.getLogger().info("~~~ woods ids gathered in " + s.elapsed().toMillis() + "ms.");
            // ok, now about blocks with non-standard names (treated wood)
            idsWithSpecials = new ArrayList<>(ids);
            for (DynamicAssetConfig.WoodSet woodSet: DynamicAssetConfig.getWoodSetsWithDumbassNames())
            {
                if (! ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(woodSet.modId(), woodSet.planks()))) { continue; }
                if (! ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(woodSet.modId(), woodSet.slab()))) { continue; }
                if (! ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(woodSet.modId(), woodSet.log())))
                {
                    String substitute = DynamicAssetConfig.getLogRecipeSubstitution(woodSet.planks());
                    if (substitute == null || ! ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(substitute)))
                    {
                        continue;
                    }
                }
                idsWithSpecials.add(CustomTripletSupport.addPrefixTo(woodSet.planks()));
                woodToHostMap.put(CustomTripletSupport.addPrefixTo(woodSet.planks()), woodSet.modId());
            }
        }
    }
    public static String getHostMod(String wood) { return woodToHostMap.get(wood); }
    public static List<ResourceLocation> getDuplicateWoods() { return dupeIds; }

    private static List<String> ids = null;
    private static List<String> idsWithSpecials = null;
    private static final HashMap<String, String> woodToHostMap = new HashMap<>();
    private static final List<ResourceLocation> dupeIds = new ArrayList<>();
}
