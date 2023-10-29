package moonfather.workshop_for_handsome_adventurer.integration;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElement;
import mcp.mobius.waila.impl.ui.ItemStackElement;
import moonfather.workshop_for_handsome_adventurer.OptionsHolder;
import moonfather.workshop_for_handsome_adventurer.block_entities.BookShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.BookShelf;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;


public class JadeBookTooltipProvider extends JadeBaseTooltipProvider implements IComponentProvider
{
    private static final JadeBookTooltipProvider instance = new JadeBookTooltipProvider();
    public static JadeBookTooltipProvider getInstance() { return instance; }


    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config)
    {
        if (accessor.getBlockEntity() instanceof BookShelfBlockEntity shelf)
        {
            int slot = BookShelf.getBookShelfSlot((BookShelf) accessor.getBlock(), accessor.getHitResult());
            if (slot >= 0 && ! shelf.GetItem(slot).isEmpty())
            {
                this.appendTooltipInternal(tooltip, shelf.GetItem(slot));
            }
        }
    }



    @Override
    protected ForgeConfigSpec.ConfigValue<Boolean> getOption()
    {
        return OptionsHolder.CLIENT.DetailedWailaInfoForEnchantedBooks;
    }
}
