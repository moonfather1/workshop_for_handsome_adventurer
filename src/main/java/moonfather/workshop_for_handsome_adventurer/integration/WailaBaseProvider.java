package moonfather.workshop_for_handsome_adventurer.integration;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(item);
        for (var e : map.entrySet())
        {
            result.add(new TranslatableComponent(e.getKey().getDescriptionId()).withStyle(ChatFormatting.DARK_GRAY));
            int level = e.getValue();
            if (level < roman.length)
            {
                result.add(roman[level]);
            }
            else
            {
                result.add(new TextComponent(" " + level).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
        enchantmentCache.put(item.hashCode(), result);
        return result;
    }
    private static final Component[] roman = {new TextComponent(""), new TextComponent(""),
                                              new TextComponent(" II").withStyle(ChatFormatting.DARK_GRAY),
                                              new TextComponent(" III").withStyle(ChatFormatting.DARK_GRAY),
                                              new TextComponent(" IV").withStyle(ChatFormatting.DARK_GRAY),
                                              new TextComponent(" V").withStyle(ChatFormatting.DARK_GRAY),
                                              new TextComponent(" VI").withStyle(ChatFormatting.DARK_GRAY) };
    private final HashMap<Integer, List<Component>> enchantmentCache = new HashMap<>(50);
}
