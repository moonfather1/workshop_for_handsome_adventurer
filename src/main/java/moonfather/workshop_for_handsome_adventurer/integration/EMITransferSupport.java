package moonfather.workshop_for_handsome_adventurer.integration;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.handler.CraftingRecipeHandler;
import dev.emi.emi.mixin.accessor.HandledScreenAccessor;
import moonfather.workshop_for_handsome_adventurer.block_entities.screens.DualTableCraftingScreen;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;

@EmiEntrypoint
public class EMITransferSupport implements EmiPlugin
{
    @Override
    public void register(EmiRegistry emiRegistry)
    {
        emiRegistry.addRecipeHandler(Registration.CRAFTING_DUAL_MENU_TYPE.get(), new EMIRecipeHandlerForDualTable());
        emiRegistry.addRecipeHandler(Registration.CRAFTING_SINGLE_MENU_TYPE.get(), new EMIRecipeHandlerForSmallTable());

        emiRegistry.addExclusionArea(DualTableCraftingScreen.class, (screen, consumer) -> {
            int left = screen.getGuiLeft();
            int top = ((HandledScreenAccessor) screen).getY();
            int width = screen.getXSize(); //((HandledScreenAccessor) screen).getBackgroundWidth();
            int height = ((HandledScreenAccessor) screen).getBackgroundHeight();
            consumer.accept(new Bounds(left, top, width, height));
        });
    }
}
