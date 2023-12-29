package moonfather.workshop_for_handsome_adventurer;

import com.mojang.logging.LogUtils;
import moonfather.workshop_for_handsome_adventurer.blocks.PotionShelf;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.FinderEvents;
import moonfather.workshop_for_handsome_adventurer.initialization.ClientSetup;
import moonfather.workshop_for_handsome_adventurer.initialization.CommonSetup;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import moonfather.workshop_for_handsome_adventurer.integration.TOPRegistration;
import net.minecraft.core.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;


@Mod(Constants.MODID)
public class ModWorkshop
{
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public ModWorkshop()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, OptionsHolder.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, OptionsHolder.CLIENT_SPEC);
        Registration.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CommonSetup::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::Initialize));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::RegisterRenderers));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::StitchTextures));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::AddClientPack));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        MinecraftForge.EVENT_BUS.addListener(PotionShelf::onRightClickBlock);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(FinderEvents::addServerPack);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOWEST, this::registerDynamicItems);
    }

    //// support for CarryOn, etc.
    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        String[] woodTypes = {"oak", "spruce", "jungle", "birch", "dark_oak", "mangrove"};

        for (String woodType: woodTypes)
        {
            InterModComms.sendTo("carryon", "blacklistBlock", () -> Constants.MODID + ":tool_rack_double_" + woodType);
            InterModComms.sendTo("carryon", "blacklistBlock", () -> Constants.MODID + ":tool_rack_framed_" + woodType);
            InterModComms.sendTo("carryon", "blacklistBlock", () -> Constants.MODID + ":tool_rack_pframed_" + woodType);
            InterModComms.sendTo("carryon", "blacklistBlock", () -> Constants.MODID + ":dual_table_bottom_left_" + woodType);
            InterModComms.sendTo("carryon", "blacklistBlock", () -> Constants.MODID + ":dual_table_bottom_right_" + woodType);
            InterModComms.sendTo("carryon", "blacklistBlock", () -> Constants.MODID + ":dual_table_top_left_" + woodType);
            InterModComms.sendTo("carryon", "blacklistBlock", () -> Constants.MODID + ":dual_table_top_right_" + woodType);
        }
        //System.out.println("test imc " + Constants.MODID + ":dual_table_bottom_left_" + "oak   " + ForgeRegistries.BLOCKS.getValue(new ResourceLocation(Constants.MODID + ":dual_table_bottom_left_" + "oak")).getCloneItemStack(null, null, null, null, null));
        if (ModList.get().isLoaded("theoneprobe"))
        {
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPRegistration::instance);
        }
    }



    //// tables/racks/shelves for 3rd party woods
    private void registerDynamicItems(final RegisterEvent event)
    {
        if (event.getRegistryKey().equals(Registry.ITEM_REGISTRY))  // Registries.BLOCK is too early
        {
            Registration.registerBlocksForThirdPartyWood(event);
        }
    }
}
