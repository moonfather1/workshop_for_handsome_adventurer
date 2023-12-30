package moonfather.workshop_for_handsome_adventurer.initialization;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.*;
import moonfather.workshop_for_handsome_adventurer.blocks.*;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.DynamicAssetConfig;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.SecondCreativeTab;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.WoodTypeLister;
import moonfather.workshop_for_handsome_adventurer.items.BlockItemEx;
import moonfather.workshop_for_handsome_adventurer.other.CreativeTab;
import moonfather.workshop_for_handsome_adventurer.other.UnsupportedWoodRecipe;
import moonfather.workshop_for_handsome_adventurer.items.WorkstationPlacerItem;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

import java.util.ArrayList;
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

	public static void init()
	{
		BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
		BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
		CONTAINER_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
		RECIPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final List<Supplier<Block>> blocks_table1 = new ArrayList<>();
	public static final List<Supplier<Block>> blocks_table2 = new ArrayList<>();
	public static final List<Supplier<Block>> blocks_rack = new ArrayList<>();
	public static final List<Supplier<Block>> blocks_pshelf = new ArrayList<>();
	public static final List<Supplier<Block>> blocks_bshelf = new ArrayList<>();
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final String[] woodTypes = {"oak", "spruce", "jungle", "birch", "dark_oak", "mangrove"};

	// static initialization
	static {
		// small tables
		for (String woodType: Registration.woodTypes) {
			RegistryObject<Block> block = BLOCKS.register("simple_table_" + woodType, () -> new SimpleTable());
			blocks_table1.add(block);
			FromBlock(block);
		}
		// dual tables
		for (String woodType: Registration.woodTypes) {
			RegistryObject<Block> primary = BLOCKS.register("dual_table_bottom_left_" + woodType, () -> new AdvancedTableBottomPrimary());
			BLOCKS.register("dual_table_bottom_right_" + woodType, () -> new AdvancedTableBottomSecondary());
			BLOCKS.register("dual_table_top_left_" + woodType, () -> new AdvancedTableTopSecondary());
			BLOCKS.register("dual_table_top_right_" + woodType, () -> new AdvancedTableTopSecondary());
			RegistryObject<Item> placer = ITEMS.register("workstation_placer_" + woodType, () -> new WorkstationPlacerItem(woodType, new Item.Properties().tab(CreativeTab.TAB_WORKSHOP)));
			blocks_table2.add(primary);
			ExternalWoodSupport.registerHostMod(woodType, Constants.MODID);
		}
		// toolracks
		for (String woodType: Registration.woodTypes) {
			RegistryObject<Block> rack;
			rack = BLOCKS.register("tool_rack_single_" + woodType, () -> ToolRack.create(2, "single"));
			blocks_rack.add(rack);
			FromBlock(rack);
			rack = BLOCKS.register("tool_rack_framed_" + woodType, () -> DualToolRack.create(6, "framed"));
			blocks_rack.add(rack);
			FromBlock(rack);
			rack = BLOCKS.register("tool_rack_pframed_" + woodType, () -> DualToolRack.create(6, "pframed"));
			blocks_rack.add(rack);
			FromBlock(rack);
			rack = BLOCKS.register("tool_rack_double_" + woodType, () -> DualToolRack.create(6, "double"));
			blocks_rack.add(rack);
			FromBlock(rack);
		}
		// potion shelves
		for (String woodType: Registration.woodTypes) {
			RegistryObject<Block> shelf = BLOCKS.register("potion_shelf_" + woodType, () -> new PotionShelf());
			blocks_pshelf.add(shelf);
			FromBlock(shelf);
		}
		// book shelves
		for (String woodType: Registration.woodTypes) {
			RegistryObject<Block> rack;
			rack = BLOCKS.register("book_shelf_double_" + woodType, () -> new BookShelf.Dual("double"));
			blocks_bshelf.add(rack);
			FromBlock(rack);
			rack = BLOCKS.register("book_shelf_open_double_" + woodType, () -> new BookShelf.Dual("open_double"));
			blocks_bshelf.add(rack);
			FromBlock(rack);
			rack = BLOCKS.register("book_shelf_minimal_" + woodType, () -> new BookShelf.TopSimple("minimal"));
			blocks_bshelf.add(rack);
			FromBlock(rack);
			rack = BLOCKS.register("book_shelf_open_minimal_" + woodType, () -> new BookShelf.TopSimple("open_minimal"));
			blocks_bshelf.add(rack);
			FromBlock(rack);
			rack = BLOCKS.register("book_shelf_with_lanterns_" + woodType, () -> new BookShelf.TopWithLanterns("with_lanterns"));
			blocks_bshelf.add(rack);
			FromBlock(rack);
		}
	}



	private static RegistryObject<Item> FromBlock(RegistryObject<Block> block)
	{
		Item.Properties properties = new Item.Properties().tab(CreativeTab.TAB_WORKSHOP);
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

	public static final RegistryObject<RecipeSerializer<UnsupportedWoodRecipe>> TABLE_RECIPE = RECIPES.register("table_recipe_unknown_planks", ()-> new SimpleRecipeSerializer<UnsupportedWoodRecipe>(UnsupportedWoodRecipe::new));

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void registerSingleBlockForThirdPartyWood(Block block, String prefix, String wood, List<Supplier<Block>> listForBlockEntities, CreativeModeTab tab)
	{
		Item item = new BlockItemEx(block, new Item.Properties().tab(tab));
		ForgeRegistries.BLOCKS.register(prefix + wood, block);
		ForgeRegistries.ITEMS.register(prefix + wood, item);
		if (listForBlockEntities != null)
		{
			listForBlockEntities.add(() -> block); // for the block entity
		}
		if (block instanceof SimpleTable)
		{
			SecondCreativeTab.items_table1.add(item);
		}
	}

	public static void registerBlocksForThirdPartyWood(RegisterEvent event)
	{
		if (! DynamicAssetConfig.masterLeverOn())
		{
			return;
		}
		try  // because of unfreeze fuckery
		{
			CreativeModeTab tab = DynamicAssetConfig.separateCreativeTab() ? SecondCreativeTab.getInstance() : CreativeTab.TAB_WORKSHOP;
			((ForgeRegistry) ForgeRegistries.BLOCKS).unfreeze();
			for (String wood : WoodTypeLister.getWoodIds())
			{
				// can't just add wood types to Registration.woodTypes; def registry is filled at mod constructor. wood list is available much later, after RegisterEvent for blocks. that's why we do things here.
				// anyway...
				// small tables
				registerSingleBlockForThirdPartyWood(new SimpleTable(), "simple_table_", wood, blocks_table1, tab);
				// dual tables
				Block primary = new AdvancedTableBottomPrimary();
				ForgeRegistries.BLOCKS.register("dual_table_bottom_left_" + wood, primary);
				ForgeRegistries.BLOCKS.register("dual_table_bottom_right_" + wood, new AdvancedTableBottomSecondary());
				ForgeRegistries.BLOCKS.register("dual_table_top_left_" + wood, new AdvancedTableTopSecondary());
				ForgeRegistries.BLOCKS.register("dual_table_top_right_" + wood, new AdvancedTableTopSecondary());
				Item placer = new WorkstationPlacerItem(wood, new Item.Properties().tab(tab));
				ForgeRegistries.ITEMS.register("workstation_placer_" + wood, placer);
				blocks_table2.add(() -> primary); // for the block entity
				ExternalWoodSupport.registerHostMod(wood, Constants.MODID);
				// toolracks
				registerSingleBlockForThirdPartyWood(ToolRack.create(2, "single"), "tool_rack_single_", wood, blocks_rack, tab);
				registerSingleBlockForThirdPartyWood(DualToolRack.create(6, "framed"), "tool_rack_framed_", wood, blocks_rack, tab);
				registerSingleBlockForThirdPartyWood(DualToolRack.create(6, "pframed"), "tool_rack_pframed_", wood, blocks_rack, tab);
				registerSingleBlockForThirdPartyWood(DualToolRack.create(6, "double"), "tool_rack_double_", wood, blocks_rack, tab);
				// potion shelves
				registerSingleBlockForThirdPartyWood(new PotionShelf(), "potion_shelf_", wood, blocks_pshelf, tab);
				// book shelves
				registerSingleBlockForThirdPartyWood(new BookShelf.Dual("double"), "book_shelf_double_", wood, blocks_bshelf, tab);
				registerSingleBlockForThirdPartyWood(new BookShelf.Dual("open_double"), "book_shelf_open_double_", wood, blocks_bshelf, tab);
				registerSingleBlockForThirdPartyWood(new BookShelf.TopSimple("minimal"), "book_shelf_minimal_", wood, blocks_bshelf, tab);
				registerSingleBlockForThirdPartyWood(new BookShelf.TopSimple("open_minimal"), "book_shelf_open_minimal_", wood, blocks_bshelf, tab);
				registerSingleBlockForThirdPartyWood(new BookShelf.TopWithLanterns("with_lanterns"), "book_shelf_with_lanterns_", wood, blocks_bshelf, tab);
			}
			((ForgeRegistry) ForgeRegistries.BLOCKS).freeze();
		}
		catch (Exception ignored)	{ }
	}
}
