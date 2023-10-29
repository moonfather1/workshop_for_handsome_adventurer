package moonfather.workshop_for_handsome_adventurer.integration;

import mcp.mobius.waila.api.*;
import moonfather.workshop_for_handsome_adventurer.block_entities.PotionShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.BookShelf;
import moonfather.workshop_for_handsome_adventurer.blocks.PotionShelf;
import moonfather.workshop_for_handsome_adventurer.blocks.ToolRack;

@WailaPlugin
public class JadePlugin implements IWailaPlugin
{
    @Override
    public void register(IWailaCommonRegistration registration)
    {
        registration.registerBlockDataProvider(JadePotionTooltipProvider.getInstance(), PotionShelfBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration)
    {
        registration.registerComponentProvider(JadePotionTooltipProvider.getInstance(), TooltipPosition.BODY, PotionShelf.class);
        registration.registerComponentProvider(JadeBookTooltipProvider.getInstance(), TooltipPosition.BODY, BookShelf.class);
        registration.registerComponentProvider(JadeToolTooltipProvider.getInstance(), TooltipPosition.BODY, ToolRack.class);
    }
}
