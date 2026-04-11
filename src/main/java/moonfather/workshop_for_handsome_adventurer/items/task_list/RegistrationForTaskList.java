package moonfather.workshop_for_handsome_adventurer.items.task_list;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities.TaskListBlockEntity;
import moonfather.workshop_for_handsome_adventurer.items.task_list.blocks.TaskListPanel;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.TaskListItem;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.crafting.TaskListPlusCreamRecipe;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.crafting.TaskListPlusPaperRecipe;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RegistrationForTaskList
{
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Constants.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Constants.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.MODID);
    private static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Constants.MODID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Constants.MODID);

    public static void init(IEventBus modBus)
    {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        RECIPES.register(modBus);
        DATA_COMPONENT_TYPES.register(modBus);
    }

    public static final Supplier<Item> TASK_LIST = ITEMS.register("task_list", () -> new TaskListItem("task_list"));
    public  static final Supplier<Block> TASK_LIST_PANEL = BLOCKS.register("task_list_panel", () -> new TaskListPanel("task_list_panel"));

    public static final Supplier<DataComponentType<TaskListComponent>> TASK_LIST_CONTENT = DATA_COMPONENT_TYPES.registerComponentType("task_list_data", builder -> builder.persistent(TaskListComponent.CODEC_FOR_COMPONENT).networkSynchronized(TaskListComponent.STREAM_CODEC_FOR_COMPONENT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> FIRE_RESISTANT = DATA_COMPONENT_TYPES.register("fire_resistant", () -> DataComponentType.<Unit>builder().persistent(Unit.CODEC).networkSynchronized(Unit.STREAM_CODEC).build() );
    public static final Supplier<RecipeSerializer<TaskListPlusPaperRecipe>> TASK_LIST_EXPANSION_RECIPE = RECIPES.register("task_list_ex", () -> TaskListPlusPaperRecipe.SERIALIZER);
    public static final Supplier<RecipeSerializer<TaskListPlusCreamRecipe>> TASK_LIST_CREAMING_RECIPE = RECIPES.register("task_list_creaming", () -> TaskListPlusCreamRecipe.SERIALIZER);

    public static final Supplier<BlockEntityType<TaskListBlockEntity>> TASK_LIST_PANEL_BE = BLOCK_ENTITIES.register("task_list_panel_be", () -> new BlockEntityType<>(TaskListBlockEntity::new, TASK_LIST_PANEL.get()));
}
