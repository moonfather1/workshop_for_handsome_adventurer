package moonfather.workshop_for_handsome_adventurer.initialization;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.DualTableCraftingScreen;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableCraftingScreen;
import moonfather.workshop_for_handsome_adventurer.block_entities.ToolRackTESR;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup
{
	//public static final MenuType<CraftingMenu> CRAFTING1 = register("crafting1", SimpleTableMenu::new);

	public static void Initialize(FMLClientSetupEvent event)
	{
		event.enqueueWork(() -> MenuScreens.register(Registration.CRAFTING_SINGLE_MENU_TYPE.get(), SimpleTableCraftingScreen::new));
		event.enqueueWork(() -> MenuScreens.register(Registration.CRAFTING_DUAL_MENU_TYPE.get(), DualTableCraftingScreen::new));
		//ItemBlockRenderTypes.setRenderLayer(Registration.SIMPLE_TABLE.get(), RenderType.cutoutMipped()); // apparently unnecessary.
		//BlockEntityRenderers.register(Registration.TOOL_RACK.get(), ToolRackTESR::new); //maybe this would be ok too
	}



	public static void RegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		event.registerBlockEntityRenderer(Registration.TOOL_RACK_BE.get(),	ToolRackTESR::new);
		event.registerBlockEntityRenderer(Registration.POTION_SHELF_BE.get(),	ToolRackTESR::new);
	}

	public static void StitchTextures(TextureStitchEvent.Pre event)
	{
		if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
			event.addSprite(new ResourceLocation(Constants.MODID, "gui/c_slot"));
		}
	}
}
