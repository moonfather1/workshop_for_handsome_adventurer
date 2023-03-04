package moonfather.workshop_for_handsome_adventurer.integration;

import mcp.mobius.waila.api.*;
import moonfather.workshop_for_handsome_adventurer.block_entities.PotionShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.PotionShelf;

@WailaPlugin
public class JadePlugin implements IWailaPlugin
{
    @Override
    public void register(IWailaCommonRegistration registration)
    {
        registration.registerBlockDataProvider(JadeTooltipProvider.getInstance(), PotionShelfBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration)
    {
        registration.registerComponentProvider(JadeTooltipProvider.getInstance(), TooltipPosition.BODY, PotionShelf.class);
    }
}
