package moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers;

import moonfather.workshop_for_handsome_adventurer.Constants;
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

        final String[] files = {
                "dual_table_bottom_left_",
                "tool_rack_double_",
                "tool_rack_pframed_",
                "tool_rack_framed_"
        };
        final String template = "\"%s:%s%s\"";
        boolean first = true;
        for (String file: files)
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
                builder.append(template.formatted(Constants.MODID, file, wood));
            }
        }
        builder.append("  ]\n}\n");
        cache.put(new ResourceLocation("packingtape", "tags/blocks/te_blacklist.json"), builder.toString());
    }
}
