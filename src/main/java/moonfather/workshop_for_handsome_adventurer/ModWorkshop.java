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
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import moonfather.workshop_for_handsome_adventurer.integration.CarryOnBlacklisting;
import moonfather.workshop_for_handsome_adventurer.integration.TOPProxyRegistration;
import moonfather.workshop_for_handsome_adventurer.integration.TOPRegistration;
import moonfather.workshop_for_handsome_adventurer.items.task_list.RegistrationForTaskList;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.MissingMappingsHandler2;
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


    // todo: 1.21.8 as above in simplebutton we used DefaultTooltipPositioner.INSTANCE
    // todo: 1.21.8  +   JEITransferInfo ima sumnjiv novi tip. x2
    // todo: 1.21.8  TL model switching changed    if works, remove old code
    // todo: 1.21.8  +   toolrack save/load
    // todo: 1.21.8  added camera to DualTableTESR;  not using it.
    // todo: 1.21.8  InventoryAccessHelper.getItemFromNamedSlot  now gets chest slot and leggings differently
    //               assi  record.ItemChest = be.getBlockState().getCloneItemStack  is different  for tab icons
    //               direction = Direction.getNearest is different in simpleTableTESR when we decide direction to turn items
    //               model.isGui3d() in TR TESR is changed and i don't know what to replace it with     ---- https://docs.neoforged.net/docs/resources/client/models/items/
    //               test TL saving thoroughly.  also toolrack saving/loading.
    //               TR canDepositItem might be too strict now
    //               verify bookshelf drops
    //               test dual toolrack at world bottom
    //               test dual table at world top
    //               checkboxes - blit in TaskListScreen, maybe remove last two args
    //               using TextureAtlas.LOCATION_BLOCKS in SpecialFirstEverRenderer is sus
    //               interaction with tetra hammer is fixed - it was stupid in all  old versions
    //               remove commented out stuff from InventoryAccessComponent.render()
    //               customization tooltips are diff from a bunch i fixed already
    //               jei types are likely broken
    //               potion cloning disabled
    //               todo: rework tetra creation when tetra is up.
    //               try block desc prefix
    //---------------------------------------------------------------//
    //   https://modrinth.com/mod/frycooks-delight


    //https://www.reddit.com/r/MinecraftMod/comments/1p91nz1/spent_a_year_making_a_musicmod_for_minecraft_c418/

    // 1.21.1 : does rename update tabs?
    // test placer on 319
    // test keyboard in rename box, test f and e
    // !   missing tables don't turn to oak.   verity in 1.21.1
    //   ? bookshelf recipes
    // sd in lang, emba in lang
    // https://modrinth.com/mod/ars-elixirum

    ////////////////
    //  BESRs different
    //  can't read from IModFile
    //  test first renderer (atlas a little different)
    public ModWorkshop(IEventBus modBus, ModContainer modContainer)
    {
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.STARTUP, DynamicAssetClientConfig.SPEC, "workshop/special-client.toml");
        modContainer.registerConfig(ModConfig.Type.STARTUP, DynamicAssetCommonConfig.SPEC, "workshop/special-server.toml");
        Registration.init(modBus);
        modBus.addListener(CommonSetup::init);
        modBus.addListener(CarryOnBlacklisting::enqueueIMC);
        modBus.addListener(TOPProxyRegistration::enqueueIMC);
        modBus.addListener(CreativeTab::onCreativeTabPopulation);
        modBus.addListener(FinderEvents::addServerPack);
        modBus.addListener(MessagingInitialization::register);
        NeoForge.EVENT_BUS.addListener(PotionShelf::onRightClickBlock);
        modBus.addListener(EventPriority.LOWEST, DynamicContentRegistration::handleRegistryEvent);
        MissingMappingsHandler.read();
		
        RegistrationForTaskList.init(modBus);
        modBus.addListener(TaskListMessagingInitialization::register);
        modBus.addListener(EventPriority.LOWEST, MissingMappingsHandler2::handleRegistryEvent);
    }
}
