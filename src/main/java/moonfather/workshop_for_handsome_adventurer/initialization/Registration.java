package moonfather.workshop_for_handsome_adventurer.initialization;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.*;
import moonfather.workshop_for_handsome_adventurer.blocks.*;
import moonfather.workshop_for_handsome_adventurer.items.BlockItemEx;
import moonfather.workshop_for_handsome_adventurer.other.CreativeTab;
import moonfather.workshop_for_handsome_adventurer.other.UnsupportedWoodRecipe;
import moonfather.workshop_for_handsome_adventurer.items.WorkstationPlacerItem;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registration
{
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MODID);
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MODID);
	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Constants.MODID);
	public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, Constants.MODID);
	public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Constants.MODID);

	public static void init()
	{
		BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
		BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
		CONTAINER_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
		RECIPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final RegistryObject<Block> SIMPLE_TABLE_OAK = BLOCKS.register("simple_table_oak", () -> new SimpleTable());
	public static final RegistryObject<Item> SIMPLE_TABLE_ITEM_OAK = FromBlock(SIMPLE_TABLE_OAK);
	public static final RegistryObject<Block> SIMPLE_TABLE_SPRUCE = BLOCKS.register("simple_table_spruce", () -> new SimpleTable());
	public static final RegistryObject<Item> SIMPLE_TABLE_ITEM_SPRUCE = FromBlock(SIMPLE_TABLE_SPRUCE);
	public static final RegistryObject<Block> SIMPLE_TABLE_BIRCH = BLOCKS.register("simple_table_birch", () -> new SimpleTable());
	public static final RegistryObject<Item> SIMPLE_TABLE_ITEM_BIRCH = FromBlock(SIMPLE_TABLE_BIRCH);
	public static final RegistryObject<Block> SIMPLE_TABLE_JUNGLE = BLOCKS.register("simple_table_jungle", () -> new SimpleTable());
	public static final RegistryObject<Item> SIMPLE_TABLE_ITEM_JUNGLE = FromBlock(SIMPLE_TABLE_JUNGLE);
	public static final RegistryObject<Block> SIMPLE_TABLE_DARK_OAK = BLOCKS.register("simple_table_dark_oak", () -> new SimpleTable());
	public static final RegistryObject<Item> SIMPLE_TABLE_ITEM_DARK_OAK = FromBlock(SIMPLE_TABLE_DARK_OAK);

	public static final RegistryObject<Block> DUAL_TABLE_PRIMARY_OAK = BLOCKS.register("dual_table_bottom_left_oak", () -> new AdvancedTableBottomPrimary());
	public static final RegistryObject<Block> DUAL_TABLE_PRIMARY_SPRUCE = BLOCKS.register("dual_table_bottom_left_spruce", () -> new AdvancedTableBottomPrimary());
	public static final RegistryObject<Block> DUAL_TABLE_PRIMARY_BIRCH = BLOCKS.register("dual_table_bottom_left_birch", () -> new AdvancedTableBottomPrimary());
	public static final RegistryObject<Block> DUAL_TABLE_PRIMARY_JUNGLE = BLOCKS.register("dual_table_bottom_left_jungle", () -> new AdvancedTableBottomPrimary());
	public static final RegistryObject<Block> DUAL_TABLE_PRIMARY_DARK_OAK = BLOCKS.register("dual_table_bottom_left_dark_oak", () -> new AdvancedTableBottomPrimary());
	public static final RegistryObject<Block> DUAL_TABLE_SECONDARY_OAK = BLOCKS.register("dual_table_bottom_right_oak", () -> new AdvancedTableBottomSecondary());
	public static final RegistryObject<Block> DUAL_TABLE_SECONDARY_SPRUCE = BLOCKS.register("dual_table_bottom_right_spruce", () -> new AdvancedTableBottomSecondary());
	public static final RegistryObject<Block> DUAL_TABLE_SECONDARY_BIRCH = BLOCKS.register("dual_table_bottom_right_birch", () -> new AdvancedTableBottomSecondary());
	public static final RegistryObject<Block> DUAL_TABLE_SECONDARY_JUNGLE = BLOCKS.register("dual_table_bottom_right_jungle", () -> new AdvancedTableBottomSecondary());
	public static final RegistryObject<Block> DUAL_TABLE_SECONDARY_DARK_OAK = BLOCKS.register("dual_table_bottom_right_dark_oak", () -> new AdvancedTableBottomSecondary());
	public static final RegistryObject<Block> DUAL_TABLE_TOP_LEFT_OAK = BLOCKS.register("dual_table_top_left_oak", () -> new AdvancedTableTopSecondary());
	public static final RegistryObject<Block> DUAL_TABLE_TOP_LEFT_SPRUCE = BLOCKS.register("dual_table_top_left_spruce", () -> new AdvancedTableTopSecondary());
	public static final RegistryObject<Block> DUAL_TABLE_TOP_LEFT_BIRCH = BLOCKS.register("dual_table_top_left_birch", () -> new AdvancedTableTopSecondary());
	public static final RegistryObject<Block> DUAL_TABLE_TOP_LEFT_JUNGLE = BLOCKS.register("dual_table_top_left_jungle", () -> new AdvancedTableTopSecondary());
	public static final RegistryObject<Block> DUAL_TABLE_TOP_LEFT_DARK_OAK = BLOCKS.register("dual_table_top_left_dark_oak", () -> new AdvancedTableTopSecondary());
	public static final RegistryObject<Block> DUAL_TABLE_TOP_RIGHT_OAK = BLOCKS.register("dual_table_top_right_oak", () -> new AdvancedTableTopSecondary());
	public static final RegistryObject<Block> DUAL_TABLE_TOP_RIGHT_SPRUCE = BLOCKS.register("dual_table_top_right_spruce", () -> new AdvancedTableTopSecondary());
	public static final RegistryObject<Block> DUAL_TABLE_TOP_RIGHT_BIRCH = BLOCKS.register("dual_table_top_right_birch", () -> new AdvancedTableTopSecondary());
	public static final RegistryObject<Block> DUAL_TABLE_TOP_RIGHT_JUNGLE = BLOCKS.register("dual_table_top_right_jungle", () -> new AdvancedTableTopSecondary());
	public static final RegistryObject<Block> DUAL_TABLE_TOP_RIGHT_DARK_OAK = BLOCKS.register("dual_table_top_right_dark_oak", () -> new AdvancedTableTopSecondary());

	public static final RegistryObject<Item> DUAL_TABLE_PLACER_ITEM_OAK = ITEMS.register("workstation_placer_oak", () -> new WorkstationPlacerItem("oak"));
	public static final RegistryObject<Item> DUAL_TABLE_PLACER_ITEM_SPRUCE = ITEMS.register("workstation_placer_spruce", () -> new WorkstationPlacerItem("spruce"));
	public static final RegistryObject<Item> DUAL_TABLE_PLACER_ITEM_BIRCH = ITEMS.register("workstation_placer_birch", () -> new WorkstationPlacerItem("birch"));
	public static final RegistryObject<Item> DUAL_TABLE_PLACER_ITEM_JUNGLE = ITEMS.register("workstation_placer_jungle", () -> new WorkstationPlacerItem("jungle"));
	public static final RegistryObject<Item> DUAL_TABLE_PLACER_ITEM_DARK_OAK = ITEMS.register("workstation_placer_dark_oak", () -> new WorkstationPlacerItem("dark_oak"));

	public static final RegistryObject<Block> MINI_TOOL_RACK_OAK = BLOCKS.register("tool_rack_single_oak", () -> new ToolRack(2, "single"));
	public static final RegistryObject<Item> MINI_TOOL_RACK_ITEM_OAK = FromBlock(MINI_TOOL_RACK_OAK);
	public static final RegistryObject<Block> MINI_TOOL_RACK_SPRUCE = BLOCKS.register("tool_rack_single_spruce", () -> new ToolRack(2, "single"));
	public static final RegistryObject<Item> MINI_TOOL_RACK_ITEM_SPRUCE = FromBlock(MINI_TOOL_RACK_SPRUCE);
	public static final RegistryObject<Block> MINI_TOOL_RACK_BIRCH = BLOCKS.register("tool_rack_single_birch", () -> new ToolRack(2, "single"));
	public static final RegistryObject<Item> MINI_TOOL_RACK_ITEM_BIRCH = FromBlock(MINI_TOOL_RACK_BIRCH);
	public static final RegistryObject<Block> MINI_TOOL_RACK_JUNGLE = BLOCKS.register("tool_rack_single_jungle", () -> new ToolRack(2, "single"));
	public static final RegistryObject<Item> MINI_TOOL_RACK_ITEM_JUNGLE = FromBlock(MINI_TOOL_RACK_JUNGLE);
	public static final RegistryObject<Block> MINI_TOOL_RACK_DARK_OAK = BLOCKS.register("tool_rack_single_dark_oak", () -> new ToolRack(2, "single"));
	public static final RegistryObject<Item> MINI_TOOL_RACK_ITEM_DARK_OAK = FromBlock(MINI_TOOL_RACK_DARK_OAK);

	public static final RegistryObject<Block> FRAMED_TOOL_RACK_OAK = BLOCKS.register("tool_rack_framed_oak", () -> new DualToolRack(6, "framed"));
	public static final RegistryObject<Item> FRAMED_TOOL_RACK_ITEM_OAK = FromBlock(FRAMED_TOOL_RACK_OAK);
	public static final RegistryObject<Block> FRAMED_TOOL_RACK_SPRUCE = BLOCKS.register("tool_rack_framed_spruce", () -> new DualToolRack(6, "framed"));
	public static final RegistryObject<Item> FRAMED_TOOL_RACK_ITEM_SPRUCE = FromBlock(FRAMED_TOOL_RACK_SPRUCE);
	public static final RegistryObject<Block> FRAMED_TOOL_RACK_BIRCH = BLOCKS.register("tool_rack_framed_birch", () -> new DualToolRack(6, "framed"));
	public static final RegistryObject<Item> FRAMED_TOOL_RACK_ITEM_BIRCH = FromBlock(FRAMED_TOOL_RACK_BIRCH);
	public static final RegistryObject<Block> FRAMED_TOOL_RACK_JUNGLE = BLOCKS.register("tool_rack_framed_jungle", () -> new DualToolRack(6, "framed"));
	public static final RegistryObject<Item> FRAMED_TOOL_RACK_ITEM_JUNGLE = FromBlock(FRAMED_TOOL_RACK_JUNGLE);
	public static final RegistryObject<Block> FRAMED_TOOL_RACK_DARK_OAK = BLOCKS.register("tool_rack_framed_dark_oak", () -> new DualToolRack(6, "framed"));
	public static final RegistryObject<Item> FRAMED_TOOL_RACK_ITEM_DARK_OAK = FromBlock(FRAMED_TOOL_RACK_DARK_OAK);

	public static final RegistryObject<Block> PFRAMED_TOOL_RACK_OAK = BLOCKS.register("tool_rack_pframed_oak", () -> new DualToolRack(6, "pframed"));
	public static final RegistryObject<Item> PFRAMED_TOOL_RACK_ITEM_OAK = FromBlock(PFRAMED_TOOL_RACK_OAK);
	public static final RegistryObject<Block> PFRAMED_TOOL_RACK_SPRUCE = BLOCKS.register("tool_rack_pframed_spruce", () -> new DualToolRack(6, "pframed"));
	public static final RegistryObject<Item> PFRAMED_TOOL_RACK_ITEM_SPRUCE = FromBlock(PFRAMED_TOOL_RACK_SPRUCE);
	public static final RegistryObject<Block> PFRAMED_TOOL_RACK_BIRCH = BLOCKS.register("tool_rack_pframed_birch", () -> new DualToolRack(6, "pframed"));
	public static final RegistryObject<Item> PFRAMED_TOOL_RACK_ITEM_BIRCH = FromBlock(PFRAMED_TOOL_RACK_BIRCH);
	public static final RegistryObject<Block> PFRAMED_TOOL_RACK_JUNGLE = BLOCKS.register("tool_rack_pframed_jungle", () -> new DualToolRack(6, "pframed"));
	public static final RegistryObject<Item> PFRAMED_TOOL_RACK_ITEM_JUNGLE = FromBlock(PFRAMED_TOOL_RACK_JUNGLE);
	public static final RegistryObject<Block> PFRAMED_TOOL_RACK_DARK_OAK = BLOCKS.register("tool_rack_pframed_dark_oak", () -> new DualToolRack(6, "pframed"));
	public static final RegistryObject<Item> PFRAMED_TOOL_RACK_ITEM_DARK_OAK = FromBlock(PFRAMED_TOOL_RACK_DARK_OAK);

	public static final RegistryObject<Block> DUAL_TOOL_RACK_OAK = BLOCKS.register("tool_rack_double_oak", () -> new DualToolRack(6, "double"));
	public static final RegistryObject<Item> DUAL_TOOL_RACK_ITEM_OAK = FromBlock(DUAL_TOOL_RACK_OAK);
	public static final RegistryObject<Block> DUAL_TOOL_RACK_SPRUCE = BLOCKS.register("tool_rack_double_spruce", () -> new DualToolRack(6, "double"));
	public static final RegistryObject<Item> DUAL_TOOL_RACK_ITEM_SPRUCE = FromBlock(DUAL_TOOL_RACK_SPRUCE);
	public static final RegistryObject<Block> DUAL_TOOL_RACK_BIRCH = BLOCKS.register("tool_rack_double_birch", () -> new DualToolRack(6, "double"));
	public static final RegistryObject<Item> DUAL_TOOL_RACK_ITEM_BIRCH = FromBlock(DUAL_TOOL_RACK_BIRCH);
	public static final RegistryObject<Block> DUAL_TOOL_RACK_JUNGLE = BLOCKS.register("tool_rack_double_jungle", () -> new DualToolRack(6, "double"));
	public static final RegistryObject<Item> DUAL_TOOL_RACK_ITEM_JUNGLE = FromBlock(DUAL_TOOL_RACK_JUNGLE);
	public static final RegistryObject<Block> DUAL_TOOL_RACK_DARK_OAK = BLOCKS.register("tool_rack_double_dark_oak", () -> new DualToolRack(6, "double"));
	public static final RegistryObject<Item> DUAL_TOOL_RACK_ITEM_DARK_OAK = FromBlock(DUAL_TOOL_RACK_DARK_OAK);

	public static final RegistryObject<Block> POTION_SHELF_OAK = BLOCKS.register("potion_shelf_oak", () -> new PotionShelf());
	public static final RegistryObject<Item> POTION_SHELF_ITEM_OAK = FromBlock(POTION_SHELF_OAK);
	public static final RegistryObject<Block> POTION_SHELF_SPRUCE = BLOCKS.register("potion_shelf_spruce", () -> new PotionShelf());
	public static final RegistryObject<Item> POTION_SHELF_ITEM_SPRUCE = FromBlock(POTION_SHELF_SPRUCE);
	public static final RegistryObject<Block> POTION_SHELF_BIRCH = BLOCKS.register("potion_shelf_birch", () -> new PotionShelf());
	public static final RegistryObject<Item> POTION_SHELF_ITEM_BIRCH = FromBlock(POTION_SHELF_BIRCH);
	public static final RegistryObject<Block> POTION_SHELF_JUNGLE = BLOCKS.register("potion_shelf_jungle", () -> new PotionShelf());
	public static final RegistryObject<Item> POTION_SHELF_ITEM_JUNGLE = FromBlock(POTION_SHELF_JUNGLE);
	public static final RegistryObject<Block> POTION_SHELF_DARK_OAK = BLOCKS.register("potion_shelf_dark_oak", () -> new PotionShelf());
	public static final RegistryObject<Item> POTION_SHELF_ITEM_DARK_OAK = FromBlock(POTION_SHELF_DARK_OAK);

	private static RegistryObject<Item> FromBlock(RegistryObject<Block> block)
	{
		Item.Properties properties = new Item.Properties().tab(CreativeTab.TAB_WORKSHOP);
		return ITEMS.register(block.getId().getPath(), () -> new BlockItemEx(block.get(), properties));
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final RegistryObject<BlockEntityType<ToolRackBlockEntity>> TOOL_RACK_BE = BLOCK_ENTITIES.register("tool_rack_be", () -> BlockEntityType.Builder.of(ToolRackBlockEntity::new,
				DUAL_TOOL_RACK_OAK.get(), DUAL_TOOL_RACK_SPRUCE.get(), DUAL_TOOL_RACK_JUNGLE.get(), DUAL_TOOL_RACK_BIRCH.get(), DUAL_TOOL_RACK_DARK_OAK.get(),
				FRAMED_TOOL_RACK_OAK.get(), FRAMED_TOOL_RACK_SPRUCE.get(), FRAMED_TOOL_RACK_JUNGLE.get(), FRAMED_TOOL_RACK_BIRCH.get(), FRAMED_TOOL_RACK_DARK_OAK.get(),
				PFRAMED_TOOL_RACK_OAK.get(), PFRAMED_TOOL_RACK_SPRUCE.get(), PFRAMED_TOOL_RACK_JUNGLE.get(), PFRAMED_TOOL_RACK_BIRCH.get(), PFRAMED_TOOL_RACK_DARK_OAK.get(),
				MINI_TOOL_RACK_OAK.get(), MINI_TOOL_RACK_SPRUCE.get(), MINI_TOOL_RACK_JUNGLE.get(), MINI_TOOL_RACK_BIRCH.get(), MINI_TOOL_RACK_DARK_OAK.get()
			).build(null));
	public static final RegistryObject<BlockEntityType<SimpleTableBlockEntity>> SIMPLE_TABLE_BE = BLOCK_ENTITIES.register("simple_table_be", () -> BlockEntityType.Builder.of(SimpleTableBlockEntity::new,
			SIMPLE_TABLE_OAK.get(), SIMPLE_TABLE_SPRUCE.get(), SIMPLE_TABLE_BIRCH.get(), SIMPLE_TABLE_JUNGLE.get(), SIMPLE_TABLE_DARK_OAK.get()).build(null));
	public static final RegistryObject<BlockEntityType<DualTableBlockEntity>> DUAL_TABLE_BE = BLOCK_ENTITIES.register("dual_table_be", () -> BlockEntityType.Builder.of(DualTableBlockEntity::new,
			DUAL_TABLE_PRIMARY_OAK.get(), DUAL_TABLE_PRIMARY_SPRUCE.get(), DUAL_TABLE_PRIMARY_BIRCH.get(), DUAL_TABLE_PRIMARY_JUNGLE.get(), DUAL_TABLE_PRIMARY_DARK_OAK.get()).build(null));
	public static final RegistryObject<BlockEntityType<PotionShelfBlockEntity>> POTION_SHELF_BE = BLOCK_ENTITIES.register("potion_shelf_be", () -> BlockEntityType.Builder.of(PotionShelfBlockEntity::new,
			POTION_SHELF_OAK.get(), POTION_SHELF_SPRUCE.get(), POTION_SHELF_JUNGLE.get(), POTION_SHELF_BIRCH.get(), POTION_SHELF_DARK_OAK.get()
	).build(null));
	public static final RegistryObject<MenuType<SimpleTableMenu>> CRAFTING_SINGLE_MENU_TYPE = CONTAINER_TYPES.register("crafting_single", () -> IForgeMenuType.create(SimpleTableMenu::new));
	public static final RegistryObject<MenuType<DualTableMenu>> CRAFTING_DUAL_MENU_TYPE = CONTAINER_TYPES.register("crafting_dual", () -> IForgeMenuType.create(DualTableMenu::new));

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final RegistryObject<RecipeSerializer<UnsupportedWoodRecipe>> TABLE_RECIPE = RECIPES.register("table_recipe_unknown_planks", ()-> new SimpleRecipeSerializer<UnsupportedWoodRecipe>(UnsupportedWoodRecipe::new));
}
