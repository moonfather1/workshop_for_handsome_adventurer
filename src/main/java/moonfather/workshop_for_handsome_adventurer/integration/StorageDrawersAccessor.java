package moonfather.workshop_for_handsome_adventurer.integration;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemHandler;
import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.capabilities.Capabilities;
import moonfather.workshop_for_handsome_adventurer.block_entities.containers.container_translators.IExcessSlotManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class StorageDrawersAccessor
{
    public static SimpleContainer getCapabilityFromWorld(Level level, BlockPos pos)
    {
        IItemRepository capa1 = Capabilities.ITEM_REPOSITORY.getCapability(level, pos);
        if (capa1 != null)
        {
            IItemHandler capa2 = Capabilities.ITEM_HANDLER.getCapability(level, pos);
            return new Wrapper(capa1, capa2);
        }
        return null;
    }

    private static class Wrapper extends SimpleContainer implements IExcessSlotManager
    {
        private final IItemRepository wrapped_repoditory;
        private final IItemHandler wrapped_handler;

        public Wrapper(IItemRepository itemRepository, IItemHandler itemHandler)
        {
            super(itemHandler.getSlots() - 1);
            this.wrapped_repoditory = itemRepository;
            this.wrapped_handler = itemHandler;
        }
        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack)
        {
            ItemStack old = this.getItem(slot);
            if (old.isEmpty() || itemStack.isEmpty())
            {
                return true;
            }
            if (! ItemStack.isSameItemSameComponents(itemStack, old))
            {
                return false;  // prevents replacing through our table.
            }
            return this.wrapped_repoditory.getRemainingItemCapacity(itemStack) > 0;
        }
        @Override
        public ItemStack getItem(int slot)
        {
            return this.wrapped_handler.getStackInSlot(slot + 1);
        }

        @Override
        public ItemStack removeItem(int slot, int count) { return this.wrapped_handler.extractItem(slot + 1, count, false); }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {	return this.wrapped_handler.extractItem(slot + 1, 1000, false); }

        @Override
        public void setItem(int slot, ItemStack itemStack)
        {
            ItemStack old = this.wrapped_handler.getStackInSlot(slot + 1);
            ItemStack resto;
            if (old.isEmpty())
            {
                resto = this.wrapped_handler.insertItem(slot + 1, itemStack, false);
            }
            else if (itemStack.isEmpty())
            {
                resto = this.wrapped_handler.extractItem(slot + 1, old.getCount(), false);
            }
            else if (ItemStack.isSameItemSameComponents(itemStack, old))
            {
                if (itemStack.getCount() > old.getCount())
                {
                    itemStack.shrink(old.getCount());
                    resto = this.wrapped_handler.insertItem(slot + 1, itemStack, false);
                }
                else
                {
                    resto = this.wrapped_handler.extractItem(slot + 1, old.getCount() - itemStack.getCount(), false);
                }
            }
            else
            {
                // replacing.
                resto = this.wrapped_handler.extractItem(slot + 1, old.getCount(), false);
                resto = this.wrapped_handler.insertItem(slot + 1, itemStack, false);
            }
        }

        @Override
        public boolean isEmpty() { return false; }

        @Override
        public void setChanged() {  }

        @Override
        public int getMaxStackSize() { return this.wrapped_handler.getSlotLimit(1); }

        @Override
        public int getMaxStackSize(ItemStack stack) { return this.wrapped_repoditory.getItemCapacity(stack); }

        @Override
        public boolean isSlotSpecificallyDisabled(int slotIndex) { return slotIndex >= this.wrapped_handler.getSlots() - 1; }
    }
    //////
    public static class VariableSizeContainerWrapper extends SimpleContainer implements IExcessSlotManager
    {
        private IExcessSlotManager excessManager = null;
        private final Container internal;
        private final boolean allowPlaceContainers;

        public VariableSizeContainerWrapper(Container wrapped, boolean allowPlaceContainers)
        {
            super(54);
            this.internal = wrapped;
            this.allowPlaceContainers = allowPlaceContainers;
            if (this.internal instanceof IExcessSlotManager esm)
            {
                this.excessManager = esm;
            }
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack)
        {
            if (! this.allowPlaceContainers && ! itemStack.getItem().canFitInsideContainerItems()) { return false; }
            return slot < internal.getContainerSize() && internal.canPlaceItem(slot, itemStack);
        }

        @Override
        public ItemStack getItem(int slot) { return slot < internal.getContainerSize() ? internal.getItem(slot) : ItemStack.EMPTY; }

        @Override
        public ItemStack removeItem(int slot, int count) { return slot < internal.getContainerSize() ? internal.removeItem(slot, count) : ItemStack.EMPTY; }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {	return slot < internal.getContainerSize() ? internal.removeItemNoUpdate(slot) : ItemStack.EMPTY; }

        @Override
        public void setItem(int slot, ItemStack itemStack) { if (slot < internal.getContainerSize()) { internal.setItem(slot, itemStack); } }

        @Override
        public boolean isEmpty() { return internal.isEmpty(); }

        @Override
        public void setChanged() { internal.setChanged(); }

        @Override
        public int getMaxStackSize() { return internal.getMaxStackSize(); }

        @Override
        public int getMaxStackSize(ItemStack stack) { return internal.getMaxStackSize(stack); }

        @Override
        public boolean isSlotSpecificallyDisabled(int slotIndex)
        {
            return this.excessManager != null && this.excessManager.isSlotSpecificallyDisabled(slotIndex);
        }
    }
}
