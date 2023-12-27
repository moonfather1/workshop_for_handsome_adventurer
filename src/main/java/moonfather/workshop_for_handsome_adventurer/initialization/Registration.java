package moonfather.workshop_for_handsome_adventurer.initialization;

import com.mojang.logging.LogUtils;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.*;
import moonfather.workshop_for_handsome_adventurer.blocks.*;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.SecondCreativeTab;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.WoodTypeLister;
import moonfather.workshop_for_handsome_adventurer.items.BlockItemEx;
import moonfather.workshop_for_handsome_adventurer.other.CreativeTab;
import moonfather.workshop_for_handsome_adventurer.other.UnsupportedWoodRecipe;
import moonfather.workshop_for_handsome_adventurer.items.WorkstationPlacerItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class Registration
{
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MODID);
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MODID);
	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MODID);
	private static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Constants.MODID);
	private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Constants.MODID);
	private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MODID);

	public static void init()
	{
		BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
		BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
		CONTAINER_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
		RECIPES.register(FMLJavaModLoadingContext.get().getModEventBus());
		CREATIVE_TABS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final List<Supplier<Block>> blocks_table1 = new ArrayList<>();
	public static final List<Supplier<Block>> blocks_table2 = new ArrayList<>();
	public static final List<Supplier<Block>> blocks_rack = new ArrayList<>();
	public static final List<Supplier<Block>> blocks_pshelf = new ArrayList<>();
	public static final List<Supplier<Block>> blocks_bshelf = new ArrayList<>();
	public static final List<RegistryObject<Item>> items_table1 = new ArrayList<>();
	public static final List<RegistryObject<Item>> items_table2 = new ArrayList<>(); // because of sorting in creative tabs, we can't just dump into one list
	public static final List<RegistryObject<Item>> items_rack1 = new ArrayList<>();
	public static final List<RegistryObject<Item>> items_rack2 = new ArrayList<>();
	public static final List<RegistryObject<Item>> items_rack3 = new ArrayList<>();
	public static final List<RegistryObject<Item>> items_rack4 = new ArrayList<>();
	public static final List<RegistryObject<Item>> items_pshelf = new ArrayList<>();
	public static final List<RegistryObject<Item>> items_bshelf1 = new ArrayList<>();
	public static final List<RegistryObject<Item>> items_bshelf2 = new ArrayList<>();
	public static final List<RegistryObject<Item>> items_bshelf3 = new ArrayList<>();
	public static final List<RegistryObject<Item>> items_bshelf4 = new ArrayList<>();
	public static final List<RegistryObject<Item>> items_bshelf5 = new ArrayList<>();
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final List<String> woodTypes = new ArrayList<>(Arrays.asList("oak", "spruce", "jungle", "birch", "dark_oak", "mangrove", "cherry"));

	// static initialization
	static {
		// small tables
		for (String woodType: Registration.woodTypes) {
			RegistryObject<Block> block = BLOCKS.register("simple_table_" + woodType, () -> new SimpleTable());
			blocks_table1.add(block);
			items_table1.add(FromBlock(block));
		}
		// dual tables
		for (String woodType: Registration.woodTypes) {
			RegistryObject<Block> primary = BLOCKS.register("dual_table_bottom_left_" + woodType, () -> new AdvancedTableBottomPrimary());
			BLOCKS.register("dual_table_bottom_right_" + woodType, () -> new AdvancedTableBottomSecondary());
			BLOCKS.register("dual_table_top_left_" + woodType, () -> new AdvancedTableTopSecondary());
			BLOCKS.register("dual_table_top_right_" + woodType, () -> new AdvancedTableTopSecondary());
			RegistryObject<Item> placer = ITEMS.register("workstation_placer_" + woodType, () -> new WorkstationPlacerItem(woodType));
			items_table2.add(placer);
			blocks_table2.add(primary);
			ExternalWoodSupport.registerHostMod(woodType, Constants.MODID);
		}
		// toolracks
		for (String woodType: Registration.woodTypes) {
			RegistryObject<Block> rack;
			rack = BLOCKS.register("tool_rack_single_" + woodType, () -> ToolRack.create(2, "single"));
			items_rack1.add(FromBlock(rack));
			blocks_rack.add(rack);
			rack = BLOCKS.register("tool_rack_framed_" + woodType, () -> DualToolRack.create(6, "framed"));
			items_rack2.add(FromBlock(rack));
			blocks_rack.add(rack);
			rack = BLOCKS.register("tool_rack_pframed_" + woodType, () -> DualToolRack.create(6, "pframed"));
			items_rack3.add(FromBlock(rack));
			blocks_rack.add(rack);
			rack = BLOCKS.register("tool_rack_double_" + woodType, () -> DualToolRack.create(6, "double"));
			items_rack4.add(FromBlock(rack));
			blocks_rack.add(rack);
		}
		// potion shelves
		for (String woodType: Registration.woodTypes) {
			RegistryObject<Block> shelf = BLOCKS.register("potion_shelf_" + woodType, () -> new PotionShelf());
			items_pshelf.add(FromBlock(shelf));
			blocks_pshelf.add(shelf);
		}
		// book shelves
		for (String woodType: Registration.woodTypes) {
			RegistryObject<Block> rack;
			rack = BLOCKS.register("book_shelf_double_" + woodType, () -> new BookShelf.Dual("double"));
			items_bshelf1.add(FromBlock(rack));
			blocks_bshelf.add(rack);
			rack = BLOCKS.register("book_shelf_open_double_" + woodType, () -> new BookShelf.Dual("open_double"));
			items_bshelf2.add(FromBlock(rack));
			blocks_bshelf.add(rack);
			rack = BLOCKS.register("book_shelf_minimal_" + woodType, () -> new BookShelf.TopSimple("minimal"));
			items_bshelf3.add(FromBlock(rack));
			blocks_bshelf.add(rack);
			rack = BLOCKS.register("book_shelf_open_minimal_" + woodType, () -> new BookShelf.TopSimple("open_minimal"));
			items_bshelf4.add(FromBlock(rack));
			blocks_bshelf.add(rack);
			rack = BLOCKS.register("book_shelf_with_lanterns_" + woodType, () -> new BookShelf.TopWithLanterns("with_lanterns"));
			items_bshelf5.add(FromBlock(rack));
			blocks_bshelf.add(rack);
		}
	}


	private static RegistryObject<Item> FromBlock(RegistryObject<Block> block)
	{
		Item.Properties properties = new Item.Properties();
		return ITEMS.register(block.getId().getPath(), () -> new BlockItemEx(block.get(), properties));
	}

	private static Block[] ListToArray(List<Supplier<Block>> list) {
		Block[] result = new Block[list.size()];
		for (int i = 0; i < list.size(); i++) {
			result[i] = list.get(i).get();
		}
		return result;
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final RegistryObject<BlockEntityType<ToolRackBlockEntity>> TOOL_RACK_BE = BLOCK_ENTITIES.register("tool_rack_be", () -> BlockEntityType.Builder.of(ToolRackBlockEntity::new, ListToArray(blocks_rack)).build(null));
	public static final RegistryObject<BlockEntityType<SimpleTableBlockEntity>> SIMPLE_TABLE_BE = BLOCK_ENTITIES.register("simple_table_be", () -> BlockEntityType.Builder.of(SimpleTableBlockEntity::new, ListToArray(blocks_table1)).build(null));
	public static final RegistryObject<BlockEntityType<DualTableBlockEntity>> DUAL_TABLE_BE = BLOCK_ENTITIES.register("dual_table_be", () -> BlockEntityType.Builder.of(DualTableBlockEntity::new, ListToArray(blocks_table2)).build(null));
	public static final RegistryObject<BlockEntityType<PotionShelfBlockEntity>> POTION_SHELF_BE = BLOCK_ENTITIES.register("potion_shelf_be", () -> BlockEntityType.Builder.of(PotionShelfBlockEntity::new, ListToArray(blocks_pshelf)).build(null));
	public static final RegistryObject<BlockEntityType<BookShelfBlockEntity>> BOOK_SHELF_BE = BLOCK_ENTITIES.register("book_shelf_be", () -> BlockEntityType.Builder.of(BookShelfBlockEntity::new, ListToArray(blocks_bshelf)).build(null));
	public static final RegistryObject<MenuType<SimpleTableMenu>> CRAFTING_SINGLE_MENU_TYPE = CONTAINER_TYPES.register("crafting_single", () -> IForgeMenuType.create(SimpleTableMenu::new));
	public static final RegistryObject<MenuType<DualTableMenu>> CRAFTING_DUAL_MENU_TYPE = CONTAINER_TYPES.register("crafting_dual", () -> IForgeMenuType.create(DualTableMenu::new));

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final RegistryObject<RecipeSerializer<UnsupportedWoodRecipe>> TABLE_RECIPE = RECIPES.register("table_recipe_unknown_planks", ()-> new SimpleCraftingRecipeSerializer<UnsupportedWoodRecipe>(UnsupportedWoodRecipe::new));

	public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("tab", CreativeTab::buildTab);

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void registerSingleBlockForThirdPartyWood(Block block, String prefix, String wood, List<Supplier<Block>> listForBlockEntities, List<Item> listForCreativeTab)
	{
		Item item = new BlockItemEx(block, new Item.Properties());
		ForgeRegistries.BLOCKS.register(prefix + wood, block);
		ForgeRegistries.ITEMS.register(prefix + wood, item);
		listForCreativeTab.add(item);
		if (listForBlockEntities != null)
		{
			listForBlockEntities.add(() -> block); // for the block entity
		}
	}

	public static void registerBlocksForThirdPartyWood(RegisterEvent event)
	{
		try  // because of unfreeze fuckery
		{
			((ForgeRegistry) ForgeRegistries.BLOCKS).unfreeze();
			for (String wood : WoodTypeLister.getWoodIds())
			{
				// can't just add wood types to Registration.woodTypes; def registry is filled at mod constructor. wood list is available much later, after RegisterEvent for blocks. that's why we do things here.
				// anyway...
				// small tables
				registerSingleBlockForThirdPartyWood(new SimpleTable(), "simple_table_", wood, blocks_table1, SecondCreativeTab.items_table1);
				// dual tables
				Block primary = new AdvancedTableBottomPrimary();
				ForgeRegistries.BLOCKS.register("dual_table_bottom_left_" + wood, primary);
				ForgeRegistries.BLOCKS.register("dual_table_bottom_right_" + wood, new AdvancedTableBottomSecondary());
				ForgeRegistries.BLOCKS.register("dual_table_top_left_" + wood, new AdvancedTableTopSecondary());
				ForgeRegistries.BLOCKS.register("dual_table_top_right_" + wood, new AdvancedTableTopSecondary());
				Item placer = new WorkstationPlacerItem(wood);
				ForgeRegistries.ITEMS.register("workstation_placer_" + wood, placer);
				blocks_table2.add(() -> primary); // for the block entity
				SecondCreativeTab.items_table2.add(placer);
				ExternalWoodSupport.registerHostMod(wood, Constants.MODID);
				// toolracks
				registerSingleBlockForThirdPartyWood(ToolRack.create(2, "single"), "tool_rack_single_", wood, blocks_rack, SecondCreativeTab.items_rack1);
				registerSingleBlockForThirdPartyWood(DualToolRack.create(6, "framed"), "tool_rack_framed_", wood, blocks_rack, SecondCreativeTab.items_rack2);
				registerSingleBlockForThirdPartyWood(DualToolRack.create(6, "pframed"), "tool_rack_pframed_", wood, blocks_rack, SecondCreativeTab.items_rack3);
				registerSingleBlockForThirdPartyWood(DualToolRack.create(6, "double"), "tool_rack_double_", wood, blocks_rack, SecondCreativeTab.items_rack4);
				// potion shelves
				registerSingleBlockForThirdPartyWood(new PotionShelf(), "potion_shelf_", wood, blocks_pshelf, SecondCreativeTab.items_pshelf);
				// book shelves
				registerSingleBlockForThirdPartyWood(new BookShelf.Dual("double"), "book_shelf_double_", wood, blocks_bshelf, SecondCreativeTab.items_bshelf1);
				registerSingleBlockForThirdPartyWood(new BookShelf.Dual("open_double"), "book_shelf_open_double_", wood, blocks_bshelf, SecondCreativeTab.items_bshelf2);
				registerSingleBlockForThirdPartyWood(new BookShelf.TopSimple("minimal"), "book_shelf_minimal_", wood, blocks_bshelf, SecondCreativeTab.items_bshelf3);
				registerSingleBlockForThirdPartyWood(new BookShelf.TopSimple("open_minimal"), "book_shelf_open_minimal_", wood, blocks_bshelf, SecondCreativeTab.items_bshelf4);
				registerSingleBlockForThirdPartyWood(new BookShelf.TopWithLanterns("with_lanterns"), "book_shelf_with_lanterns_", wood, blocks_bshelf, SecondCreativeTab.items_bshelf5);
			}
			((ForgeRegistry) ForgeRegistries.BLOCKS).freeze();
		}
		catch (Exception ignored)	{ }
	}
}
