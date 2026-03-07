package moonfather.workshop_for_handsome_adventurer.items.task_list;

import moonfather.workshop_for_handsome_adventurer.ClientConfig;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities.renderers.TaskListPanelTESR;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.Map;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup
{
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        if (ClientConfig.taskListItemsAreDrawnOnWall.getAsBoolean())
        {
            event.registerBlockEntityRenderer(RegistrationForTaskList.TASK_LIST_PANEL_BE.get(), TaskListPanelTESR::new);
        }
    }

    @SubscribeEvent
    public static void messWithBaking(ModelEvent.ModifyBakingResult event)
    {
        if (! ClientConfig.taskListItemsAreDrawnOnWall.getAsBoolean())
        {
            // this replaces empty task list model with onw with fake text
            ModelResourceLocation key1;
            for (Map.Entry<ModelResourceLocation, BakedModel> i : event.getModels().entrySet())
            {
                key1 = i.getKey();
                if (key1.id().getNamespace().equals(Constants.MODID) && key1.variant().contains("empty=true"))  // "task_list"
                {
                    ModelResourceLocation key2 = new ModelResourceLocation(key1.id(), key1.variant().replace("true", "false"));
                    event.getModels().put(key1, event.getModels().get(key2));
                }
            }
        }
    }
}
