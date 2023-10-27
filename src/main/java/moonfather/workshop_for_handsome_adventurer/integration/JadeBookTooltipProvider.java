package moonfather.workshop_for_handsome_adventurer.integration;


import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.OptionsHolder;
import moonfather.workshop_for_handsome_adventurer.block_entities.BookShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.BookShelf;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;

import java.util.ArrayList;
import java.util.List;


public class JadeBookTooltipProvider extends JadeBaseTooltipProvider implements IBlockComponentProvider
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



    @Override
    public ResourceLocation getUid() {  return this.pluginId;  }
    private final ResourceLocation pluginId = new ResourceLocation(Constants.MODID, "jade_plugin2");
}