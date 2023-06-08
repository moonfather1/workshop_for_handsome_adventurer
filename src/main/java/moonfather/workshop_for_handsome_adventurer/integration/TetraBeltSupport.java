package moonfather.workshop_for_handsome_adventurer.integration;

import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import se.mickelus.tetra.items.modular.impl.toolbelt.ToolbeltHelper;
import se.mickelus.tetra.items.modular.impl.toolbelt.inventory.StorageInventory;

public class TetraBeltSupport
{
    public static Object findToolbelt(Player player)
    {
        if (! ModList.get().isLoaded("tetra")) { return null; }
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
        ItemStack belt = ToolbeltHelper.findToolbelt(player);
        if (! belt.isEmpty()) {
            return new StorageInventory(belt);
        }
        return null;
    }
}
