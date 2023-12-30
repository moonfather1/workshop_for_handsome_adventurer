package moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.WoodTypeLister;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

import java.util.Map;

public class BlockTagWriter2
{
    public static void writeFiles(Map<ResourceLocation, String> cache)
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
            if (ModList.get().isLoaded("tetra_tables"))
            {
                builder.append(",\n").append(lineTetra).append(wood).append('"');
            }
        }
        builder.append("  ]\n}\n");
        cache.put(new ResourceLocation("minecraft", "tags/blocks/mineable/axe.json"), builder.toString());
    }


    private static final String template = "\"%s:%s%s\"";
    private static final String[] files = {
            "simple_table_",
            "dual_table_bottom_left_",
            "dual_table_bottom_right_",
            "dual_table_top_left_",
            "dual_table_top_right_",
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
    private static final String lineTetra = "\"tetra_tables:tetra_table_";
}
