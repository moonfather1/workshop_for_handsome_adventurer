package moonfather.workshop_for_handsome_adventurer.integration;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.PotionShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.DiscShelf;
import moonfather.workshop_for_handsome_adventurer.blocks.PotionShelf;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

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
            int slot;
            if (shelf.getNumberOfItems() != 9)
            {
                slot = PotionShelf.getPotionShelfSlot(accessor.getHitResult());
            }
            else
            {
                slot = DiscShelf.getDiscShelfSlot(accessor.getHitResult());
            }            if (! shelf.GetItem(slot).isEmpty())
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
                list.add(IElementHelper.get().text(Component.literal(" " + count + "x  ")));
                list.add(IElementHelper.get().text(shelf.GetItem(slot).getHoverName()));
                tooltip.add(list);
                JukeboxPlayable songContainer = shelf.GetItem(slot).get(DataComponents.JUKEBOX_PLAYABLE);
                if (songContainer != null)
                {
                    List<IElement> list2 = new ArrayList<>(2);
                    list2.add(IElementHelper.get().text(Component.literal(" ")));
                    EitherHolder<JukeboxSong> song = songContainer.song();
                    song.unwrap(accessor.getLevel().registryAccess()).ifPresent(holder -> list2.add(IElementHelper.get().text(holder.value().description())));
                    tooltip.add(list2);
                }
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
    private final ResourceLocation pluginId = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "jade_plugin1");
}
