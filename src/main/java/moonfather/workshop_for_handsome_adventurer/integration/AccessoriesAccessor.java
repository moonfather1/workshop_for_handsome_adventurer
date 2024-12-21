package moonfather.workshop_for_handsome_adventurer.integration;

import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import io.wispforest.accessories.api.slot.SlotType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.util.Collection;
import java.util.List;

public class AccessoriesAccessor
{
    public static ItemStack getFirstItem(Player player, String slot)
    {
        if (ModList.get().isLoaded("accessories"))
        {
            AccessoriesCapability capability = AccessoriesCapability.get(player);
            if (capability == null)
            {
                return ItemStack.EMPTY;
            }
            List<SlotEntryReference> slots = capability.getAllEquipped(false);
            for (SlotEntryReference entry: slots)
            {
                if (entry.reference().isValid() && slot.equals(entry.reference().slotName()))
                {
                    return entry.stack();
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
