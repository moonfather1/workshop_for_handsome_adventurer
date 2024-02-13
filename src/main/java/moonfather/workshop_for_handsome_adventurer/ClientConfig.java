package moonfather.workshop_for_handsome_adventurer;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig
{
    private static final boolean defaultRenderItemsOnTable = true;
    private static final boolean defaultDetailedWailaInfoForEnchantedTools = false;
    private static final boolean defaultDetailedWailaInfoForEnchantedBooks = true;
    ///---------------------------------------------
    private static final ModConfigSpec.Builder BUILDER;
    public static final ModConfigSpec.BooleanValue RenderItemsOnTable;
    public static final ModConfigSpec.BooleanValue DetailedWailaInfoForEnchantedTools;
    public static final ModConfigSpec.BooleanValue DetailedWailaInfoForEnchantedBooks;
    static final ModConfigSpec SPEC;



    static //constructor
    {
        BUILDER = new ModConfigSpec.Builder();
        BUILDER.push("Tables");
            RenderItemsOnTable = BUILDER
                    .comment("Crafting tables can permanently hold items if you put a chest into a customization slot. Here you set whether the items are rendered.")
                    .define("Render items on top of tables", defaultRenderItemsOnTable);
        BUILDER.pop();
        BUILDER.push("Book shelves");
            DetailedWailaInfoForEnchantedBooks = BUILDER
                    .comment("If this option is turned on, you'll see a list of enchantments for your books.")
                    .define("Detailed info for enchanted books in Jade/TOP/WTHIT", defaultDetailedWailaInfoForEnchantedBooks);
        BUILDER.pop();
        BUILDER.push("Tool racks");
            DetailedWailaInfoForEnchantedTools = BUILDER
                    .comment("If this option is turned on, you'll see a list of enchantments for your tools.")
                    .define("Detailed info for enchanted tools in Jade/TOP/WTHIT", defaultDetailedWailaInfoForEnchantedTools);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
