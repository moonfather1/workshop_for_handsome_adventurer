package moonfather.workshop_for_handsome_adventurer.other;

import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

public class CreativeTab
{
    public static void OnCreativeTabPopulation(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTab() == Registration.CREATIVE_TAB.get())
        {
            event.accept(Registration.SIMPLE_TABLE_ITEM_OAK);
            event.accept(Registration.DUAL_TABLE_PLACER_ITEM_OAK);
            event.accept(Registration.MINI_TOOL_RACK_ITEM_OAK);
            event.accept(Registration.FRAMED_TOOL_RACK_ITEM_OAK);
            event.accept(Registration.PFRAMED_TOOL_RACK_ITEM_OAK);
            event.accept(Registration.DUAL_TOOL_RACK_ITEM_OAK);
            event.accept(Registration.POTION_SHELF_ITEM_OAK);

            event.accept(Registration.SIMPLE_TABLE_ITEM_SPRUCE);
            event.accept(Registration.DUAL_TABLE_PLACER_ITEM_SPRUCE);
            event.accept(Registration.MINI_TOOL_RACK_ITEM_SPRUCE);
            event.accept(Registration.FRAMED_TOOL_RACK_ITEM_SPRUCE);
            event.accept(Registration.PFRAMED_TOOL_RACK_ITEM_SPRUCE);
            event.accept(Registration.DUAL_TOOL_RACK_ITEM_SPRUCE);
            event.accept(Registration.POTION_SHELF_ITEM_SPRUCE);

            event.accept(Registration.SIMPLE_TABLE_ITEM_BIRCH);
            event.accept(Registration.DUAL_TABLE_PLACER_ITEM_BIRCH);
            event.accept(Registration.MINI_TOOL_RACK_ITEM_BIRCH);
            event.accept(Registration.FRAMED_TOOL_RACK_ITEM_BIRCH);
            event.accept(Registration.PFRAMED_TOOL_RACK_ITEM_BIRCH);
            event.accept(Registration.DUAL_TOOL_RACK_ITEM_BIRCH);
            event.accept(Registration.POTION_SHELF_ITEM_BIRCH);

            event.accept(Registration.SIMPLE_TABLE_ITEM_JUNGLE);
            event.accept(Registration.DUAL_TABLE_PLACER_ITEM_JUNGLE);
            event.accept(Registration.MINI_TOOL_RACK_ITEM_JUNGLE);
            event.accept(Registration.FRAMED_TOOL_RACK_ITEM_JUNGLE);
            event.accept(Registration.PFRAMED_TOOL_RACK_ITEM_JUNGLE);
            event.accept(Registration.DUAL_TOOL_RACK_ITEM_JUNGLE);
            event.accept(Registration.POTION_SHELF_ITEM_JUNGLE);

            event.accept(Registration.SIMPLE_TABLE_ITEM_DARK_OAK);
            event.accept(Registration.DUAL_TABLE_PLACER_ITEM_DARK_OAK);
            event.accept(Registration.MINI_TOOL_RACK_ITEM_DARK_OAK);
            event.accept(Registration.FRAMED_TOOL_RACK_ITEM_DARK_OAK);
            event.accept(Registration.PFRAMED_TOOL_RACK_ITEM_DARK_OAK);
            event.accept(Registration.DUAL_TOOL_RACK_ITEM_DARK_OAK);
            event.accept(Registration.POTION_SHELF_ITEM_DARK_OAK);

            event.accept(Registration.SIMPLE_TABLE_ITEM_MANGROVE);
            event.accept(Registration.DUAL_TABLE_PLACER_ITEM_MANGROVE);
            event.accept(Registration.MINI_TOOL_RACK_ITEM_MANGROVE);
            event.accept(Registration.FRAMED_TOOL_RACK_ITEM_MANGROVE);
            event.accept(Registration.PFRAMED_TOOL_RACK_ITEM_MANGROVE);
            event.accept(Registration.DUAL_TOOL_RACK_ITEM_MANGROVE);
            event.accept(Registration.POTION_SHELF_ITEM_MANGROVE);
        }
    }



    public static CreativeModeTab buildTab()
    {
        return CreativeModeTab.builder()
                .icon( ()-> new ItemStack(Registration.SIMPLE_TABLE_OAK.get()) )
                .title(Component.translatable("itemGroup.workshop_for_handsome_adventurer"))
                .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                .build();
    }
}
