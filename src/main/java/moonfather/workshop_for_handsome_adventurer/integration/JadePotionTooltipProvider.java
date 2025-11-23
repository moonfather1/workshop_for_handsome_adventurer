package moonfather.workshop_for_handsome_adventurer.integration;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.PotionShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.PotionShelf;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.JadeUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JadePotionTooltipProvider implements IBlockComponentProvider
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
                Optional<List<Integer>> serverData = JadePotionDataProvider.getInstance().decodeFromData(accessor);
                if (serverData.isPresent())
                {
                    count = serverData.get().get(slot);
                }
                else
                {
                    count = shelf.GetRemainingItems(slot);
                }
                List<LayoutElement> list = new ArrayList<>(2);
                list.add(JadeUI.text(Component.literal(" " + count + "x  ")));
                list.add(JadeUI.text(shelf.GetItem(slot).getDisplayName()));
                tooltip.add(list);
            }
        }
    }



    @Override
    public ResourceLocation getUid() {
        return this.pluginId;
    }
    private final ResourceLocation pluginId = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "jade_plugin1");
}
