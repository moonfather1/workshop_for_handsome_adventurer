package moonfather.workshop_for_handsome_adventurer.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEITransferSupport implements IModPlugin
{
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
	{
		registration.addRecipeTransferHandler(SimpleTableMenu.class, VanillaRecipeCategoryUid.CRAFTING, 1, 9, 10, 36);
	}

	@Override
	public ResourceLocation getPluginUid()
	{
		return new ResourceLocation("workshop_for_handsome_adventurer:jei_transfer_plugin");
	}
}
