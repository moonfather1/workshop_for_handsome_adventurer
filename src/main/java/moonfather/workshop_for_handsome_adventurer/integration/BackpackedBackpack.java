package moonfather.workshop_for_handsome_adventurer.integration;

import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BackpackedBackpack implements IBackpack
{
    private BackpackedBackpack(BackpackInventory container)
    {
        this.container = container;
    }

    static BackpackedBackpack instance(Player player, int index)
    {
        if (player instanceof BackpackedInventoryAccess access)
        {
            //iiif (index < access.backpacked$GetBackpackInventoryCount())  // useless; usually count == 5, first one is empty, other 4 are locked.
            BackpackInventory c = access.backpacked$GetBackpackInventory(index);
            if (c != null) // actual backpack
            {
                return new BackpackedBackpack(c);
            }
        }
        return null;
    }

    private final BackpackInventory container;

    //////////////////////////////////////

    @Override
    public boolean isPresent()
    {
        return this.container != null;
    }

    @Override
    public int slotCount()
    {
        return this.container.getContainerSize();
    }

    @Override
    public ItemStack getTabIcon()
    {
        ItemStack result = getContainerItem().copy();
        result.remove(DataComponents.CONTAINER);
        return result;
    }

    @Override
    public ItemStack getContainerItem()
    {
        return this.container.getBackpackStack();
    }

    @Override
    public ItemStack getFirst()
    {
        return this.container.findFirst(is -> ! is.isEmpty());
        // i usually don't want to, but now he added ability to have multiples on back at once, so...
    }

    @Override
    public Container getContainer()
    {
        return this.container;
    }
}
