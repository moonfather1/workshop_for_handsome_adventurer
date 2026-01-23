package moonfather.workshop_for_handsome_adventurer.integration;

import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import net.minecraft.world.entity.player.Player;

public class BackpackedManager
{
    public static int getBackpackCount(Player player)
    {
        return player instanceof BackpackedInventoryAccess access ? access.backpacked$GetBackpackInventoryCount() : 0;
    }

    public static IBackpack get(Player player, int i)
    {
        return BackpackedBackpack.instance(player, i);
    }
}
