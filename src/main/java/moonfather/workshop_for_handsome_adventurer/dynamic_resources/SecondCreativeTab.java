package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SecondCreativeTab
{
    public static final List<Item> items_table1 = new ArrayList<>();

    public static final CreativeModeTab TAB_DYNAMIC = new CreativeModeTab(Constants.MODID)
    {
        public ItemStack makeIcon()
        {
            int iconIndex = (new Random()).nextInt(SecondCreativeTab.items_table1.size());
            return new ItemStack(SecondCreativeTab.items_table1.get(iconIndex));
        }
    };
}
