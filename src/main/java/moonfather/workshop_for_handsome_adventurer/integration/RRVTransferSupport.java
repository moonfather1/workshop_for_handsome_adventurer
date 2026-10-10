package moonfather.workshop_for_handsome_adventurer.integration;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.client.ExclusionAreaManager;
import cc.cassian.rrv.api.event.OverlayManagementEvents;
import cc.cassian.rrv.common.overlay.BlockingGuiComponent;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.screens.DualTableCraftingScreen;
import moonfather.workshop_for_handsome_adventurer.block_entities.screens.SimpleTableCraftingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;

public class RRVTransferSupport implements ReliableRecipeViewerClientPlugin
{
    @Override
    public void onIntegrationInitialize()
    {
        OverlayManagementEvents.registerExclusionArea(RRVTransferSupport::exclusionZoneEvent);
    }

    private static void exclusionZoneEvent(Screen screen, ExclusionAreaManager exclusionAreaManager, float v)
    {
        if (screen instanceof DualTableCraftingScreen screen2)
        {
            exclusionAreaManager.setExclusionArea(new BlockingGuiComponent(ID_DUAL, screen2.getLeftPos(), screen2.getTopPos(), screen2.getXSize(), screen2.getImageHeight()));
        }
        else if (screen instanceof SimpleTableCraftingScreen screen1)
        {
            exclusionAreaManager.setExclusionArea(new BlockingGuiComponent(ID_SOLO, screen1.getLeftPos(), screen1.getTopPos(), screen1.getXSize(), screen1.getImageHeight()));
        }
    }
    private static final Identifier ID_DUAL = Identifier.fromNamespaceAndPath(Constants.MODID, "table_dual");
    private static final Identifier ID_SOLO = Identifier.fromNamespaceAndPath(Constants.MODID, "table_solo");
}