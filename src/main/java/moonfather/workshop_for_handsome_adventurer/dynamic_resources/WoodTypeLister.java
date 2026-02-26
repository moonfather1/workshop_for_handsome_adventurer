package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import com.google.common.base.Stopwatch;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.config.DynamicAssetCommonConfig;
import moonfather.workshop_for_handsome_adventurer.initialization.ContentRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

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
            ids = new ArrayList<>();
            idsWithSpecials = new ArrayList<>(ids);
            if (! DynamicAssetCommonConfig.masterLeverOn()) { return; }
            Stopwatch s = Stopwatch.createStarted();
            ids.add("acacia");
            woodToHostMap.put("acacia", "minecraft");
            ids.add("bamboo");
            woodToHostMap.put("bamboo", "minecraft");
            ids.add("pale_oak");
            woodToHostMap.put("pale_oak", "minecraft");

            ids.add("crimson");
            woodToHostMap.put("crimson", "minecraft");
            ids.add("warped");
            woodToHostMap.put("warped", "minecraft");

            // ready:
            final String mc = "minecraft";
            final String planks = "_planks";
            final String slab = "_slab";
            final String vertical = "vertical";
            final String LOG1 = "stripped_";
            final String LOG2 = "_log";
            for (Identifier id: BuiltInRegistries.BLOCK.keySet())
            {
                if (! id.getNamespace().equals(mc) && id.getPath().endsWith(planks) && ! id.getPath().contains(vertical))
                {
                    // looks like wood so far. let's check for slabs as we need them for recipes
                    String wood = id.getPath().replace(planks, "");
                    if (DynamicAssetCommonConfig.isBlackListed(id.getNamespace(), wood))
                    {
                        continue;
                    }
                    if (BuiltInRegistries.BLOCK.containsKey(Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath().replace(planks, slab))))
                    {
                        if (! ids.contains(wood) && ! ContentRegistration.woodTypes.contains(wood))  // normal dupes and vanilla dupes get recipes only
                        {
                            // check for stripped logs. if we don't have them, we allow a substitution:
                            if (! BuiltInRegistries.BLOCK.containsKey(Identifier.fromNamespaceAndPath(id.getNamespace(), LOG1 + wood + LOG2)))
                            {
                                String substitute = DynamicAssetCommonConfig.getLogRecipeSubstitution(wood);
                                if (substitute == null || ! BuiltInRegistries.BLOCK.containsKey(Identifier.parse(substitute)))
                                {
                                    continue;
                                }
                            }
                            ids.add(wood);
                            woodToHostMap.put(wood, id.getNamespace());
                        }
                        else
                        {
                            if (BuiltInRegistries.BLOCK.containsKey(Identifier.fromNamespaceAndPath(id.getNamespace(), LOG1 + wood + LOG2)))
                            {
                                dupeIds.add(Identifier.fromNamespaceAndPath(id.getNamespace(), wood));
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
            for (WoodTypeCommonManager.WoodSet woodSet: WoodTypeCommonManager.getWoodSetsWithDumbassNames())
            {
                if (! BuiltInRegistries.BLOCK.containsKey(Identifier.fromNamespaceAndPath(woodSet.modId(), woodSet.planks()))) { continue; }
                if (! BuiltInRegistries.BLOCK.containsKey(Identifier.fromNamespaceAndPath(woodSet.modId(), woodSet.slab()))) { continue; }
                if (! BuiltInRegistries.BLOCK.containsKey(Identifier.fromNamespaceAndPath(woodSet.modId(), woodSet.log())))
                {
                    String substitute = DynamicAssetCommonConfig.getLogRecipeSubstitution(woodSet.planks());
                    if (substitute == null || ! BuiltInRegistries.BLOCK.containsKey(Identifier.parse(substitute)))
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
    public static List<Identifier> getDuplicateWoods() { return dupeIds; }

    private static List<String> ids = null;
    private static List<String> idsWithSpecials = null;
    private static final HashMap<String, String> woodToHostMap = new HashMap<>();
    private static final List<Identifier> dupeIds = new ArrayList<>();
}
