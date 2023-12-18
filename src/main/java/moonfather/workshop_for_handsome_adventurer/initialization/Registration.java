package moonfather.workshop_for_handsome_adventurer.initialization;

import com.mojang.serialization.Codec;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.*;
import moonfather.workshop_for_handsome_adventurer.blocks.*;
import moonfather.workshop_for_handsome_adventurer.items.BlockItemEx;
import moonfather.workshop_for_handsome_adventurer.other.CreativeTab;
import moonfather.workshop_for_handsome_adventurer.other.OptionalRecipeCondition;
import moonfather.workshop_for_handsome_adventurer.other.UnsupportedWoodRecipe;
import moonfather.workshop_for_handsome_adventurer.items.WorkstationPlacerItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class Registration
{
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Constants.MODID);
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Constants.MODID);
	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.MODID);
	private static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, Constants.MODID);
	private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Constants.MODID);
	private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MODID);
	private static final DeferredRegister<Codec<? extends ICondition>> CONDITIONS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, Constants.MODID);

	public static void init(IEventBus modBus)
	{
		BLOCKS.register(modBus);
		ITEMS.register(modBus);
		BLOCK_ENTITIES.register(modBus);
		CONTAINER_TYPES.register(modBus);
		RECIPES.register(modBus);
		CREATIVE_TABS.register(modBus);
		CONDITIONS.register(modBus);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final List<Supplier<Block>> blocks_table1 = new ArrayList<>();
	public static final List<Supplier<Block>> blocks_table2 = new ArrayList<>();
	public static final List<Supplier<Block>> blocks_rack = new ArrayList<>();
	public static final List<Supplier<Block>> blocks_pshelf = new ArrayList<>();
	public static final List<Supplier<Block>> blocks_bshelf = new ArrayList<>();
	public static final List<Supplier<Item>> items_table1 = new ArrayList<>();
	public static final List<Supplier<Item>> items_table2 = new ArrayList<>(); // because of sorting in creative tabs, we can't just dump into one list
	public static final List<Supplier<Item>> items_rack1 = new ArrayList<>();
	public static final List<Supplier<Item>> items_rack2 = new ArrayList<>();
	public static final List<Supplier<Item>> items_rack3 = new ArrayList<>();
	public static final List<Supplier<Item>> items_rack4 = new ArrayList<>();
	public static final List<Supplier<Item>> items_pshelf = new ArrayList<>();
	public static final List<Supplier<Item>> items_bshelf1 = new ArrayList<>();
	public static final List<Supplier<Item>> items_bshelf2 = new ArrayList<>();
	public static final List<Supplier<Item>> items_bshelf3 = new ArrayList<>();
	public static final List<Supplier<Item>> items_bshelf4 = new ArrayList<>();
	public static final List<Supplier<Item>> items_bshelf5 = new ArrayList<>();
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final String[] woodTypes = {"oak", "spruce", "jungle", "birch", "dark_oak", "mangrove", "cherry"};

	// static initialization
	static {
		String id;
		// small tables
		for (String woodType: Registration.woodTypes) {
			id = "simple_table_" + woodType;
			Supplier<Block> block = BLOCKS.register(id, () -> new SimpleTable());
			blocks_table1.add(block);
			items_table1.add(FromBlock(block, id));
		}
		// dual tables
		for (String woodType: Registration.woodTypes) {
			Supplier<Block> primary = BLOCKS.register("dual_table_bottom_left_" + woodType, () -> new AdvancedTableBottomPrimary());
			BLOCKS.register("dual_table_bottom_right_" + woodType, () -> new AdvancedTableBottomSecondary());
			BLOCKS.register("dual_table_top_left_" + woodType, () -> new AdvancedTableTopSecondary());
			BLOCKS.register("dual_table_top_right_" + woodType, () -> new AdvancedTableTopSecondary());
			Supplier<Item> placer = ITEMS.register("workstation_placer_" + woodType, () -> new WorkstationPlacerItem(woodType));
			items_table2.add(placer);
			blocks_table2.add(primary);
			ExternalWoodSupport.registerHostMod(woodType, Constants.MODID);
		}
		// toolracks
		for (String woodType: Registration.woodTypes) {
			Supplier<Block> rack;
			id = "tool_rack_single_" + woodType;
			rack = BLOCKS.register(id, () -> ToolRack.create(2, "single"));
			items_rack1.add(FromBlock(rack, id));
			blocks_rack.add(rack);
			id = "tool_rack_framed_" + woodType;
			rack = BLOCKS.register(id, () -> DualToolRack.create(6, "framed"));
			items_rack2.add(FromBlock(rack, id));
			blocks_rack.add(rack);
			id = "tool_rack_pframed_" + woodType;
			rack = BLOCKS.register(id, () -> DualToolRack.create(6, "pframed"));
			items_rack3.add(FromBlock(rack,id));
			blocks_rack.add(rack);
			id = "tool_rack_double_" + woodType;
			rack = BLOCKS.register(id, () -> DualToolRack.create(6, "double"));
			items_rack4.add(FromBlock(rack, id));
			blocks_rack.add(rack);
		}
		// potion shelves
		for (String woodType: Registration.woodTypes) {
			id = "potion_shelf_" + woodType;
			Supplier<Block> shelf = BLOCKS.register(id, () -> new PotionShelf());
			items_pshelf.add(FromBlock(shelf, id));
			blocks_pshelf.add(shelf);
		}
		// book shelves
		for (String woodType: Registration.woodTypes) {
			Supplier<Block> rack;
			id = "book_shelf_double_" + woodType;
			rack = BLOCKS.register(id, () -> new BookShelf.Dual("double"));
			items_bshelf1.add(FromBlock(rack, id));
			blocks_bshelf.add(rack);
			id = "book_shelf_open_double_" + woodType;
			rack = BLOCKS.register(id, () -> new BookShelf.Dual("open_double"));
			items_bshelf2.add(FromBlock(rack, id));
			blocks_bshelf.add(rack);
			id = "book_shelf_minimal_" + woodType;
			rack = BLOCKS.register(id, () -> new BookShelf.TopSimple("minimal"));
			items_bshelf3.add(FromBlock(rack, id));
			blocks_bshelf.add(rack);
			id = "book_shelf_open_minimal_" + woodType;
			rack = BLOCKS.register(id, () -> new BookShelf.TopSimple("open_minimal"));
			items_bshelf4.add(FromBlock(rack, id));
			blocks_bshelf.add(rack);
			id = "book_shelf_with_lanterns_" + woodType;
			rack = BLOCKS.register(id, () -> new BookShelf.TopWithLanterns("with_lanterns"));
			items_bshelf5.add(FromBlock(rack, id));
			blocks_bshelf.add(rack);
		}
	}


	private static Supplier<Item> FromBlock(Supplier<Block> block, String id)
	{
		// could have passed deffered holder instead of separate string, but it doesn't matter
		Item.Properties properties = new Item.Properties();
		return ITEMS.register(id, () -> new BlockItemEx(block.get(), properties));
	}

	private static Block[] ListToArray(List<Supplier<Block>> list) {
		Block[] result = new Block[list.size()];
		for (int i = 0; i < list.size(); i++) {
			result[i] = list.get(i).get();
		}
		return result;
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Supplier<BlockEntityType<ToolRackBlockEntity>> TOOL_RACK_BE = BLOCK_ENTITIES.register("tool_rack_be", () -> BlockEntityType.Builder.of(ToolRackBlockEntity::new, ListToArray(blocks_rack)).build(null));
	public static final Supplier<BlockEntityType<SimpleTableBlockEntity>> SIMPLE_TABLE_BE = BLOCK_ENTITIES.register("simple_table_be", () -> BlockEntityType.Builder.of(SimpleTableBlockEntity::new, ListToArray(blocks_table1)).build(null));
	public static final Supplier<BlockEntityType<DualTableBlockEntity>> DUAL_TABLE_BE = BLOCK_ENTITIES.register("dual_table_be", () -> BlockEntityType.Builder.of(DualTableBlockEntity::new, ListToArray(blocks_table2)).build(null));
	public static final Supplier<BlockEntityType<PotionShelfBlockEntity>> POTION_SHELF_BE = BLOCK_ENTITIES.register("potion_shelf_be", () -> BlockEntityType.Builder.of(PotionShelfBlockEntity::new, ListToArray(blocks_pshelf)).build(null));
	public static final Supplier<BlockEntityType<BookShelfBlockEntity>> BOOK_SHELF_BE = BLOCK_ENTITIES.register("book_shelf_be", () -> BlockEntityType.Builder.of(BookShelfBlockEntity::new, ListToArray(blocks_bshelf)).build(null));
	public static final Supplier<MenuType<SimpleTableMenu>> CRAFTING_SINGLE_MENU_TYPE = CONTAINER_TYPES.register("crafting_single", () -> IMenuTypeExtension.create(SimpleTableMenu::new));
	public static final Supplier<MenuType<DualTableMenu>> CRAFTING_DUAL_MENU_TYPE = CONTAINER_TYPES.register("crafting_dual", () -> IMenuTypeExtension.create(DualTableMenu::new));

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Supplier<RecipeSerializer<UnsupportedWoodRecipe>> TABLE_RECIPE = RECIPES.register("table_recipe_unknown_planks", ()-> new SimpleCraftingRecipeSerializer<UnsupportedWoodRecipe>(UnsupportedWoodRecipe::new));

	public static final Supplier<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("tab", CreativeTab::buildTab);

	public static final Supplier<Codec<? extends ICondition>> OptionalRecipe = CONDITIONS.register("optional", () -> OptionalRecipeCondition.CODEC);
}
