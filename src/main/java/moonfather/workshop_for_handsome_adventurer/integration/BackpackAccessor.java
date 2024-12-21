package moonfather.workshop_for_handsome_adventurer.integration;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public class BackpackAccessor
{
    public static ItemStack getFirstItemFromBackSlot(Player player)
    {
        ItemStack result = ItemStack.EMPTY;
        if (ModList.get().isLoaded("curios"))
        {
            result = CuriosAccessor.getFirstItem(player, "back");
        }
        if (! result.isEmpty())
        {
            return result;
        }
        if (ModList.get().isLoaded("accessories"))
        {
            result = AccessoriesAccessor.getFirstItem(player, "back");
        }
        if (! result.isEmpty())
        {
            return result;
        }
        if (ModList.get().isLoaded("ohmega"))
        {
            result = OhmegaAccessor.getFirstItem(player, "back");
        }
        if (! result.isEmpty())
        {
            return result;
        }
        return ItemStack.EMPTY;
    }
}
