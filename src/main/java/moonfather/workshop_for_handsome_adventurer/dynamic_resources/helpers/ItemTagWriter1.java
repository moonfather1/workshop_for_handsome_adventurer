package moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.WoodTypeLister;
import net.minecraft.resources.Identifier;

import java.util.Map;

public class ItemTagWriter1
{
    public static void writeFiles(Map<Identifier, String> cache)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("  \"replace\": false,\n");
        builder.append("  \"values\": [\n");

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
            builder.append("\"").append(RecipeWriter.getPlanks(wood)).append("\"");
        }
        builder.append("\n  ]\n}\n");
        cache.put(Identifier.fromNamespaceAndPath(Constants.MODID, "tags/item/supported_planks.json"), builder.toString());
    }
}
