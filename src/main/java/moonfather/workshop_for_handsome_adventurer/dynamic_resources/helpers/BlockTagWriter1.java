package moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.WoodTypeLister;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class BlockTagWriter1
{
    public static void writeFiles(Map<ResourceLocation, String> cache)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("  \"replace\": false,\n");
        builder.append("  \"values\": [\n");

        String line = "\"workshop_for_handsome_adventurer:simple_table_";
        boolean first = true;
        for (String wood: WoodTypeLister.getWoodIds())
        {
            if (! first)
            {
                builder.append(",\n");
            }
            else
            {
                first = false;
            }
            builder.append(line + wood + '"');
        }
        builder.append("\n  ]\n}\n");
        cache.put(new ResourceLocation("forge", "tags/blocks/workbench.json"), builder.toString());
        cache.put(new ResourceLocation("forge", "tags/items/workbench.json"), builder.toString());
    }
}
