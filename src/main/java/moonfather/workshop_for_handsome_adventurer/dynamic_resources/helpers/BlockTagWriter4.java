package moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers;

import moonfather.workshop_for_handsome_adventurer.Constants;
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

        final String[] files = {
                "tool_rack_single_",
                "tool_rack_double_",
                "tool_rack_pframed_",
                "tool_rack_framed_",
                "potion_shelf_",
                "book_shelf_minimal_",
                "book_shelf_open_minimal_",
                "book_shelf_double_",
                "book_shelf_open_double_",
                "book_shelf_with_lanterns_"
        };
        final String template = "\"%s:%s%s\"";
        boolean first = true;
        for (String file: files)
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
                builder.append("\"").append(Constants.MODID).append(":").append(file).append(wood).append("\"");
            }
        }
        builder.append("  ]\n}\n");
        cache.put(new ResourceLocation("inventorytabs", "tags/blocks/mod_compat_blacklist.json"), builder.toString());
    }
}
