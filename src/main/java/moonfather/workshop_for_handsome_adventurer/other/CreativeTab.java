package moonfather.workshop_for_handsome_adventurer.other;

import moonfather.workshop_for_handsome_adventurer.dynamic_resources.SecondCreativeTab;
import moonfather.workshop_for_handsome_adventurer.initialization.ContentRegistration;
import moonfather.workshop_for_handsome_adventurer.items.task_list.RegistrationForTaskList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public class CreativeTab
{
    public static void onCreativeTabPopulation(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTab() == ContentRegistration.CREATIVE_TAB.get())
        {
            event.accept(RegistrationForTaskList.TASK_LIST.get());
            //-------------------------------------------------
            int i = 0;
            for (String woodType: ContentRegistration.woodTypes)
            {
                event.accept(ContentRegistration.items_table1.get(i).get());
                event.accept(ContentRegistration.items_table2.get(i).get());
                event.accept(ContentRegistration.items_rack1.get(i).get());
                event.accept(ContentRegistration.items_rack2.get(i).get());
                event.accept(ContentRegistration.items_rack3.get(i).get());
                event.accept(ContentRegistration.items_rack4.get(i).get());
                event.accept(ContentRegistration.items_pshelf.get(i).get());
                event.accept(ContentRegistration.items_bshelf1.get(i).get());
                event.accept(ContentRegistration.items_bshelf2.get(i).get());
                event.accept(ContentRegistration.items_bshelf3.get(i).get());
                event.accept(ContentRegistration.items_bshelf4.get(i).get());
                event.accept(ContentRegistration.items_bshelf5.get(i).get());
                event.accept(ContentRegistration.items_dshelf.get(i).get());
                i++;
            }
        }
        SecondCreativeTab.onCreativeTabPopulation(event);
    }



    public static CreativeModeTab buildTab()
    {
        return CreativeModeTab.builder()
                .icon( ()-> new ItemStack(ContentRegistration.items_table1.get(0).get()) )
                .title(Component.translatable("itemGroup.workshop_for_handsome_adventurer"))
                .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                .build();
    }
}
