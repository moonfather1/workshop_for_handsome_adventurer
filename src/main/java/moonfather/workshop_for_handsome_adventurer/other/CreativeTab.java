package moonfather.workshop_for_handsome_adventurer.other;

import moonfather.workshop_for_handsome_adventurer.dynamic_resources.DynamicAssetConfig;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.SecondCreativeTab;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

public class CreativeTab
{
    public static void OnCreativeTabPopulation(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTab() == Registration.CREATIVE_TAB.get())
        {
            int i = 0;
            for (String woodType: Registration.woodTypes) {
                event.accept(Registration.items_table1.get(i));
                event.accept(Registration.items_table2.get(i));
                event.accept(Registration.items_rack1.get(i));
                event.accept(Registration.items_rack2.get(i));
                event.accept(Registration.items_rack3.get(i));
                event.accept(Registration.items_rack4.get(i));
                event.accept(Registration.items_pshelf.get(i));
                event.accept(Registration.items_bshelf1.get(i));
                event.accept(Registration.items_bshelf2.get(i));
                event.accept(Registration.items_bshelf3.get(i));
                event.accept(Registration.items_bshelf4.get(i));
                event.accept(Registration.items_bshelf5.get(i));
                i++;
            }
            if (DynamicAssetConfig.masterLeverOn() && ! DynamicAssetConfig.separateCreativeTab())
            {
                i = 0;
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
                    i++;
                }
            }
        }
        if (DynamicAssetConfig.masterLeverOn() && DynamicAssetConfig.separateCreativeTab() && event.getTab().equals(SecondCreativeTab.getTab()))
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
                i++;
            }
        }
    }



    public static CreativeModeTab buildTab()
    {
        return CreativeModeTab.builder()
                .icon( ()-> new ItemStack(Registration.items_table1.get(0).get()) )
                .title(Component.translatable("itemGroup.workshop_for_handsome_adventurer"))
                .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                .build();
    }
}
