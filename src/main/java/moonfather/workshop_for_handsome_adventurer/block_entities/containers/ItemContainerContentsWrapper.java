package moonfather.workshop_for_handsome_adventurer.block_entities.containers;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class ItemContainerContentsWrapper implements Container
{
    public ItemContainerContentsWrapper(ItemContainerContents container, ItemStack backpack, int maxSize)
    {
        this.containerStack = backpack;
        this.wrapped = container;
        container.copyInto(this.list);
        this.maxSize = maxSize;
    }
    private final ItemStack containerStack;
    private final ItemContainerContents wrapped;
    private final NonNullList<ItemStack> list = NonNullList.withSize(54, ItemStack.EMPTY);
    private final int maxSize;

    @Override
    public int getContainerSize()
    {
        return maxSize;
    }

    @Override
    public boolean isEmpty()
    {
        return this.wrapped.getSlots() < 1;
    }

    @Override
    public ItemStack getItem(int slot)
    {
        return slot < this.maxSize ? this.list.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int count)
    {
        ItemStack result = ItemStack.EMPTY;
        if (slot < this.maxSize)
        {
            result = this.getItem(slot).copy();
            if (count < result.getCount())
            {
                result.shrink(result.getCount() - count);
            }
            this.list.get(slot).shrink(result.getCount());
            this.containerStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.list));
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot)
    {
        ItemStack result = this.getItem(slot);
        if (slot < this.maxSize)
        {
            this.list.set(slot, ItemStack.EMPTY);
        }
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack itemStack)
    {
        if (slot < this.maxSize)
        {
            this.list.set(slot, ItemStack.EMPTY);
        }
        this.containerStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.list));
    }

    @Override
    public int getMaxStackSize()
    {
        return Container.super.getMaxStackSize();
    }

    @Override
    public int getMaxStackSize(ItemStack stack)
    {
        return Container.super.getMaxStackSize(stack);
    }

    @Override
    public void setChanged()
    {
        this.containerStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.list));
    }

    @Override
    public boolean stillValid(Player player)
    {
        return true;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack)
    {
        return slot >= 0 && slot < this.maxSize;
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack)
    {
        return slot >= 0 && slot < this.maxSize && target.canTakeItem(target, slot, stack);
    }

    @Override
    public void clearContent()
    {
        for (int i = 0; i < this.maxSize; i++)
        {
            this.list.set(i, ItemStack.EMPTY);
        }
        this.containerStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.list));
    }
}
