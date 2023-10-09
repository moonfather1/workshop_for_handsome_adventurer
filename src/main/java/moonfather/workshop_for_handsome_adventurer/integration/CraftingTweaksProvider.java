package moonfather.workshop_for_handsome_adventurer.integration;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.DualTableMenu;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import net.blay09.mods.craftingtweaks.api.ButtonAlignment;
import net.blay09.mods.craftingtweaks.api.CraftingGridBuilder;
import net.blay09.mods.craftingtweaks.api.CraftingGridProvider;
import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class CraftingTweaksProvider implements CraftingGridProvider
{
    public CraftingTweaksProvider()
    {
        CraftingTweaksAPI.registerCraftingGridProvider(this);
    }

    @Override
    public String getModId()
    {
        return Constants.MODID;
    }

    @Override
    public boolean handles(AbstractContainerMenu menu)
    {
        return menu instanceof SimpleTableMenu;
    }

    @Override
    public void buildCraftingGrids(CraftingGridBuilder builder, AbstractContainerMenu menu)
    {
        if (menu instanceof DualTableMenu) {
            builder.addGrid("primary", DualTableMenu.CRAFT_SLOT_START, 3*3)
                   .setButtonAlignment(ButtonAlignment.LEFT);
            builder.addGrid("secondary", DualTableMenu.CRAFT_SECONDARY_SLOT_START, 3*3)
                   .setButtonAlignment(ButtonAlignment.LEFT);
        }
        else if (menu instanceof SimpleTableMenu) {
            builder.addGrid("single", SimpleTableMenu.CRAFT_SLOT_START, 3*3)
                   .setButtonAlignment(ButtonAlignment.LEFT);
        }
    }
}
