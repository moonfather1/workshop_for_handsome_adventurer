package moonfather.workshop_for_handsome_adventurer.integration;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WailaBaseProvider
{
    protected List<Component> getEnchantmentParts(ItemStack item)
    {
        List<Component> cached = enchantmentCache.getOrDefault(item.hashCode(), null);
        if (cached != null)
        {
            return cached;
        }
        if (! item.is(Items.ENCHANTED_BOOK) && ! item.isEnchanted())
        {
            return null;
        }
        if (enchantmentCache.size() > 100)
        {
            enchantmentCache.clear();
        }
        List<Component> result = new ArrayList<>();
        ItemEnchantments enchantments = item.get(DataComponents.STORED_ENCHANTMENTS);
        if (enchantments != null)
        {
            enchantments.addToTooltip(Item.TooltipContext.of((HolderLookup.Provider) null), result::add, TooltipFlag.NORMAL, null);
        }
        else
        {
            enchantments = item.get(DataComponents.ENCHANTMENTS);
            if (enchantments != null)
            {
                enchantments.addToTooltip(Item.TooltipContext.of((HolderLookup.Provider) null), result::add, TooltipFlag.NORMAL, null);
            }
        }
        enchantmentCache.put(item.hashCode(), result);
        return result;
    }
    private final HashMap<Integer, List<Component>> enchantmentCache = new HashMap<>(50);
}
