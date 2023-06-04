package moonfather.workshop_for_handsome_adventurer.integration;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.items.modular.impl.toolbelt.ToolbeltHelper;
import se.mickelus.tetra.items.modular.impl.toolbelt.inventory.StorageInventory;

public class TetraBeltSupport
{
    public static Object findToolbelt(Player player)
    {
        return ToolbeltHelper.findToolbelt(player);
    }

    public static boolean hasToolbelt(Object searchResult)
    {
        return searchResult != null && ! ((ItemStack) searchResult).isEmpty();
    }

    public static ItemStack getToolbeltIcon(Object searchResult)
    {
        ItemStack result = ((ItemStack) searchResult).copy();
        result.getTag().remove("storageInventory");
        result.getTag().remove("potionsInventory");
        result.getTag().remove("quickInventory");
        result.getTag().remove("quiverInventory");
        return result;
    }

    public static ItemStack getToolbeltStorageFirst(Object searchResult)
    {
        return ItemStack.EMPTY;
    }

    public static Container getToolbeltStorage(Object searchResult)
    {
        return new StorageInventory((ItemStack) searchResult);
    }

    public static Container getToolbeltStorage(Player player)
    {
        return new StorageInventory(ToolbeltHelper.findToolbelt(player));
    }
}
