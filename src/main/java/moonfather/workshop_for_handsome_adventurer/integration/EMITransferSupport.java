package moonfather.workshop_for_handsome_adventurer.integration;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;

@EmiEntrypoint
public class EMITransferSupport implements EmiPlugin
{
    @Override
    public void register(EmiRegistry emiRegistry)
    {
        emiRegistry.addRecipeHandler(Registration.CRAFTING_DUAL_MENU_TYPE.get(), new EMIRecipeHandlerForDualTable());
        emiRegistry.addRecipeHandler(Registration.CRAFTING_SINGLE_MENU_TYPE.get(), new EMIRecipeHandlerForSmallTable());
    }
}
