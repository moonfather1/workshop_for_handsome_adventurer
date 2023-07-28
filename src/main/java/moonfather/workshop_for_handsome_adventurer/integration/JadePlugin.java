package moonfather.workshop_for_handsome_adventurer.integration;

import moonfather.workshop_for_handsome_adventurer.block_entities.PotionShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.BookShelf;
import moonfather.workshop_for_handsome_adventurer.blocks.PotionShelf;
import snownee.jade.api.*;

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
        registration.registerBlockComponent(JadePotionTooltipProvider.getInstance(), PotionShelf.class);
        registration.registerBlockComponent(JadeBookTooltipProvider.getInstance(), BookShelf.class);
    }
}
