package moonfather.workshop_for_handsome_adventurer.integration;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public class OhmegaAccessor
{
    public static ItemStack getFirstItem(Player player, String slot)
    {
        if (ModList.get().isLoaded("ohmega"))
        {
            return ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }
}
