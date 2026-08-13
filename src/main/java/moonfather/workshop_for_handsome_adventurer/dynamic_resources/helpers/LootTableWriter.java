package moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.AssetReader;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.WoodTypeLister;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.neoforged.fml.ModList;

import java.util.Map;

public class LootTableWriter
{
    public static void writeFiles(Map<Identifier, String> cache)
    {
        final String SPRUCE = "spruce";
        for (String file: files)
        {
            String original = AssetReader.getInstance(PackType.SERVER_DATA, Constants.MODID).getText(Identifier.fromNamespaceAndPath(Constants.MODID, file));
            // it never will be null, won't even check
            for (String wood: WoodTypeLister.getWoodIds())
            {
                cache.put(Identifier.fromNamespaceAndPath(Constants.MODID, file.replace(SPRUCE, wood)), original.replace(SPRUCE, wood));
            }
        }
        // support for addon mod
        if (ModList.get().isLoaded("tetra_tables"))
        {
            for (String wood: WoodTypeLister.getWoodIds())
            {
                cache.put(Identifier.fromNamespaceAndPath("tetra_tables", "loot_table/blocks/tetra_table_spruce.json".replace(SPRUCE, wood)), tetraTableLoot.replace(SPRUCE, wood));
            }
        }
    }

    private static final String[] files = {
            "loot_table/blocks/book_shelf_double_spruce.json",
            "loot_table/blocks/book_shelf_open_double_spruce.json",
            "loot_table/blocks/book_shelf_open_minimal_spruce.json",
            "loot_table/blocks/book_shelf_with_lanterns_spruce.json",
            "loot_table/blocks/dual_table_bottom_left_spruce.json",
            "loot_table/blocks/potion_shelf_spruce.json",
            "loot_table/blocks/disc_shelf_spruce.json",
            "loot_table/blocks/simple_table_spruce.json",
            "loot_table/blocks/tool_rack_double_spruce.json",
            "loot_table/blocks/tool_rack_framed_spruce.json",
            "loot_table/blocks/tool_rack_pframed_spruce.json",
            "loot_table/blocks/tool_rack_single_spruce.json"
    };
    private static final String tetraTableLoot = "{  \"type\": \"minecraft:block\",  \"pools\": [  {  \"rolls\": 1.0,  \"entries\": [  {  \"type\": \"minecraft:item\",  \"conditions\": [  {  \"condition\": \"minecraft:survives_explosion\"  }  ],  \"name\": \"tetra_tables:tetra_table_spruce\"  }  ]  }  ],  \"functions\": [  {  \"function\": \"minecraft:explosion_decay\"  }  ]  }";
}
