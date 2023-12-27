package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SecondCreativeTab
{
    public static final List<Item> items_table1 = new ArrayList<>();
    public static final List<Item> items_table2 = new ArrayList<>(); // because of sorting in creative tabs, we can't just dump into one list
    public static final List<Item> items_rack1 = new ArrayList<>();
    public static final List<Item> items_rack2 = new ArrayList<>();
    public static final List<Item> items_rack3 = new ArrayList<>();
    public static final List<Item> items_rack4 = new ArrayList<>();
    public static final List<Item> items_pshelf = new ArrayList<>();
    public static final List<Item> items_bshelf1 = new ArrayList<>();
    public static final List<Item> items_bshelf2 = new ArrayList<>();
    public static final List<Item> items_bshelf3 = new ArrayList<>();
    public static final List<Item> items_bshelf4 = new ArrayList<>();
    public static final List<Item> items_bshelf5 = new ArrayList<>();

    public static CreativeModeTab getTab()
    {
        if (tab == null)
        {
            int iconIndex = (new Random()).nextInt(SecondCreativeTab.items_table1.size());
            tab = CreativeModeTab.builder()
                                 .icon( ()-> new ItemStack(SecondCreativeTab.items_table1.get(iconIndex)) )
                                 .title(Component.translatable("itemGroup.workshop_for_handsome_adventurer"))
                                 .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                                 .build();
        }
        return tab;
    }
    private static CreativeModeTab tab = null;
}
