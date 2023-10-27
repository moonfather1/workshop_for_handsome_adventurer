package moonfather.workshop_for_handsome_adventurer.integration;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import snownee.jade.api.ITooltip;
import snownee.jade.api.ui.IElement;

import java.util.ArrayList;
import java.util.List;

public abstract class JadeBaseTooltipProvider extends WailaBaseProvider
{
    protected void appendTooltipInternal(ITooltip tooltip, ItemStack item)
    {
        List<IElement> list = new ArrayList<>(3);
        list.add(tooltip.getElementHelper().item(item));
        list.add(tooltip.getElementHelper().spacer(4, 12));
        list.add(tooltip.getElementHelper().text(item.getHoverName()));
        tooltip.add(list);
        if (this.optionsCacheTime <= 0)
        {
            this.optionEnchantments = getOption().get();
            this.optionsCacheTime = 35;
        }
        this.optionsCacheTime -= 1;
        if (this.optionEnchantments)
        {
            List<Component> enchantments = this.getEnchantmentParts(item);
            if (enchantments != null)
            {
                for (int i = 0; i < enchantments.size(); i += 2)
                {
                    list = new ArrayList<>();
                    list.add(tooltip.getElementHelper().spacer(4, 12));
                    list.add(tooltip.getElementHelper().text(enchantments.get(i)));
                    list.add(tooltip.getElementHelper().text(enchantments.get(i + 1)));
                    tooltip.add(list);
                }
            }
        }
    }

    protected abstract ForgeConfigSpec.ConfigValue<Boolean> getOption();

    private int optionsCacheTime = 0;
    private boolean optionEnchantments = false;
}
