package moonfather.workshop_for_handsome_adventurer.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEITransferSupport implements IModPlugin
{
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
	{
		registration.addRecipeTransferHandler(new JEITransferInfoForDualTable());
		registration.addRecipeTransferHandler(new JEITransferInfoForSmallTable());
	}

	@Override
	public ResourceLocation getPluginUid()
	{
		return new ResourceLocation("workshop_for_handsome_adventurer:jei_transfer_plugin");
	}
}
