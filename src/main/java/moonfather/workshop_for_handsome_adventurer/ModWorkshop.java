package moonfather.workshop_for_handsome_adventurer;

import com.mojang.logging.LogUtils;
import moonfather.workshop_for_handsome_adventurer.block_entities.DualTableMenu;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import moonfather.workshop_for_handsome_adventurer.blocks.PotionShelf;
import moonfather.workshop_for_handsome_adventurer.initialization.ClientSetup;
import moonfather.workshop_for_handsome_adventurer.initialization.CommonSetup;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import moonfather.workshop_for_handsome_adventurer.integration.TOPRegistration;
import net.minecraft.nbt.CompoundTag;
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
    //--later--
    //after 1.0: render items on top of tables
    //after 1.0: todo list similar to one in BiblioCraft     (edit: apparently there will be one in create mod soon so i might cross this off)
    //after 1.0: bookshelf like the one in BiblioCraft; no gui; (edit: apparently exactly that is coming in 1.20)   DONE IN 1.05
    //after 1.1: double the number of supported adjacent chests
	//after 1.1: chest markers (similar to item frames); maybe? (no gui, pull items from chest)
    //maybe after 1.1: storage drawer support. maybe. not sure how. in any case, only the closest blocks.
    //maybe after 1.2: tetra tables in wood variants


    // todo: consider - i can use furnaces from crafting tables. maybe forbid that? what condition
    // todo: consider packingtape addon/support
    // todo: unbolt 6 from potion shelf

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
        MinecraftForge.EVENT_BUS.addListener(PotionShelf::onRightClickBlock);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        for (String woodType: Registration.woodTypes)
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
