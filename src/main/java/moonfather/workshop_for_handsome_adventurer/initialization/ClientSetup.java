package moonfather.workshop_for_handsome_adventurer.initialization;

import moonfather.workshop_for_handsome_adventurer.ClientConfig;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import moonfather.workshop_for_handsome_adventurer.block_entities.screens.DualTableCraftingScreen;
import moonfather.workshop_for_handsome_adventurer.block_entities.screens.SimpleTableCraftingScreen;
import moonfather.workshop_for_handsome_adventurer.block_entities.renderers.*;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.FinderEvents;
import moonfather.workshop_for_handsome_adventurer.integration.PolymorphAccessorClient;
import moonfather.workshop_for_handsome_adventurer.other.InWorldTooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientSetup
{
	@SubscribeEvent
	public static void Initialize(FMLClientSetupEvent event)
	{
		if (ModList.get().isLoaded("polymorph"))
		{
			PolymorphAccessorClient.register();
		}
	}



	@SubscribeEvent
	public static void RegisterScreens(RegisterMenuScreensEvent event)
	{
		event.register(ContentRegistration.CRAFTING_SINGLE_MENU_TYPE.get(), SimpleTableCraftingScreen::create1);
		event.register(ContentRegistration.CRAFTING_DUAL_MENU_TYPE.get(), DualTableCraftingScreen::create2);
	}



	@SubscribeEvent
	public static void RegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		event.registerBlockEntityRenderer(ContentRegistration.TOOL_RACK_BE.get(), ToolRackTESR::new);
		event.registerBlockEntityRenderer(ContentRegistration.POTION_SHELF_BE.get(), ToolRackTESR::new);
		event.registerBlockEntityRenderer(ContentRegistration.DISC_SHELF_BE.get(), DiscShelfTESR::new);
		event.registerBlockEntityRenderer(ContentRegistration.DUAL_TABLE_BE.get(), DualTableTESR::new);
		event.registerBlockEntityRenderer(ContentRegistration.SIMPLE_TABLE_BE.get(), SimpleTableTESR::new);
	}


	// two slot textures are in minecraft's block atlas. i wanted to add my own, but it's way too much trouble.


	@SubscribeEvent
	public static void RegisterGuiLayers(RegisterGuiLayersEvent event)
	{
		if (! ClientConfig.ownWorldTooltipForceDisabled)
		{
			if (ClientConfig.ownWorldTooltipForceEnabled
				|| (! ModList.get().isLoaded("jade") && ! ModList.get().isLoaded("theoneprobe") && ! ModList.get().isLoaded("wthit")))
			{
				event.registerAboveAll(Identifier.fromNamespaceAndPath(Constants.MODID, "world_tooltip"), InWorldTooltip.getInstance());
			}
		}
	}


	@SubscribeEvent
	public static void AddClientPack(final AddPackFindersEvent event)
	{
		FinderEvents.addClientPack(event);
	}
}
