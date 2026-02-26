package moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.WoodTypeLister;
import net.minecraft.resources.Identifier;

import java.util.Map;

public class BlockTagWriter5
{
    public static void writeFiles(Map<Identifier, String> cache)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("  \"replace\": false,\n");
        builder.append("  \"values\": [\n");
        boolean first = true;
        for (String wood : WoodTypeLister.getWoodIds())
        {
            for (String file: files)
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
        cache.put(Identifier.fromNamespaceAndPath("c", "tags/block/relocation_not_supported.json"), builder.toString());
    }



    private static final String template = "\"%s:%s%s\"";
    public static final String[] files = {
            "dual_table_bottom_left_",
            "dual_table_bottom_right_",
            "dual_table_top_left_",
            "dual_table_top_right_",
            "tool_rack_double_",
            "tool_rack_pframed_",
            "tool_rack_framed_"
    };
}
