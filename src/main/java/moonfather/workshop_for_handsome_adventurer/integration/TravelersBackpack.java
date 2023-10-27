package moonfather.workshop_for_handsome_adventurer.integration;

import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.capability.TravelersBackpackCapability;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class TravelersBackpack
{
    private Player player = null;
    private TravelersBackpack(Player player) { this.player = player; }
    private LazyOptional<ITravelersBackpack> capability = null;

    public static TravelersBackpack getInstance(Player player)
    {
        return new TravelersBackpack(player);
    }

    public boolean isPresent()
    {
        if (this.capability == null)
        {
            this.capability = this.player.getCapability(TravelersBackpackCapability.TRAVELERS_BACKPACK_CAPABILITY);
        }
        return this.capability.isPresent();
    }

    public int slotCount()
    {
        if (this.capability == null)
        {
            this.capability = this.player.getCapability(TravelersBackpackCapability.TRAVELERS_BACKPACK_CAPABILITY);
        }
        AtomicInteger count = new AtomicInteger();
        this.capability.ifPresent(c -> { count.set(c.getContainer().getHandler().getSlots()); } );
        return count.get();
    }

    public ItemStack getTabIcon()
    {
        if (this.capability == null)
        {
            this.capability = this.player.getCapability(TravelersBackpackCapability.TRAVELERS_BACKPACK_CAPABILITY);
        }
        AtomicReference<ItemStack> result = new AtomicReference<>(ItemStack.EMPTY);
        this.capability.ifPresent(c -> { result.set(c.getWearable().copy()); } );
        return result.get();
    }

    public ItemStack getFirst()
    {
        if (this.capability == null)
        {
            this.capability = this.player.getCapability(TravelersBackpackCapability.TRAVELERS_BACKPACK_CAPABILITY);
        }
        AtomicReference<ItemStack> result = new AtomicReference<>(ItemStack.EMPTY);
        this.capability.ifPresent(c -> { result.set(c.getContainer().getHandler().getStackInSlot(0)); } );
        return result.get();
    }

    public IItemHandler getItems()
    {
        if (this.capability == null)
        {
            this.capability = this.player.getCapability(TravelersBackpackCapability.TRAVELERS_BACKPACK_CAPABILITY);
        }
        if (this.capability.isPresent() && this.capability.resolve().isPresent())
        {
            return this.capability.resolve().get().getContainer().getHandler();
        }
        return null;
    }
}
