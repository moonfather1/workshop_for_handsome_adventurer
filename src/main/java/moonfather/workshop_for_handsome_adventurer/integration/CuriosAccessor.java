package moonfather.workshop_for_handsome_adventurer.integration;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;

public class CuriosAccessor {
    public static ItemStack getFirstItem(Player player, String slot) {
        if (ModList.get().isLoaded("curios")) {
            List<SlotResult> results = CuriosApi.getCuriosHelper().findCurios(player, slot);
            if (results != null && results.size() > 0) {
                return results.get(0).stack();
            }
        }
        return ItemStack.EMPTY;
    }
}
