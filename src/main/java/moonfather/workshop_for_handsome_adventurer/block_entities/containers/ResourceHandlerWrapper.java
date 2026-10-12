package moonfather.workshop_for_handsome_adventurer.block_entities.containers;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

///
/// turns 2025 capability into a container. from winged horse to donkey.
///
public class ResourceHandlerWrapper extends SimpleContainer
{
    public ResourceHandlerWrapper(ResourceHandler<ItemResource> wrapped, int totalSize, boolean allowPlaceContainers)
    {
        super(totalSize);
        this.allowPlaceContainers = allowPlaceContainers;
        this.internal = wrapped;
    }

    public ResourceHandlerWrapper(ResourceHandler<ItemResource> wrapped)
    {
        super(wrapped.size());
        this.allowPlaceContainers = false;
        this.internal = wrapped;
    }
    private final boolean allowPlaceContainers;
    protected final ResourceHandler<ItemResource> internal;

    ///////////////////////////////////////////////////

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack)
    {
        if (! this.allowPlaceContainers && ! stack.getItem().canFitInsideContainerItems()) { return false; }
        return internal.isValid(slot, ItemResource.of(stack));
    }



    @Override
    public ItemStack getItem(int index)
    {
        return internal.getResource(index).toStack(internal.getAmountAsInt(index));
    }



    @Override
    public ItemStack removeItem(int index, int count)
    {
        if (internal.getAmountAsInt(index) == 0) { return ItemStack.EMPTY; }
        ItemResource resource = internal.getResource(index);
        int amount = 0;
        try (Transaction tx = Transaction.openRoot())
        {
            amount = internal.extract(index, resource, count, tx);
            tx.commit(); // what to check...
        }
        return resource.toStack(amount);
    }



    @Override
    public ItemStack removeItemNoUpdate(int index)
    {
        ItemResource resource = internal.getResource(index);
        return this.removeItem(index, internal.getCapacityAsInt(index, resource));
    }



    @Override
    public void setItem(int index, ItemStack stack, boolean insideTransaction)
    {
        ItemResource oldRes = internal.getResource(index);
        int oldCount = internal.getAmountAsInt(index);
        if (oldCount == 0)
        {
            try (Transaction tx = Transaction.openRoot())
            {
                if (stack.isEmpty())
                {
                    return;
                }
                int accepted = internal.insert(index, ItemResource.of(stack), stack.getCount(), tx);
                stack.shrink(accepted);
                if (! insideTransaction)
                {
                    tx.commit();
                }
            }
            return;
        }
        if (oldRes.matches(stack))
        {
            if (stack.isEmpty() || stack.getCount() == oldCount)
            {
                return;
            }
            if (stack.getCount() > oldCount)
            {
                try (Transaction tx = Transaction.openRoot())
                {
                    int accepted = internal.insert(index, ItemResource.of(stack), stack.getCount() - oldCount, tx);
                    stack.shrink(accepted);
                    if (! insideTransaction)
                    {
                        tx.commit();
                    }
                }
            }
            else
            {
                try (Transaction tx = Transaction.openRoot())
                {
                    int extracted = this.internal.extract(index, oldRes, oldCount - stack.getCount(), tx);
                    stack.shrink(stack.getCount());
                    if (! insideTransaction)
                    {
                        tx.commit();
                    }
                }
            }
            return;
        }
        // replacing.
        try (Transaction tx = Transaction.openRoot())
        {
            int extracted = this.internal.extract(index, oldRes, oldCount, tx);
            int inserted = 0;
            if (! stack.isEmpty())
            {
                inserted = this.internal.insert(index, ItemResource.of(stack), stack.getCount(), tx);
            }
            if (inserted == stack.getCount())
            {
                stack = oldRes.toStack(extracted);
                if (! insideTransaction)
                {
                    tx.commit();
                }
            }
        }
    }



    @Override
    public int getContainerSize()
    {
        return internal.size();
    }

    @Override
    public boolean isEmpty()
    {
        for (int i = 0; i < internal.size(); i++)
        {
            if (internal.getAmountAsInt(i) > 0) { return false; }
        }
        return true;
    }

    @Override
    public int getMaxStackSize()
    {
        if (internal.size() == 0) { return 0; } //???
        return internal.getCapacityAsInt(0, ItemResource.of(Items.CARROT));
    }

    @Override
    public int getMaxStackSize(ItemStack stack)
    {
        return internal.getCapacityAsInt(0, ItemResource.of(stack));
    }
}