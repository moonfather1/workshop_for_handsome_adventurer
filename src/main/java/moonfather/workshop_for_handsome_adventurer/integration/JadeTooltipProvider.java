package moonfather.workshop_for_handsome_adventurer.integration;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElement;
import moonfather.workshop_for_handsome_adventurer.block_entities.PotionShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.PotionShelf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public class JadeTooltipProvider implements IComponentProvider, IServerDataProvider<BlockEntity>
{
    private static final JadeTooltipProvider instance = new JadeTooltipProvider();
    public static JadeTooltipProvider getInstance() { return instance; }


    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config)
    {
        if (accessor.getBlockEntity() instanceof PotionShelfBlockEntity shelf)
        {
            int slot = PotionShelf.getTargetedSlot(accessor.getHitResult());
            if (! shelf.GetItem(slot).isEmpty())
            {
                if (accessor.getServerData().contains("Bottles" + slot))
                {
                    int count = accessor.getServerData().getInt("Bottles" + slot);
                    List<IElement> list = new ArrayList<>(2);
                    list.add(tooltip.getElementHelper().text(new TextComponent(" " + shelf.GetRemainingItems(slot) + "x  ")));
                    list.add(tooltip.getElementHelper().text(shelf.GetItem(slot).getDisplayName()));
                    tooltip.add(list);
                }
            }
        }
    }


    @Override
    public void appendServerData(CompoundTag data, ServerPlayer player, Level world, BlockEntity t, boolean showDetails)
    {
        for (int i = 0; i < 6; i++)
        {
            int bottles = ((PotionShelfBlockEntity)t).GetRemainingItems(i);
            data.putInt("Bottles" + i, bottles);
        }
    }
}
