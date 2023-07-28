package moonfather.workshop_for_handsome_adventurer.integration;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.BookShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.BookShelf;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;

import java.util.ArrayList;
import java.util.List;

public class JadeBookTooltipProvider implements IBlockComponentProvider
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
                List<IElement> list = new ArrayList<>(3);
                list.add(tooltip.getElementHelper().item(shelf.GetItem(slot)));
                list.add(tooltip.getElementHelper().spacer(4, 12));
                list.add(tooltip.getElementHelper().text(shelf.GetItem(slot).getHoverName()));
                tooltip.add(list);
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return this.pluginId;
    }
    private final ResourceLocation pluginId = new ResourceLocation(Constants.MODID, "jade_plugin_books");
}
