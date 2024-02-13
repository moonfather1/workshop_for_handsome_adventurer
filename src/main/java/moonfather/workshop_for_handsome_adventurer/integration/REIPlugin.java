package moonfather.workshop_for_handsome_adventurer.integration;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.forge.REIPluginClient;
import moonfather.workshop_for_handsome_adventurer.block_entities.screens.DualTableCraftingScreen;
import moonfather.workshop_for_handsome_adventurer.block_entities.screens.SimpleTableCraftingScreen;

import java.util.Collection;
import java.util.HashSet;

@REIPluginClient
public class REIPlugin implements REIClientPlugin
{
    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry)
    {
        registry.register(new REITransferHandlerForDualTable());
        registry.register(new REITransferHandlerForSmallTable());
    }

    @Override
    public void registerExclusionZones(ExclusionZones zones)
    {
        zones.register(DualTableCraftingScreen.class, screen ->
        {
            Collection<me.shedaniel.math.Rectangle> result = new HashSet<>();
            result.add(new me.shedaniel.math.Rectangle(screen.getGuiLeft(), screen.getGuiTop(), screen.getXSize(), screen.getYSize()));
            return result;
        });
        zones.register(SimpleTableCraftingScreen.class, screen ->
        {
            Collection<me.shedaniel.math.Rectangle> result = new HashSet<>();
            result.add(new me.shedaniel.math.Rectangle(screen.getGuiLeft(), screen.getGuiTop(), screen.getXSize(), screen.getYSize()));
            return result;
        });
    }
}
