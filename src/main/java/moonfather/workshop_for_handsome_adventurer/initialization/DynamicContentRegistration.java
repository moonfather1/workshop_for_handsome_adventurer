package moonfather.workshop_for_handsome_adventurer.initialization;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.blocks.*;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.MissingMappingsHandler;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.SecondCreativeTab;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.WoodTypeLister;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.config.DynamicAssetClientConfig;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.config.DynamicAssetCommonConfig;
import moonfather.workshop_for_handsome_adventurer.items.BlockItemEx;
import moonfather.workshop_for_handsome_adventurer.items.WorkstationPlacerItem;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.BaseMappedRegistry;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.List;
import java.util.function.Supplier;

public class DynamicContentRegistration
{
    //// tables/racks/shelves for 3rd party woods
    public static void handleRegistryEvent(final RegisterEvent event)
    {
        if (event.getRegistryKey().equals(Registries.ITEM))  // Registries.BLOCK is too early
        {
            if (DynamicAssetCommonConfig.masterLeverOn())
            {
                DynamicContentRegistration.registerBlocksForThirdPartyWood(event);
            }
            MissingMappingsHandler.prepareMappings();
            MissingMappingsHandler.storeForNextTime();
        }
        if (event.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB))
        {
            if (DynamicAssetCommonConfig.masterLeverOn() && SecondCreativeTab.usingSecondTab())
            {
                event.register(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(Constants.MODID, "tab2"), SecondCreativeTab::getTab);
            }
        }
    }



    private static void registerSinglePrimaryBlockForThirdPartyWood(Block block, String prefix, String wood, List<Supplier<Block>> listForBlockEntities, List<Item> listForCreativeTab)
    {
        Item item = new BlockItemEx(block, new Item.Properties());
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Constants.MODID, prefix + wood);
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        Registry.register(BuiltInRegistries.ITEM, id, item);
        listForCreativeTab.add(item);
        if (listForBlockEntities != null)
        {
            listForBlockEntities.add(() -> block); // for the block entity
        }
    }

    private static void registerSingleSupportBlockForThirdPartyWood(Block block, String prefix, String wood)
    {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Constants.MODID, prefix + wood);
        Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    private static void registerBlocksForThirdPartyWood(RegisterEvent event)
    {
        try  // because of unfreeze fuckery
        {
            boolean wasFrozen = ((MappedRegistry<Block>) BuiltInRegistries.BLOCK).frozen;
            ((MappedRegistry<Block>) BuiltInRegistries.BLOCK).unfreeze();
            for (String wood : WoodTypeLister.getWoodIds())
            {
                // can't just add wood types to Registration.woodTypes; def registry is filled at mod constructor. wood list is available much later, after RegisterEvent for blocks. that's why we do things here.
                // anyway...
                // small tables
                registerSinglePrimaryBlockForThirdPartyWood(new SimpleTable(), "simple_table_", wood, Registration.blocks_table1, SecondCreativeTab.items_table1);
                // dual tables
                Block primary = new AdvancedTableBottomPrimary();
                registerSingleSupportBlockForThirdPartyWood(primary, "dual_table_bottom_left_", wood);
                registerSingleSupportBlockForThirdPartyWood(new AdvancedTableBottomSecondary(), "dual_table_bottom_right_", wood);
                registerSingleSupportBlockForThirdPartyWood(new AdvancedTableTopSecondary(), "dual_table_top_left_", wood);
                registerSingleSupportBlockForThirdPartyWood(new AdvancedTableTopSecondary(), "dual_table_top_right_",  wood);
                Item placer = new WorkstationPlacerItem(wood);
                Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MODID, "workstation_placer_" + wood), placer);
                Registration.blocks_table2.add(() -> primary); // for the block entity
                SecondCreativeTab.items_table2.add(placer);
                // toolracks
                registerSinglePrimaryBlockForThirdPartyWood(ToolRack.create(2, "single"), "tool_rack_single_", wood, Registration.blocks_rack, SecondCreativeTab.items_rack1);
                registerSinglePrimaryBlockForThirdPartyWood(DualToolRack.create(6, "framed"), "tool_rack_framed_", wood, Registration.blocks_rack, SecondCreativeTab.items_rack2);
                registerSinglePrimaryBlockForThirdPartyWood(DualToolRack.create(6, "pframed"), "tool_rack_pframed_", wood, Registration.blocks_rack, SecondCreativeTab.items_rack3);
                registerSinglePrimaryBlockForThirdPartyWood(DualToolRack.create(6, "double"), "tool_rack_double_", wood, Registration.blocks_rack, SecondCreativeTab.items_rack4);
                // potion shelves
                registerSinglePrimaryBlockForThirdPartyWood(new PotionShelf(), "potion_shelf_", wood, Registration.blocks_pshelf, SecondCreativeTab.items_pshelf);
                // disc shelves
                registerSinglePrimaryBlockForThirdPartyWood(new DiscShelf(), "disc_shelf_", wood, Registration.blocks_dshelf, SecondCreativeTab.items_dshelf);
                // book shelves
                registerSinglePrimaryBlockForThirdPartyWood(new BookShelf.Dual("double"), "book_shelf_double_", wood, Registration.blocks_bshelf, SecondCreativeTab.items_bshelf1);
                registerSinglePrimaryBlockForThirdPartyWood(new BookShelf.Dual("open_double"), "book_shelf_open_double_", wood, Registration.blocks_bshelf, SecondCreativeTab.items_bshelf2);
                registerSinglePrimaryBlockForThirdPartyWood(new BookShelf.TopSimple("minimal"), "book_shelf_minimal_", wood, Registration.blocks_bshelf, SecondCreativeTab.items_bshelf3);
                registerSinglePrimaryBlockForThirdPartyWood(new BookShelf.TopSimple("open_minimal"), "book_shelf_open_minimal_", wood, Registration.blocks_bshelf, SecondCreativeTab.items_bshelf4);
                registerSinglePrimaryBlockForThirdPartyWood(new BookShelf.TopWithLanterns("with_lanterns"), "book_shelf_with_lanterns_", wood, Registration.blocks_bshelf, SecondCreativeTab.items_bshelf5);
            }
            if (wasFrozen)
            {
                BuiltInRegistries.BLOCK.freeze();
            }
        }
        catch (Exception ignored)	{ }
    }
}
