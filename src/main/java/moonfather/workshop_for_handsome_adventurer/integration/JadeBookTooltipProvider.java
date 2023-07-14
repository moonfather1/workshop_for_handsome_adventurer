package moonfather.workshop_for_handsome_adventurer.integration;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElement;
import mcp.mobius.waila.impl.ui.ItemStackElement;
import moonfather.workshop_for_handsome_adventurer.block_entities.BookShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.BookShelf;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;


public class JadeBookTooltipProvider implements IComponentProvider
{
    private static final JadeBookTooltipProvider instance = new JadeBookTooltipProvider();
    public static JadeBookTooltipProvider getInstance() { return instance; }


    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config)
    {
        if (accessor.getBlockEntity() instanceof BookShelfBlockEntity shelf)
        {
            int slot = BookShelf.getBookShelfSlot((BookShelf) accessor.getBlock(), accessor.getHitResult());
            if (! shelf.GetItem(slot).isEmpty())
            {
                List<IElement> list = new ArrayList<>(3);
                list.add(tooltip.getElementHelper().item(shelf.GetItem(slot)));
                list.add(tooltip.getElementHelper().spacer(4, 12));
                list.add(tooltip.getElementHelper().text(shelf.GetItem(slot).getHoverName()));
                tooltip.add(list);
            }
        }
    }
}
