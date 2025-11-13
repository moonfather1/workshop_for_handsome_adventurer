package moonfather.workshop_for_handsome_adventurer.integration;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.List;
import java.util.Optional;

public class CuriosAccessor
{
    public static ItemStack getFirstItem(Player player, String slot)
    {
        if (ModList.get().isLoaded("curios"))
        {
            Optional<ICuriosItemHandler> inv = CuriosApi.getCuriosInventory(player);
            if (! inv.isPresent()) { return ItemStack.EMPTY; }
            List<SlotResult> results = inv.get().findCurios(slot);
            if (results != null && results.size() > 0)
            {
                return results.get(0).stack();
            }
        }
        return ItemStack.EMPTY;
    }
}
