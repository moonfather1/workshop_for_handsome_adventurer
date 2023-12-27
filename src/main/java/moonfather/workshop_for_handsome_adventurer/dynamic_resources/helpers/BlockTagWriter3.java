package moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers;

import moonfather.workshop_for_handsome_adventurer.dynamic_resources.WoodTypeLister;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class BlockTagWriter3
{
    public static void writeFiles(Map<ResourceLocation, String> cache)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("  \"replace\": false,\n");
        builder.append("  \"values\": [\n");

        String[] lines = {
                          "\"workshop_for_handsome_adventurer:dual_table_bottom_left_",
                          "\"workshop_for_handsome_adventurer:tool_rack_double_",
                          "\"workshop_for_handsome_adventurer:tool_rack_pframed_",
                          "\"workshop_for_handsome_adventurer:tool_rack_framed_"
        };
        boolean first = true;
        for (String line: lines)
        {
            for (String wood : WoodTypeLister.getWoodIds())
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
        }
        builder.append("  ]\n}\n");
        cache.put(new ResourceLocation("packingtape", "tags/blocks/te_blacklist.json"), builder.toString());
    }
}
