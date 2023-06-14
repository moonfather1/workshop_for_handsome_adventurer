package moonfather.workshop_for_handsome_adventurer.other;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;

public class CreativeTab
{
    public static void OnCreativeTabPopulation(CreativeModeTabEvent.BuildContents event)
    {
        if (event.getTab() == TAB_WORKSHOP)
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

    public static void OnCreativeTabRegistration(CreativeModeTabEvent.Register event) {
        TAB_WORKSHOP = event.registerCreativeModeTab(tab, builder -> builder
                .icon( ()-> new ItemStack(Registration.SIMPLE_TABLE_OAK.get()) )
                .title(Component.translatable("itemGroup.workshop_for_handsome_adventurer"))
        );
    }

    private static final ResourceLocation tab = new ResourceLocation(Constants.MODID, "tab");
    public static CreativeModeTab TAB_WORKSHOP;
}
