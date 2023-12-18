package moonfather.workshop_for_handsome_adventurer.other;

import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public class CreativeTab
{
    public static void OnCreativeTabPopulation(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTab() == Registration.CREATIVE_TAB.get())
        {
            int i = 0;
            for (String woodType: Registration.woodTypes) {
                event.accept(Registration.items_table1.get(i).get());
                event.accept(Registration.items_table2.get(i).get());
                event.accept(Registration.items_rack1.get(i).get());
                event.accept(Registration.items_rack2.get(i).get());
                event.accept(Registration.items_rack3.get(i).get());
                event.accept(Registration.items_rack4.get(i).get());
                event.accept(Registration.items_pshelf.get(i).get());
                event.accept(Registration.items_bshelf1.get(i).get());
                event.accept(Registration.items_bshelf2.get(i).get());
                event.accept(Registration.items_bshelf3.get(i).get());
                event.accept(Registration.items_bshelf4.get(i).get());
                event.accept(Registration.items_bshelf5.get(i).get());
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
