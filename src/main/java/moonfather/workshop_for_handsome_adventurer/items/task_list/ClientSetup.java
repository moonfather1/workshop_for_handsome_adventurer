package moonfather.workshop_for_handsome_adventurer.items.task_list;

import moonfather.workshop_for_handsome_adventurer.ClientConfig;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities.renderers.TaskListPanelTESR;
import moonfather.workshop_for_handsome_adventurer.items.task_list.blocks.TaskListPanel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientSetup
{
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        if (ClientConfig.taskListItemsAreDrawnOnWall)
        {
            event.registerBlockEntityRenderer(RegistrationForTaskList.TASK_LIST_PANEL_BE.get(), TaskListPanelTESR::new);
        }
    }

    @SubscribeEvent
    public static void messWithBaking(ModelEvent.ModifyBakingResult event)
    {
        if (! ClientConfig.taskListItemsAreDrawnOnWall)
        {
            // this replaces empty task list model with onw with fake text
            for (BlockState key1 : event.getBakingResult().blockStateModels().keySet())
            {
                if (key1.toString().contains(Constants.MODID) && key1.toString().contains("empty=true"))  // "task_list"
                {
                    BlockState key2 = key1.setValue(TaskListPanel.EMPTY, false);
                    event.getBakingResult().blockStateModels().put(key1, event.getBakingResult().blockStateModels().get(key2));
                }
            }
        }
    }
}
