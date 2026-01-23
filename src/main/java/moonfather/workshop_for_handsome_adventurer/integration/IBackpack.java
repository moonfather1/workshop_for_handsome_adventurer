package moonfather.workshop_for_handsome_adventurer.integration;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public interface IBackpack
{
    boolean isPresent();
    int slotCount();
    ItemStack getTabIcon();
    ItemStack getContainerItem();
    ItemStack getFirst();
    Container getContainer();
}
