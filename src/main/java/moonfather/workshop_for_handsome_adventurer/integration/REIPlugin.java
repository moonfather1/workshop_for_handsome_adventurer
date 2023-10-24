package moonfather.workshop_for_handsome_adventurer.integration;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public class REIPlugin implements REIClientPlugin
{
    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry)
    {
        registry.register(new REITransferHandlerForDualTable());
        registry.register(new REITransferHandlerForSmallTable());
    }
}
