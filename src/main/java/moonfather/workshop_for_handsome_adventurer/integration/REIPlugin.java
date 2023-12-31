package moonfather.workshop_for_handsome_adventurer.integration;

import moonfather.workshop_for_handsome_adventurer.block_entities.screens.SimpleTableCraftingScreen;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.forge.REIPluginClient;

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
        zones.register(SimpleTableCraftingScreen.class, screen ->
        {
            Collection<Rectangle> result = new HashSet<>();
            result.add(new me.shedaniel.math.Rectangle(screen.getGuiLeft(), screen.getGuiTop(), screen.getXSize(), screen.getYSize()));
            return result;
        });
    }
}
