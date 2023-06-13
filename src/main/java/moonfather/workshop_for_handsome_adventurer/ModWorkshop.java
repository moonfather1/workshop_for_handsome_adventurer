package moonfather.workshop_for_handsome_adventurer;

import com.mojang.logging.LogUtils;
import moonfather.workshop_for_handsome_adventurer.blocks.PotionShelf;
import moonfather.workshop_for_handsome_adventurer.initialization.ClientSetup;
import moonfather.workshop_for_handsome_adventurer.initialization.CommonSetup;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import moonfather.workshop_for_handsome_adventurer.integration.TOPRegistration;
import moonfather.workshop_for_handsome_adventurer.other.CreativeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;


@Mod(Constants.MODID)
public class ModWorkshop
{
    //bug !- xp nuggets in recipes
    //bug !- workbench and bare hand
    //MFO FD board, 1.18 create
    //--before--pub--
    //todo: printLn, CR in toml, ver
    //--known-- issues
    //integration: Carry On blacklist is broken in 1.17.0.8 and a few older versions. do not pick up big tables and racks, if you do, type /kill. (actually works on dedicated servers but broken on integrated.)  Carry On developer will fix the blacklist eventually.
    //integration: WTHIT is entirely unsupported, Jade and TOP are fine. it's not my fault, i wanted to add support but i can not. blame wthit author while you switch to an alternative.
    //vanilla behavior: when you have two-block rack and you're looking so that the top half isn't visible, items disappear. i think it's not that much of a problem.
    //integration: crescent hammer (thermal) won't go onto the rack.  it's fine.
	//integration: items in tetra belt storage are shown in different order than what you might expect. tetra displays rows from bottom to top (bottom-most holds items that come first in storage); while we could make a dedicated container which re-maps slot numbers, there is still an issue of us having 9 slots per row and original gui having 8. if people really want, we can fix it.
	//integration: items in toolboxes from create mod are shown weird. not our fault, create internally uses 4 slots of 64 items and shows one slot with a max of 256. we could make a dedicated container for that but it won't be easy. maybe for a large number of downloads.
    //--later--
    //after 1.0: render items on top of tables
    //after 1.0: todo list similar to one in BiblioCraft     (edit: apparently there will be one in create mod soon so i might cross this off)
    //after 1.0: bookshelf like the one in BiblioCraft; no gui; (edit: apparently exactly that is coming in 1.20)
    //after 1.1: double the number of supported adjacent chests
	//after 1.1: chest markers (similar to item frames); maybe? (no gui, pull items from chest)
    //maybe after 1.1: storage drawer support. maybe. not sure how. in any case, only the closest blocks.
    //maybe after 1.1: sword rack? likely not. (decorative, single sword, horizontal, no gui)
    //maybe after 1.2: tetra tables in wood variants
    //will not: weapon rack (vertical weapons),  sword and shield on wall... j


    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public ModWorkshop()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, OptionsHolder.COMMON_SPEC);
        Registration.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CommonSetup::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::Initialize));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::RegisterRenderers));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CreativeTab::OnCreativeTabRegistration);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CreativeTab::OnCreativeTabPopulation);
        MinecraftForge.EVENT_BUS.addListener(PotionShelf::onRightClickBlock);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        String[] woodTypes = {"oak", "spruce", "jungle", "birch", "dark_oak"};

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
}
