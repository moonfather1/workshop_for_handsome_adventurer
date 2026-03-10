package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.config.DynamicAssetClientConfig;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.config.DynamicAssetCommonConfig;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

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
    public static final List<Item> items_dshelf = new ArrayList<>();
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
                                 .withTabsBefore(ResourceLocation.fromNamespaceAndPath(Constants.MODID,"tab"))
                                 .build();
        }
        return tab;
    }
    private static CreativeModeTab tab = null;

    //////////////////////////////////////////////////////

    public static boolean usingSecondTab()
    {
        return SecondCreativeTab.items_table1.size() >= DynamicAssetClientConfig.MinimumNumberOfSetsForSeparateCreativeTab.getAsInt();
    }

    //////////////////////////////////////////////////////

    public static void onCreativeTabPopulation(BuildCreativeModeTabContentsEvent event)
    {
        if (DynamicAssetCommonConfig.masterLeverOn() && usingSecondTab() && event.getTab().equals(SecondCreativeTab.getTab()))
        {
            int i = 0;
            for (Item item : SecondCreativeTab.items_table1)
            {
                event.accept(SecondCreativeTab.items_table1.get(i));
                event.accept(SecondCreativeTab.items_table2.get(i));
                event.accept(SecondCreativeTab.items_rack1.get(i));
                event.accept(SecondCreativeTab.items_rack2.get(i));
                event.accept(SecondCreativeTab.items_rack3.get(i));
                event.accept(SecondCreativeTab.items_rack4.get(i));
                event.accept(SecondCreativeTab.items_pshelf.get(i));
                event.accept(SecondCreativeTab.items_bshelf1.get(i));
                event.accept(SecondCreativeTab.items_bshelf2.get(i));
                event.accept(SecondCreativeTab.items_bshelf3.get(i));
                event.accept(SecondCreativeTab.items_bshelf4.get(i));
                event.accept(SecondCreativeTab.items_bshelf5.get(i));
                event.accept(SecondCreativeTab.items_dshelf.get(i));
                i++;
            }
        }
        if (event.getTab() == Registration.CREATIVE_TAB.get())
        {
            if (DynamicAssetCommonConfig.masterLeverOn() && ! (SecondCreativeTab.items_table1.size() >= DynamicAssetClientConfig.MinimumNumberOfSetsForSeparateCreativeTab.getAsInt()))
            {
                int i = 0;
                for (Item item : SecondCreativeTab.items_table1)
                {
                    event.accept(SecondCreativeTab.items_table1.get(i));
                    event.accept(SecondCreativeTab.items_table2.get(i));
                    event.accept(SecondCreativeTab.items_rack1.get(i));
                    event.accept(SecondCreativeTab.items_rack2.get(i));
                    event.accept(SecondCreativeTab.items_rack3.get(i));
                    event.accept(SecondCreativeTab.items_rack4.get(i));
                    event.accept(SecondCreativeTab.items_pshelf.get(i));
                    event.accept(SecondCreativeTab.items_bshelf1.get(i));
                    event.accept(SecondCreativeTab.items_bshelf2.get(i));
                    event.accept(SecondCreativeTab.items_bshelf3.get(i));
                    event.accept(SecondCreativeTab.items_bshelf4.get(i));
                    event.accept(SecondCreativeTab.items_bshelf5.get(i));
                    event.accept(SecondCreativeTab.items_dshelf.get(i));
                    i++;
                }
            }
        }
    }
}
