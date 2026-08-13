package moonfather.workshop_for_handsome_adventurer.initialization;

import com.mojang.logging.LogUtils;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.blocks.*;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.MissingMappingsHandler;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.SecondCreativeTab;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.WoodTypeLister;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.config.DynamicAssetCommonConfig;
import moonfather.workshop_for_handsome_adventurer.items.BlockItemEx;
import moonfather.workshop_for_handsome_adventurer.items.WorkstationPlacerItem;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
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
                event.register(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(Constants.MODID, "tab2"), SecondCreativeTab::getTab);
            }
        }
    }



    private static void registerSinglePrimaryBlockForThirdPartyWood(Block block, String id, List<Supplier<Block>> listForBlockEntities, List<Item> listForCreativeTab)
    {
        Item.Properties prop26 = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MODID, id))).useBlockDescriptionPrefix();
        Item item = new BlockItemEx(block, prop26);
        Identifier fullId = Identifier.fromNamespaceAndPath(Constants.MODID, id);
        Registry.register(BuiltInRegistries.BLOCK, fullId, block);
        Registry.register(BuiltInRegistries.ITEM, fullId, item);
        listForCreativeTab.add(item);
        if (listForBlockEntities != null)
        {
            listForBlockEntities.add(() -> block); // for the block entity
        }
    }

    private static void registerSingleSupportBlockForThirdPartyWood(Block block, String id)
    {
        Identifier fullId = Identifier.fromNamespaceAndPath(Constants.MODID, id);
        Registry.register(BuiltInRegistries.BLOCK, fullId, block);
    }

    private static void registerBlocksForThirdPartyWood(RegisterEvent event)
    {
        try  // because of unfreeze fuckery
        {
            boolean wasFrozen = ((MappedRegistry<Block>) BuiltInRegistries.BLOCK).frozen;
            if (wasFrozen)
            {
                ((MappedRegistry<Block>) BuiltInRegistries.BLOCK).unfreeze(false);
            }
            for (String wood : WoodTypeLister.getWoodIds())
            {
                // can't just add wood types to Registration.woodTypes; def registry is filled at mod constructor. wood list is available much later, after RegisterEvent for blocks. that's why we do things here.
                // anyway...
                // small tables
                String id1 = "simple_table_" + wood;
                Block.Properties prop1 = SimpleTable.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id1)));
                registerSinglePrimaryBlockForThirdPartyWood(new SimpleTable(prop1), id1, ContentRegistration.blocks_table1, SecondCreativeTab.items_table1);
                // dual tables
                String id2 = "dual_table_bottom_left_" + wood;
                String id3 = "dual_table_bottom_right_" + wood;
                String id4 = "dual_table_top_left_" + wood;
                String id5 = "dual_table_top_right_" + wood;
                String desc = "item.%s.workstation_placer_%s".formatted(Constants.MODID, wood);
                Block.Properties prop2 = DualTableBaseBlock.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id2))).overrideDescription(desc);
                Block.Properties prop3 = DualTableBaseBlock.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id3))).overrideDescription(desc);
                Block.Properties prop4 = DualTableBaseBlock.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id4))).overrideDescription(desc);
                Block.Properties prop5 = DualTableBaseBlock.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id5))).overrideDescription(desc);
                Block primary = new AdvancedTableBottomPrimary(prop2);
                registerSingleSupportBlockForThirdPartyWood(primary, id2);
                registerSingleSupportBlockForThirdPartyWood(new AdvancedTableBottomSecondary(prop3), id3);
                registerSingleSupportBlockForThirdPartyWood(new AdvancedTableTopSecondary(prop4), id4);
                registerSingleSupportBlockForThirdPartyWood(new AdvancedTableTopSecondary(prop5), id5);
                String id6 = "workstation_placer_" + wood;
                Item.Properties prop6 = new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MODID, id6)));
                Item placer = new WorkstationPlacerItem(wood, prop6);
                Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Constants.MODID, id6), placer);
                ContentRegistration.blocks_table2.add(() -> primary); // for the block entity
                SecondCreativeTab.items_table2.add(placer);
                // toolracks
                String id7 = "tool_rack_single_" + wood;
                String id8 = "tool_rack_framed_" + wood;
                String id9 = "tool_rack_pframed_" + wood;
                String idA = "tool_rack_double_" + wood;
                Block.Properties prop7 = ToolRack.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id7)));
                Block.Properties prop8 = ToolRack.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id8)));
                Block.Properties prop9 = ToolRack.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id9)));
                Block.Properties propA = ToolRack.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, idA)));
                registerSinglePrimaryBlockForThirdPartyWood(ToolRack.create(2, "single", prop7), id7, ContentRegistration.blocks_rack, SecondCreativeTab.items_rack1);
                registerSinglePrimaryBlockForThirdPartyWood(DualToolRack.create(6, "framed", prop8), id8, ContentRegistration.blocks_rack, SecondCreativeTab.items_rack2);
                registerSinglePrimaryBlockForThirdPartyWood(DualToolRack.create(6, "pframed", prop9), id9, ContentRegistration.blocks_rack, SecondCreativeTab.items_rack3);
                registerSinglePrimaryBlockForThirdPartyWood(DualToolRack.create(6, "double", propA), idA, ContentRegistration.blocks_rack, SecondCreativeTab.items_rack4);
                // potion shelves
                String id11 = "potion_shelf_" + wood;
                Block.Properties prop11 = PotionShelf.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id11)));
                registerSinglePrimaryBlockForThirdPartyWood(new PotionShelf(prop11), id11, ContentRegistration.blocks_pshelf, SecondCreativeTab.items_pshelf);
                // disc shelves
                String id91 = "disc_shelf_" + wood;
                Block.Properties prop91 = DiscShelf.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id91)));
                registerSinglePrimaryBlockForThirdPartyWood(new DiscShelf(prop91), id91, ContentRegistration.blocks_dshelf, SecondCreativeTab.items_dshelf);
                // book shelves
                String id12 = "book_shelf_double_" + wood;
                String id13 = "book_shelf_open_double_" + wood;
                String id14 = "book_shelf_minimal_" + wood;
                String id15 = "book_shelf_open_minimal_" + wood;
                String id16 = "book_shelf_with_lanterns_" + wood;
                Block.Properties prop12 = BookShelf.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id12)));
                Block.Properties prop13 = BookShelf.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id13)));
                Block.Properties prop14 = BookShelf.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id14)));
                Block.Properties prop15 = BookShelf.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id15)));
                Block.Properties prop16 = BookShelf.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id16)));
                registerSinglePrimaryBlockForThirdPartyWood(new BookShelf.Dual("double", prop12), id12, ContentRegistration.blocks_bshelf, SecondCreativeTab.items_bshelf1);
                registerSinglePrimaryBlockForThirdPartyWood(new BookShelf.Dual("open_double", prop13), id13, ContentRegistration.blocks_bshelf, SecondCreativeTab.items_bshelf2);
                registerSinglePrimaryBlockForThirdPartyWood(new BookShelf.TopSimple("open_minimal", prop15), id15, ContentRegistration.blocks_bshelf, SecondCreativeTab.items_bshelf4);
                registerSinglePrimaryBlockForThirdPartyWood(new BookShelf.TopWithLanterns("with_lanterns", prop16), id16, ContentRegistration.blocks_bshelf, SecondCreativeTab.items_bshelf5);
            }
            if (wasFrozen)
            {
                BuiltInRegistries.BLOCK.freeze();
            }
        }
        catch (Exception e)
        {
            LogUtils.getLogger().error("WFHA error 141: " + e.getMessage());
        }
    }
}
