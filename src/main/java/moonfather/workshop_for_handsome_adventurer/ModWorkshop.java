package moonfather.workshop_for_handsome_adventurer;

import com.mojang.logging.LogUtils;
import moonfather.workshop_for_handsome_adventurer.block_entities.messaging.MessagingInitialization;
import moonfather.workshop_for_handsome_adventurer.blocks.PotionShelf;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.FinderEvents;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.MissingMappingsHandler;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.config.DynamicAssetClientConfig;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.config.DynamicAssetCommonConfig;
import moonfather.workshop_for_handsome_adventurer.initialization.CommonSetup;
import moonfather.workshop_for_handsome_adventurer.initialization.DynamicContentRegistration;
import moonfather.workshop_for_handsome_adventurer.initialization.ContentRegistration;
import moonfather.workshop_for_handsome_adventurer.integration.CarryOnBlacklisting;
import moonfather.workshop_for_handsome_adventurer.integration.TOPProxyRegistration;
import moonfather.workshop_for_handsome_adventurer.items.task_list.RegistrationForTaskList;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.MissingMappingsHandler2;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.TaskListItem;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListMessagingInitialization;
import moonfather.workshop_for_handsome_adventurer.other.CreativeTab;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;


@Mod(Constants.MODID)
public class ModWorkshop
{
    private static final Logger LOGGER = LogUtils.getLogger();



    // todo: 1.21.8  added camera to DualTableTESR;  not using it.
    // todo: 1.21.8  InventoryAccessHelper.getItemFromNamedSlot  now gets chest slot and leggings differently
    //               model.isGui3d() in TR TESR is changed and i don't know what to replace it with     ---- https://docs.neoforged.net/docs/resources/client/models/items/
    //               test TL saving thoroughly.  also toolrack saving/loading.
    //               TR canDepositItem might be too strict now
    //               checkboxes - blit in TaskListScreen, maybe remove last two args
    //               interaction with tetra hammer is fixed - it was stupid in all  old versions
    //               potion cloning disabled
    //               todo: rework tetra creation when tetra is up.
    //---------------------------------------------------------------//
    //   https://modrinth.com/mod/frycooks-delight

    // test keyboard in rename box, test f and e



    public ModWorkshop(IEventBus modBus, ModContainer modContainer)
    {
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.STARTUP, DynamicAssetClientConfig.SPEC, "workshop/special-client.toml");
        modContainer.registerConfig(ModConfig.Type.STARTUP, DynamicAssetCommonConfig.SPEC, "workshop/special-server.toml");
        ContentRegistration.init(modBus);
        modBus.addListener(CommonSetup::init);
        modBus.addListener(CarryOnBlacklisting::enqueueIMC);
        modBus.addListener(TOPProxyRegistration::enqueueIMC);
        modBus.addListener(CreativeTab::onCreativeTabPopulation);
        modBus.addListener(FinderEvents::addServerPack);
        modBus.addListener(MessagingInitialization::register);
        NeoForge.EVENT_BUS.addListener(TaskListItem.Utility::initializeStupidDamageTypes);
        NeoForge.EVENT_BUS.addListener(PotionShelf::onRightClickBlock);
        modBus.addListener(EventPriority.LOWEST, DynamicContentRegistration::handleRegistryEvent);
        MissingMappingsHandler.read();
		
        RegistrationForTaskList.init(modBus);
        modBus.addListener(TaskListMessagingInitialization::register);
        modBus.addListener(EventPriority.LOWEST, MissingMappingsHandler2::handleRegistryEvent);
    }
}
