package moonfather.workshop_for_handsome_adventurer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Constants.MODID)
public class ClientConfig
{
    private static final boolean defaultRenderItemsOnTable = true;
    private static final boolean defaultDetailedWailaInfoForEnchantedTools = false;
    private static final boolean defaultDetailedWailaInfoForEnchantedBooks = true;
    ///---------------------------------------------
    private static final ModConfigSpec.Builder BUILDER;
    static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue renderItemsOnTable_internal, detailedWailaInfoForEnchantedTools_internal, detailedWailaInfoForEnchantedBooks_internal;
    private static final ModConfigSpec.BooleanValue ownWorldTooltipForceEnabled_internal, ownWorldTooltipForceDisabled_internal;
    public static boolean renderItemsOnTable, detailedWailaInfoForEnchantedTools, detailedWailaInfoForEnchantedBooks;
    public static boolean ownWorldTooltipForceEnabled, ownWorldTooltipForceDisabled;

    private static final ModConfigSpec.BooleanValue taskListColoringForFinishedItems_internal, taskListPausesSingleplayer_internal, taskListItemsAreDrawnOnWall_internal;
    public static boolean taskListColoringForFinishedItems, taskListPausesSingleplayer, taskListItemsAreDrawnOnWall;
 

    static //constructor
    {
        BUILDER = new ModConfigSpec.Builder();
        BUILDER.push("Tables");
            renderItemsOnTable_internal = BUILDER
                    .comment("Crafting tables can permanently hold items if you put a chest into a customization slot. Here you set whether the items are rendered.")
                    .define("Render items on top of tables", defaultRenderItemsOnTable);
        BUILDER.pop();
        BUILDER.push("Book shelves");
            detailedWailaInfoForEnchantedBooks_internal = BUILDER
                    .comment("If this option is turned on, you'll see a list of enchantments for your books.")
                    .define("Detailed info for enchanted books in Jade/TOP/WTHIT", defaultDetailedWailaInfoForEnchantedBooks);
        BUILDER.pop();
        BUILDER.push("Tool racks");
            detailedWailaInfoForEnchantedTools_internal = BUILDER
                    .comment("If this option is turned on, you'll see a list of enchantments for your tools.")
                    .define("Detailed info for enchanted tools in Jade/TOP/WTHIT", defaultDetailedWailaInfoForEnchantedTools);
        BUILDER.pop();
        BUILDER.push("Our in-world tooltips");
            ownWorldTooltipForceEnabled_internal = BUILDER
                    .gameRestart()
                    .comment("We have a system that tells you what's under crosshair in tool rack / potion shelf / book shelf. It's disabled by default if mod pack has Jade/TOP/WTHIT, and it's enabled if none of the three are there. If you enable this, the system will be enabled even if you have Jade/TOP/WTHIT. ")
                    .define("Our world tooltip - force enabled", false);
            ownWorldTooltipForceDisabled_internal = BUILDER
                    .gameRestart()
                    .comment("We have a system that tells you what's under crosshair in tool rack / potion shelf / book shelf. It's disabled by default if mod pack has Jade/TOP/WTHIT, and it's enabled if none of the three are there. If you enable this, the system will be disabled even if you don't have Jade/TOP/WTHIT. ")
                    .define("Our world tooltip - force disabled", false);
        BUILDER.pop();
        BUILDER.push("Task list");
            taskListColoringForFinishedItems_internal = BUILDER
                .comment("Should we gray-out done and abandoned items?")
                .define("Coloring for finished items", true);
			taskListPausesSingleplayer_internal = BUILDER
                .comment("Is the game paused while the list is open?")
                .define("Task list pauses singleplayer", false);
			taskListItemsAreDrawnOnWall_internal = BUILDER
                .comment("Are item texts are checkmarks drawn on the task list block (when it's hanging on a wall)? Default is true (Bibliocraft style) - text is visible and checkmarks and paging work. Alternatively (simple mode) - right-clicking just opens the gui.  Even though this is true by default, the Author plays with it turned off and recommends that you try with it turned off and then make a decision.")
                .gameRestart()
                .define("Items are drawn on wall", true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    /////////////////////////////////////////////////////
	
    @SubscribeEvent
    static void onLoad(final ModConfigEvent.Loading event)
    {
        if (event.getConfig().getSpec().equals(SPEC))
        {
            reloadInternal();
        }
    }

    @SubscribeEvent
    static void onLoad2(final ModConfigEvent.Reloading event)
    {
        if (event.getConfig().getSpec().equals(SPEC))
        {
            reloadInternal();
        }
    }

    private static void reloadInternal()
    {
        taskListColoringForFinishedItems = taskListColoringForFinishedItems_internal.get();
        taskListPausesSingleplayer = taskListPausesSingleplayer_internal.get();
        taskListItemsAreDrawnOnWall = taskListItemsAreDrawnOnWall_internal.get();

        renderItemsOnTable = renderItemsOnTable_internal.get();
        detailedWailaInfoForEnchantedTools = detailedWailaInfoForEnchantedTools_internal.get();
        detailedWailaInfoForEnchantedBooks = detailedWailaInfoForEnchantedBooks_internal.get();
        ownWorldTooltipForceEnabled = ownWorldTooltipForceEnabled_internal.get();
        ownWorldTooltipForceDisabled = ownWorldTooltipForceDisabled_internal.get();
    }
}
