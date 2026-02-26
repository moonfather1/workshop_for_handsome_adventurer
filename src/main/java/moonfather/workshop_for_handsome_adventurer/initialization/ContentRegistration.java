package moonfather.workshop_for_handsome_adventurer.initialization;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.*;
import moonfather.workshop_for_handsome_adventurer.blocks.*;
import moonfather.workshop_for_handsome_adventurer.items.BlockItemEx;
import moonfather.workshop_for_handsome_adventurer.other.CreativeTab;
import moonfather.workshop_for_handsome_adventurer.other.OptionalRecipeCondition;
import moonfather.workshop_for_handsome_adventurer.other.UnsupportedWoodRecipe;
import moonfather.workshop_for_handsome_adventurer.items.WorkstationPlacerItem;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ContentRegistration
{
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Constants.MODID);
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Constants.MODID);
	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.MODID);
	private static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, Constants.MODID);
	private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Constants.MODID);
	private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MODID);
	private static final DeferredRegister<MapCodec<? extends ICondition>> CONDITIONS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, Constants.MODID);
	private static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Constants.MODID);

	public static void init(IEventBus modBus)
	{
		BLOCKS.register(modBus);
		ITEMS.register(modBus);
		BLOCK_ENTITIES.register(modBus);
		CONTAINER_TYPES.register(modBus);
		RECIPES.register(modBus);
		CREATIVE_TABS.register(modBus);
		CONDITIONS.register(modBus);
		DATA_COMPONENT_TYPES.register(modBus);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final List<Supplier<Block>> blocks_table1 = new ArrayList<>();
	public static final List<Supplier<Block>> blocks_table2 = new ArrayList<>();
	public static final List<Supplier<Block>> blocks_rack = new ArrayList<>();
	public static final List<Supplier<Block>> blocks_pshelf = new ArrayList<>();
	public static final List<Supplier<Block>> blocks_dshelf = new ArrayList<>();
	public static final List<Supplier<Block>> blocks_bshelf = new ArrayList<>();
	public static final List<Supplier<Item>> items_table1 = new ArrayList<>();
	public static final List<Supplier<Item>> items_table2 = new ArrayList<>(); // because of sorting in creative tabs, we can't just dump into one list
	public static final List<Supplier<Item>> items_rack1 = new ArrayList<>();
	public static final List<Supplier<Item>> items_rack2 = new ArrayList<>();
	public static final List<Supplier<Item>> items_rack3 = new ArrayList<>();
	public static final List<Supplier<Item>> items_rack4 = new ArrayList<>();
	public static final List<Supplier<Item>> items_pshelf = new ArrayList<>();
	public static final List<Supplier<Item>> items_dshelf = new ArrayList<>();
	public static final List<Supplier<Item>> items_bshelf1 = new ArrayList<>();
	public static final List<Supplier<Item>> items_bshelf2 = new ArrayList<>();
	public static final List<Supplier<Item>> items_bshelf3 = new ArrayList<>();
	public static final List<Supplier<Item>> items_bshelf4 = new ArrayList<>();
	public static final List<Supplier<Item>> items_bshelf5 = new ArrayList<>();
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final List<String> woodTypes = List.of("oak", "spruce", "jungle", "birch", "dark_oak", "mangrove", "cherry");

	// static initialization
	static
	{
		String id;
		// small tables
		for (String woodType : ContentRegistration.woodTypes)
		{
			id = "simple_table_" + woodType;
			Block.Properties prop1 = SimpleTable.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id)));
			Supplier<Block> block = BLOCKS.register(id, () -> new SimpleTable(prop1));
			blocks_table1.add(block);
			items_table1.add(FromBlock(block, id));
		}
		// dual tables
		for (String woodType : ContentRegistration.woodTypes)
		{
			String id1 = "dual_table_bottom_left_" + woodType;
			String id2 = "dual_table_bottom_right_" + woodType;
			String id3 = "dual_table_top_left_" + woodType;
			String id4 = "dual_table_top_right_" + woodType;
			String desc = "item.%s.workstation_placer_%s".formatted(Constants.MODID, woodType);
			Block.Properties prop1 = AdvancedTableBottomPrimary.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id1))).overrideDescription(desc);
			Block.Properties prop2 = AdvancedTableBottomSecondary.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id2))).overrideDescription(desc);
			Block.Properties prop3 = AdvancedTableTopSecondary.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id3))).overrideDescription(desc);
			Block.Properties prop4 = AdvancedTableTopSecondary.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id4))).overrideDescription(desc);
			Supplier<Block> primary = BLOCKS.register(id1, () -> new AdvancedTableBottomPrimary(prop1));
			BLOCKS.register(id2, () -> new AdvancedTableBottomSecondary(prop2));
			BLOCKS.register(id3, () -> new AdvancedTableTopSecondary(prop3));
			BLOCKS.register(id4, () -> new AdvancedTableTopSecondary(prop4));
			String id6 = "workstation_placer_" + woodType;
			Item.Properties prop6 = new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MODID, id6)));
			Supplier<Item> placer = ITEMS.register(id6, () -> new WorkstationPlacerItem(woodType, prop6));
			items_table2.add(placer);
			blocks_table2.add(primary);
		}
		// toolracks
		for (String woodType : ContentRegistration.woodTypes)
		{
			Supplier<Block> rack;
			id = "tool_rack_single_" + woodType;
			Block.Properties prop1 = ToolRack.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id)));
			rack = BLOCKS.register(id, () -> ToolRack.create(2, "single", prop1));
			items_rack1.add(FromBlock(rack, id));
			blocks_rack.add(rack);
			id = "tool_rack_framed_" + woodType;
			Block.Properties prop2 = ToolRack.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id)));
			rack = BLOCKS.register(id, () -> DualToolRack.create(6, "framed", prop2));
			items_rack2.add(FromBlock(rack, id));
			blocks_rack.add(rack);
			id = "tool_rack_pframed_" + woodType;
			Block.Properties prop3 = ToolRack.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id)));
			rack = BLOCKS.register(id, () -> DualToolRack.create(6, "pframed", prop3));
			items_rack3.add(FromBlock(rack, id));
			blocks_rack.add(rack);
			id = "tool_rack_double_" + woodType;
			Block.Properties prop4 = ToolRack.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id)));
			rack = BLOCKS.register(id, () -> DualToolRack.create(6, "double", prop4));
			items_rack4.add(FromBlock(rack, id));
			blocks_rack.add(rack);
		}
		// potion shelves
		for (String woodType : ContentRegistration.woodTypes)
		{
			id = "potion_shelf_" + woodType;
			Block.Properties prop1 = PotionShelf.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id)));
			Supplier<Block> shelf = BLOCKS.register(id, () -> new PotionShelf(prop1));
			items_pshelf.add(FromBlock(shelf, id));
			blocks_pshelf.add(shelf);
		}
		// book shelves
		for (String woodType : ContentRegistration.woodTypes)
		{
			Supplier<Block> rack;
			id = "book_shelf_double_" + woodType;
			Block.Properties prop1 = ToolRack.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id)));
			rack = BLOCKS.register(id, () -> new BookShelf.Dual("double", prop1));
			items_bshelf1.add(FromBlock(rack, id));
			blocks_bshelf.add(rack);
			id = "book_shelf_open_double_" + woodType;
			Block.Properties prop2 = ToolRack.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id)));
			rack = BLOCKS.register(id, () -> new BookShelf.Dual("open_double", prop2));
			items_bshelf2.add(FromBlock(rack, id));
			blocks_bshelf.add(rack);
			id = "book_shelf_minimal_" + woodType;
			Block.Properties prop3 = ToolRack.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id)));
			rack = BLOCKS.register(id, () -> new BookShelf.TopSimple("minimal", prop3));
			items_bshelf3.add(FromBlock(rack, id));
			blocks_bshelf.add(rack);
			id = "book_shelf_open_minimal_" + woodType;
			Block.Properties prop4 = ToolRack.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id)));
			rack = BLOCKS.register(id, () -> new BookShelf.TopSimple("open_minimal", prop4));
			items_bshelf4.add(FromBlock(rack, id));
			blocks_bshelf.add(rack);
			id = "book_shelf_with_lanterns_" + woodType;
			Block.Properties prop5 = ToolRack.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id)));
			rack = BLOCKS.register(id, () -> new BookShelf.TopWithLanterns("with_lanterns", prop5));
			items_bshelf5.add(FromBlock(rack, id));
			blocks_bshelf.add(rack);
		}
		// disc shelves
		for (String woodType : ContentRegistration.woodTypes)
		{
			id = "disc_shelf_" + woodType;
			Block.Properties prop1 = DiscShelf.getDefaultProperties().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MODID, id)));
			Supplier<Block> shelf = BLOCKS.register(id, () -> new DiscShelf(prop1));
			items_dshelf.add(FromBlock(shelf, id));
			blocks_dshelf.add(shelf);
		}
	}

	private static Supplier<Item> FromBlock(Supplier<Block> block, String id)
	{
		// could have passed deferred holder instead of separate string, but it doesn't matter
		Item.Properties properties = new Item.Properties();
		properties.setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MODID, id))).useBlockDescriptionPrefix();
		return ITEMS.register(id, () -> new BlockItemEx(block.get(), properties));
	}

	private static Block[] ListToArray(List<Supplier<Block>> list)
	{
		Block[] result = new Block[list.size()];
		for (int i = 0; i < list.size(); i++)
		{
			result[i] = list.get(i).get();
		}
		return result;
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Supplier<BlockEntityType<ToolRackBlockEntity>> TOOL_RACK_BE = BLOCK_ENTITIES.register("tool_rack_be", () -> new BlockEntityType<>(ToolRackBlockEntity::new, false, ListToArray(blocks_rack)));
	public static final Supplier<BlockEntityType<SimpleTableBlockEntity>> SIMPLE_TABLE_BE = BLOCK_ENTITIES.register("simple_table_be", () -> new BlockEntityType<>(SimpleTableBlockEntity::new, false, ListToArray(blocks_table1)));
	public static final Supplier<BlockEntityType<DualTableBlockEntity>> DUAL_TABLE_BE = BLOCK_ENTITIES.register("dual_table_be", () -> new BlockEntityType<>(DualTableBlockEntity::new, false, ListToArray(blocks_table2)));
	public static final Supplier<BlockEntityType<PotionShelfBlockEntity>> POTION_SHELF_BE = BLOCK_ENTITIES.register("potion_shelf_be", () -> new BlockEntityType<>(PotionShelfBlockEntity::new, false, ListToArray(blocks_pshelf)));
	public static final Supplier<BlockEntityType<DiscShelfBlockEntity>> DISC_SHELF_BE = BLOCK_ENTITIES.register("disc_shelf_be", () -> new BlockEntityType<>(DiscShelfBlockEntity::new, false, ListToArray(blocks_dshelf)));
	public static final Supplier<BlockEntityType<BookShelfBlockEntity>> BOOK_SHELF_BE = BLOCK_ENTITIES.register("book_shelf_be", () -> new BlockEntityType<>(BookShelfBlockEntity::new, false, ListToArray(blocks_bshelf)));
	public static final Supplier<MenuType<SimpleTableMenu>> CRAFTING_SINGLE_MENU_TYPE = CONTAINER_TYPES.register("crafting_single", () -> IMenuTypeExtension.create(SimpleTableMenu::new));
	public static final Supplier<MenuType<DualTableMenu>> CRAFTING_DUAL_MENU_TYPE = CONTAINER_TYPES.register("crafting_dual", () -> IMenuTypeExtension.create(DualTableMenu::new));

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Supplier<RecipeSerializer<UnsupportedWoodRecipe>> TABLE_RECIPE = RECIPES.register("table_recipe_unknown_planks", () -> new CustomRecipe.Serializer<UnsupportedWoodRecipe>(UnsupportedWoodRecipe::new));

	public static final Supplier<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("tab", CreativeTab::buildTab);

	public static final Supplier<MapCodec<? extends ICondition>> OPTIONALRECIPE = CONDITIONS.register("optional", () -> OptionalRecipeCondition.CODEC);

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> TAB_FLAGS = DATA_COMPONENT_TYPES.register("tab_flags", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build() );
}