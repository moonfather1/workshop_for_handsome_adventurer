package moonfather.workshop_for_handsome_adventurer.integration;


import com.tiviacz.travelersbackpack.attachment.AttachmentUtils;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class TravelersBackpack
{
    public static boolean isPresent(Player player)
    {
        return AttachmentUtils.isWearingBackpack(player);
    }

    public static int slotCount(Player player)
    {
        BackpackWrapper wrapper = AttachmentUtils.getBackpackWrapper(player);
        if (wrapper == null) { return 0; }
        return wrapper.getStorageForInputOutput().size();
    }

    public static ItemStack getTabIcon(Player player)
    {
        ItemStack result = AttachmentUtils.getWearingBackpack(player);
        if (result.isEmpty())
        {
            return ItemStack.EMPTY;
        }
        result = result.copy();
        result.remove(ModDataComponents.BACKPACK_CONTAINER.get());
        result.remove(ModDataComponents.TOOLS_CONTAINER.get());
        return result;
    }

    public static ItemStack getContainerItem(Player player)
    {
        ItemStack result = AttachmentUtils.getWearingBackpack(player);
        if (result.isEmpty()) { result = null; };
        return result;
    }

    public static ItemStack getFirst(Player player)
    {
        return ItemStack.EMPTY;
    }

    public static ResourceHandler<ItemResource> getItems(Player player)
    {
        BackpackWrapper wrapper = AttachmentUtils.getBackpackWrapper(player);
        if (wrapper == null) { return null; }
        return wrapper.getStorageForInputOutput();
    }

    public static SimpleContainer getContainer(Player player)
    {
        return null;
    }
}
