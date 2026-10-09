package moonfather.workshop_for_handsome_adventurer.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiFeatures;
import moonfather.workshop_for_handsome_adventurer.block_entities.screens.DualTableCraftingScreen;
import moonfather.workshop_for_handsome_adventurer.block_entities.screens.SimpleTableCraftingScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    public void registerGuiHandlers(IGuiHandlerRegistration registration)
    {
        registration.addGuiContainerHandler(DualTableCraftingScreen.class, new TableHandler2());
        registration.addGuiContainerHandler(SimpleTableCraftingScreen.class, new TableHandler1());
    }

    @Override
	public Identifier getPluginUid()
	{
		return Identifier.parse("workshop_for_handsome_adventurer:jei_transfer_plugin");
	}

    ///   ////////////////////////////////////////////////////////
    ///
    private static class TableHandler1 implements IGuiContainerHandler<SimpleTableCraftingScreen> {
        @NotNull
        @Override
        public List<Rect2i> getGuiExtraAreas(SimpleTableCraftingScreen containerScreen)
        {
            return List.of(containerScreen.getExclusionZone());
        }
    }
    private static class TableHandler2 implements IGuiContainerHandler<DualTableCraftingScreen> {
        @NotNull
        @Override
        public List<Rect2i> getGuiExtraAreas(DualTableCraftingScreen containerScreen)
        {
            return List.of(containerScreen.getExclusionZone());
        }
    }
}
