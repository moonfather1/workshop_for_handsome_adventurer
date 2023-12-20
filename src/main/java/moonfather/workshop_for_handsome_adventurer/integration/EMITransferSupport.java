package moonfather.workshop_for_handsome_adventurer.integration;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.widget.Bounds;
import moonfather.workshop_for_handsome_adventurer.block_entities.screens.SimpleTableCraftingScreen;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;

@EmiEntrypoint
public class EMITransferSupport implements EmiPlugin
{
    @Override
    public void register(EmiRegistry emiRegistry)
    {
        emiRegistry.addRecipeHandler(Registration.CRAFTING_DUAL_MENU_TYPE.get(), new EMIRecipeHandlerForDualTable());
        emiRegistry.addRecipeHandler(Registration.CRAFTING_SINGLE_MENU_TYPE.get(), new EMIRecipeHandlerForSmallTable());

//        emiRegistry.addExclusionArea(DualTableCraftingScreen.class, (screen, consumer) -> {
//            int left = screen.getGuiLeft();
//            int top = screen.getGuiTop();
//            int width = screen.getXSize(); //((HandledScreenAccessor) screen).getBackgroundWidth();
//            int height = screen.getYSize();
//            consumer.accept(new Bounds(left, top, width, height));
//        });

        emiRegistry.addGenericExclusionArea((screen, consumer) ->
        {
            if (screen instanceof SimpleTableCraftingScreen workshopTableScreen)
            {
                consumer.accept(new Bounds(workshopTableScreen.getGuiLeft(), workshopTableScreen.getGuiTop(), workshopTableScreen.getXSize(), workshopTableScreen.height));
            }
        });
    }
}
