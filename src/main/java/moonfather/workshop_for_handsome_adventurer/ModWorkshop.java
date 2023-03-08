package moonfather.workshop_for_handsome_adventurer;

import com.mojang.logging.LogUtils;
import moonfather.workshop_for_handsome_adventurer.initialization.ClientSetup;
import moonfather.workshop_for_handsome_adventurer.initialization.CommonSetup;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import moonfather.workshop_for_handsome_adventurer.integration.TOPRegistration;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.stream.Collectors;

@Mod(Constants.MODID)
public class ModWorkshop
{
    //NOW         starting double table

    //--all-- --MUST--
    //...
    //--all-- --NTH--
    //todo: woodcutter recipes

    //--rack-- --MUST--
    //potion shelf shift for all
    //...
    //--rack-- --NTH--
    //.....
    //check offhand how feels -> not well

    //--simpletable--  --MUST--
    //...
    //--simpletable--  --NTH--
    //.....

    //--dualtable--  --MUST--
    //todo: customization slot, accepts chest/drawer / spyglass(?) / spyglass (2) / 1x lantern
    //todo: lantern gives both top blocks 10 light
    //todo: chest mode visible
    //todo: BE stores items
    //todo: TC widget windows
    //todo: JEI places items
    //todo: WTHIT is entirely unsupported, jei and top are fine ootb
    //--dualtable--  --NTH--
    //consider: right click top to turn lamps off/on
    //consider: nine storage slots, other than crafting slots
    //issue: Rubidium causes z-fighting (confirmed it's not any other mod)

    //--before--pub--
    //todo: printLn, CR in toml, ver
    //--known-- issues
    //when you have two-block rack and you're looking so that the top half isn't visible, items disappear.
    //integration: crescent hammer (thermal) won't go onto the rack.  it's fine.
    //--later--
    //maybe after 1.1: sword rack?    todo list?
    //after 1.0 rename chests at the cost of 1 XPL
    //after 1.0: items on top of tables


    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public ModWorkshop()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, OptionsHolder.COMMON_SPEC);
        Registration.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CommonSetup::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::Initialize));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::RegisterRenderers));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::StitchTextures));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        InterModComms.sendTo("carryon", "blacklistBlock", () -> Constants.MODID + ":dual_table_secondary");
        InterModComms.sendTo("carryon", "blacklistBlock", () -> Constants.MODID + ":dual_table_top");
        String[] woodTypes = {"oak", "spruce", "jungle", "birch", "dark_oak"};
        for (String woodType: woodTypes)
        {
            InterModComms.sendTo("carryon", "blacklistBlock", () -> Constants.MODID + ":tool_rack_double_" + woodType);
            InterModComms.sendTo("carryon", "blacklistBlock", () -> Constants.MODID + ":tool_rack_framed_" + woodType);
            InterModComms.sendTo("carryon", "blacklistBlock", () -> Constants.MODID + ":tool_rack_pframed_" + woodType);
            InterModComms.sendTo("carryon", "blacklistBlock", () -> Constants.MODID + ":dual_table_primary_" + woodType);
        }
        /////
        if (ModList.get().isLoaded("theoneprobe"))
        {
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPRegistration::instance);
        }
    } 
}
