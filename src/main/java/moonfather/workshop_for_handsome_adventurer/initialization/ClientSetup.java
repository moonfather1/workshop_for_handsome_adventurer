package moonfather.workshop_for_handsome_adventurer.initialization;

import moonfather.workshop_for_handsome_adventurer.block_entities.screens.DualTableCraftingScreen;
import moonfather.workshop_for_handsome_adventurer.block_entities.screens.SimpleTableCraftingScreen;
import moonfather.workshop_for_handsome_adventurer.block_entities.ToolRackTESR;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.client.event.EntityRenderersEvent;
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

	// two slot textures are in minecraft's block atlas. i wanted to add my own, but it's way too much trouble.
}
