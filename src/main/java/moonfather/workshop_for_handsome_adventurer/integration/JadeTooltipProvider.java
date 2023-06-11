package moonfather.workshop_for_handsome_adventurer.integration;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.PotionShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.PotionShelf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;

import java.util.ArrayList;
import java.util.List;

public class JadeTooltipProvider implements IBlockComponentProvider, IServerDataProvider<BlockEntity>
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
                    list.add(tooltip.getElementHelper().text(Component.literal(" " + shelf.GetRemainingItems(slot) + "x  ")));
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

    @Override
    public ResourceLocation getUid() {
        return this.pluginId;
    }
    private final ResourceLocation pluginId = new ResourceLocation(Constants.MODID, "jade_plugin");
}
