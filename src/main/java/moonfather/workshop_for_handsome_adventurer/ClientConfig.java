package moonfather.workshop_for_handsome_adventurer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Constants.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ClientConfig
{
    private static final boolean defaultRenderItemsOnTable = true;
    private static final boolean defaultDetailedWailaInfoForEnchantedTools = false;
    private static final boolean defaultDetailedWailaInfoForEnchantedBooks = true;
    ///---------------------------------------------
    private static final ModConfigSpec.Builder BUILDER;
    static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue RenderItemsOnTable, DetailedWailaInfoForEnchantedTools, DetailedWailaInfoForEnchantedBooks;
    public static final ModConfigSpec.BooleanValue OwnWorldTooltipForceEnabled, OwnWorldTooltipForceDisabled;

    private static final ModConfigSpec.BooleanValue coloringForFinishedItems_internal, taskListPausesSingleplayer_internal, itemsAreDrawnOnWall_internal;
    public static boolean coloringForFinishedItems, taskListPausesSingleplayer, itemsAreDrawnOnWall;
 

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
        BUILDER.push("Our in-world tooltips");
            OwnWorldTooltipForceEnabled = BUILDER
                    .comment("We have a system that tells you what's under crosshair in tool rack / potion shelf / book shelf. It's disabled by default if mod pack has Jade/TOP/WTHIT, and it's enabled if none of the three are there. If you enable this, the system will be enabled even if you have Jade/TOP/WTHIT. ")
                    .define("Our world tooltip - force enabled", false);
            OwnWorldTooltipForceDisabled = BUILDER
                    .comment("We have a system that tells you what's under crosshair in tool rack / potion shelf / book shelf. It's disabled by default if mod pack has Jade/TOP/WTHIT, and it's enabled if none of the three are there. If you enable this, the system will be disabled even if you don't have Jade/TOP/WTHIT. ")
                    .define("Our world tooltip - force disabled", false);
        BUILDER.pop();
        BUILDER.push("Task list");
            coloringForFinishedItems_internal = BUILDER
                .comment("Should we gray-out done and abandoned items?")
                .define("coloring for finished items", true); 
			taskListPausesSingleplayer_internal = BUILDER
                .comment("Is the game paused while the list is open?")
                .define("task list pauses singleplayer", false);
			itemsAreDrawnOnWall_internal = BUILDER
                .comment("Are item texts are checkmarks drawn on the task list block (when it's hanging on a wall)? Default is true (Bibliocraft style) - text is visible and checkmarks and paging work. Alternatively (simple mode) - right-clicking just opens the gui.  Even though this is true by default, the Author plays with it turned off and recommends that you try with it turned off and then make a decision.")
                .define("items are drawn on wall", true); 		
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
	
	
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        coloringForFinishedItems = coloringForFinishedItems_internal.get();
        taskListPausesSingleplayer = taskListPausesSingleplayer_internal.get();
        itemsAreDrawnOnWall = itemsAreDrawnOnWall_internal.get();
    }
}
