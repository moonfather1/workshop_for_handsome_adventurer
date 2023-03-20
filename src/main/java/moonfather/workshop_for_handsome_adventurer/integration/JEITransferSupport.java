package moonfather.workshop_for_handsome_adventurer.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import moonfather.workshop_for_handsome_adventurer.block_entities.DualTableMenu;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEITransferSupport implements IModPlugin
{
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
	{
		registration.addRecipeTransferHandler(SimpleTableMenu.class, RecipeTypes.CRAFTING, SimpleTableMenu.CRAFT_SLOT_START, 9, SimpleTableMenu.INV_SLOT_START, 9*4);
		registration.addRecipeTransferHandler(DualTableMenu.class, RecipeTypes.CRAFTING, DualTableMenu.CRAFT_TERTIARY_SLOT_START, 9+1, DualTableMenu.INV_SLOT_START, 9*4);
	}

	@Override
	public ResourceLocation getPluginUid()
	{
		return new ResourceLocation("workshop_for_handsome_adventurer:jei_transfer_plugin");
	}
}
