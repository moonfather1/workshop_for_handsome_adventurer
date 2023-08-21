package moonfather.workshop_for_handsome_adventurer.integration;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.PotionShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.PotionShelf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;

import java.util.ArrayList;
import java.util.List;

public class JadePotionTooltipProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor>
{
    private static final JadePotionTooltipProvider instance = new JadePotionTooltipProvider();
    public static JadePotionTooltipProvider getInstance() { return instance; }


    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config)
    {
        if (accessor.getBlockEntity() instanceof PotionShelfBlockEntity shelf)
        {
            int slot = PotionShelf.getPotionShelfSlot(accessor.getHitResult());
            if (! shelf.GetItem(slot).isEmpty())
            {
                int count;
                if (accessor.getServerData().contains("Bottles" + slot))
                {
                    count = accessor.getServerData().getInt("Bottles" + slot);
                }
                else
                {
                    count = shelf.GetRemainingItems(slot);
                }
                List<IElement> list = new ArrayList<>(2);
                list.add(tooltip.getElementHelper().text(Component.literal(" " + count + "x  ")));
                list.add(tooltip.getElementHelper().text(shelf.GetItem(slot).getDisplayName()));
                tooltip.add(list);
            }
        }
    }



    @Override
    public void appendServerData(CompoundTag data, BlockAccessor blockAccessor) {
        for (int i = 0; i < 6; i++)
        {
            int bottles = ((PotionShelfBlockEntity)blockAccessor.getBlockEntity()).GetRemainingItems(i);
            data.putInt("Bottles" + i, bottles);
        }
    }



    @Override
    public ResourceLocation getUid() {
        return this.pluginId;
    }
    private final ResourceLocation pluginId = new ResourceLocation(Constants.MODID, "jade_plugin1");
}
