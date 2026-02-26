package moonfather.workshop_for_handsome_adventurer.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.Identifier;

@JeiPlugin
public class JEITransferSupport implements IModPlugin
{
	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
	{
		registration.addRecipeTransferHandler(new JEITransferInfoForDualTable());
		registration.addRecipeTransferHandler(new JEITransferInfoForSmallTable());
	}

	@Override
	public Identifier getPluginUid()
	{
		return Identifier.parse("workshop_for_handsome_adventurer:jei_transfer_plugin");
	}
}
