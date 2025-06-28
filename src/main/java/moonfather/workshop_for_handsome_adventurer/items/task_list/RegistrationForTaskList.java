package moonfather.workshop_for_handsome_adventurer.items.task_list;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities.TaskListBlockEntity;
import moonfather.workshop_for_handsome_adventurer.items.task_list.blocks.TaskListPanel;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.crafting.TaskListPlusCreamRecipe;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.crafting.TaskListPlusPaperRecipe;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.TaskListItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class RegistrationForTaskList
{
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MODID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Constants.MODID);


    public static void init(IEventBus modBus)
    {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        RECIPES.register(modBus);
    }



    public static final Supplier<Item> TASK_LIST = ITEMS.register("task_list", () -> new TaskListItem(false));
    public static final Supplier<Item> TASK_LIST_FI = ITEMS.register("task_list_fi", () -> new TaskListItem(true));
    public  static final Supplier<Block> TASK_LIST_PANEL = BLOCKS.register("task_list_panel", TaskListPanel::new);


    public static final Supplier<RecipeSerializer<TaskListPlusPaperRecipe>> TASK_LIST_EXPANSION_RECIPE = RECIPES.register("task_list_ex", () -> new SimpleCraftingRecipeSerializer<TaskListPlusPaperRecipe>(TaskListPlusPaperRecipe::new));
    public static final Supplier<RecipeSerializer<TaskListPlusCreamRecipe>> TASK_LIST_CREAMING_RECIPE = RECIPES.register("task_list_creaming", () -> new SimpleCraftingRecipeSerializer<TaskListPlusCreamRecipe>(TaskListPlusCreamRecipe::new));


    public static final Supplier<BlockEntityType<TaskListBlockEntity>> TASK_LIST_PANEL_BE = BLOCK_ENTITIES.register("task_list_panel_be", () -> BlockEntityType.Builder.of(TaskListBlockEntity::new, TASK_LIST_PANEL.get()).build(null));
}
