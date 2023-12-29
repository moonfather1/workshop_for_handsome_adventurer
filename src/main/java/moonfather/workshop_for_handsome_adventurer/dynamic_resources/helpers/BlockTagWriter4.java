package moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers;

import moonfather.workshop_for_handsome_adventurer.dynamic_resources.WoodTypeLister;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class BlockTagWriter4
{
    public static void writeFiles(Map<ResourceLocation, String> cache)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("  \"replace\": false,\n");
        builder.append("  \"values\": [\n");

        String[] lines = {
                          "\"workshop_for_handsome_adventurer:tool_rack_single_",
                          "\"workshop_for_handsome_adventurer:tool_rack_double_",
                          "\"workshop_for_handsome_adventurer:tool_rack_pframed_",
                          "\"workshop_for_handsome_adventurer:tool_rack_framed_",
                          "\"workshop_for_handsome_adventurer:potion_shelf_",
                          "\"workshop_for_handsome_adventurer:book_shelf_minimal_",
                          "\"workshop_for_handsome_adventurer:book_shelf_open_minimal_",
                          "\"workshop_for_handsome_adventurer:book_shelf_double_",
                          "\"workshop_for_handsome_adventurer:book_shelf_open_double_",
                          "\"workshop_for_handsome_adventurer:book_shelf_with_lanterns_"
        };
        boolean first = true;
        for (String line: lines)
        {
            for (String wood : WoodTypeLister.getWoodIds())
            {
                if (!first)
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
        cache.put(new ResourceLocation("inventorytabs", "tags/blocks/mod_compat_blacklist.json"), builder.toString());
    }
}
