package moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers;

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
            for (String line: lines)
            {
                if (!first)
                {
                    builder.append(",\n");
                }
                else
                {
                    first = false;
                }
                builder.append(line).append(wood).append('"');
            }
            if (ModList.get().isLoaded("tetra_tables"))
            {
                builder.append(",\n").append(lineTetra).append(wood).append('"');
            }
        }
        builder.append("  ]\n}\n");
        cache.put(new ResourceLocation("minecraft", "tags/blocks/mineable/axe.json"), builder.toString());
    }



    private static final String[] lines = {
            "\"workshop_for_handsome_adventurer:simple_table_",
            "\"workshop_for_handsome_adventurer:dual_table_bottom_left_",
            "\"workshop_for_handsome_adventurer:dual_table_bottom_right_",
            "\"workshop_for_handsome_adventurer:dual_table_top_left_",
            "\"workshop_for_handsome_adventurer:dual_table_top_right_",
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
    private static final String lineTetra = "\"tetra_tables:tetra_table_";
}
