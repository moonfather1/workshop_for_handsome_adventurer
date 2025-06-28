package moonfather.workshop_for_handsome_adventurer.items.task_list;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.OptionsHolder;
import moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities.renderers.TaskListPanelTESR;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup
{
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        // nothing. stupid client config isn't loaded. see FMLClientSetupEvent below.
    }

    @SubscribeEvent
    public static void registerRenderers2(FMLClientSetupEvent event)
    {
        // because in EntityRenderersEvent.RegisterRenderers event, stupid client config isn't loaded.
        if (OptionsHolder.CLIENT.TaskListItemsAreDrawnOnWall.get())
        {
            BlockEntityRenderers.register(RegistrationForTaskList.TASK_LIST_PANEL_BE.get(), TaskListPanelTESR::new);
        }
    }

    @SubscribeEvent
    public static void messWithBaking(ModelEvent.ModifyBakingResult event)
    {
        if (! OptionsHolder.CLIENT.TaskListItemsAreDrawnOnWall.get())
        {
            // this replaces empty task list model with onw with fake text
            ResourceLocation key1;
            for (Map.Entry<ResourceLocation, BakedModel> i : event.getModels().entrySet())
            {
                key1 = i.getKey();
                if (key1.getNamespace().equals(Constants.MODID) && key1 instanceof ModelResourceLocation mkey1)
                {
                    if (mkey1.getVariant().contains("empty=true"))  // "task_list", none of my other blocks have that
                    {
                        ModelResourceLocation mkey2 = new ModelResourceLocation(mkey1.getNamespace(), mkey1.getPath(), mkey1.getVariant().replace("true", "false"));
                        event.getModels().put(mkey1, event.getModels().get(mkey2));
                    }
                }
            }
        }
    }
}
