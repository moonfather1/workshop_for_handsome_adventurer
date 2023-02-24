package moonfather.workshop_for_handsome_adventurer.block_entities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class InventoryAccessHelper
{
    public static void getAdjacentInventories(Level level, BlockPos pos, List<InventoryAccessRecord> listToFill, Player player)
    {
        BlockPos.MutableBlockPos pos2 = new BlockPos.MutableBlockPos();
        for (int dy = 1; dy >= 0; dy--)
        {
            if (pos.getY() + dy > level.getMaxBuildHeight())
            {
                continue;
            }
            for (int dx = -1; dx <= 1; dx++)
            {
                for (int dz = -1; dz <= 1; dz++)
                {
                    if (dx * dz != 0 || dx + dz == 0)
                    {
                        continue; //corners or center, range 1
                    }
                    pos2.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    BlockEntity be = level.getBlockEntity(pos2);
                    Container container = resolveContainer(be, player);
                    if (container != null)
                    {
                        if (be instanceof net.minecraft.world.level.block.entity.BaseContainerBlockEntity bcbe && ! bcbe.canOpen(player))
                        {
                            continue;
                        }
                        InventoryAccessRecord record = new InventoryAccessRecord();
                        record.ItemChest = be.getBlockState().getBlock().asItem().getDefaultInstance();
                        if (be instanceof Nameable nameable) {
                            record.Name = nameable.getName();
                        }else{
                            record.Name = record.ItemChest.getHoverName();
                        }
                        //ih.ifPresent(inventory -> record.ItemFirst = inventory.getStackInSlot(0) );
                        record.ItemFirst = container.getItem(0);
                        record.x = pos2.getX(); record.y = pos2.getY(); record.z = pos2.getZ();
                        record.Index = listToFill.size();
                        listToFill.add(record);
                        if (listToFill.size() == SimpleTableMenu.TAB_SMUGGLING_SOFT_LIMIT) { return; }
                    }
                }
            }
        }
    }

    private static Container resolveContainer(BlockEntity be, Player player)
    {
        if (be == null) {
            return null;
        }
        if (be instanceof Container container && container.getContainerSize() == 27) {
            return container;
        }
        if (be instanceof EnderChestBlockEntity) {
            return player.getEnderChestInventory();
        }
        return null;
    }

    private List<InventoryAccessHelper.InventoryAccessRecord> adjacentInventories = null;

    public void loadAdjacentInventories(Level level, BlockPos pos, Player player)
    {
        if (this.adjacentInventories == null)
        {
            this.adjacentInventories = new ArrayList<>();
        }
        getAdjacentInventories(level, pos, this.adjacentInventories, player);
    }
    ////////////////////////////////////////////

    public Container chosenContainer;

    public boolean tryInitializeFirstInventoryAccess(Level level, Player player)
    {
        return this.tryInitializeAnotherInventoryAccess(level, player, 0);
    }

    public void putInventoriesIntoAContainerForTransferToClient(Container tabElements, int max)
    {
        if (this.adjacentInventories == null || this.adjacentInventories.size() == 0)
        {
            return;
        }
        max = Math.min(this.adjacentInventories.size(), max);
        for (int i = 0; i < max; i++)
        {
            InventoryAccessHelper.InventoryAccessRecord current = this.adjacentInventories.get(i);
            ItemStack chest = current.ItemChest.copy();
            chest.setHoverName(current.Name);
            ItemStack suff = current.ItemFirst.copy();
            tabElements.setItem(i*2, chest);
            tabElements.setItem(i*2+1, suff);
        }
    }

    public boolean tryInitializeAnotherInventoryAccess(Level level, Player player, int index) {
        if (this.adjacentInventories == null || this.adjacentInventories.size() <= index)
        {
            return false;
        }
        InventoryAccessHelper.InventoryAccessRecord record = this.adjacentInventories.get(index);
        BlockEntity be = level.getBlockEntity(new BlockPos(record.x, record.y, record.z));
        if (be == null) { return false; }
        if (! (be instanceof Container c)) { return false; }
        this.chosenContainer = c;
        return true;
    }
    ////////////////////////////////////////////

    static class InventoryAccessRecord
    {
        public Component Name = null;
        public ItemStack ItemChest = ItemStack.EMPTY;
        public ItemStack ItemFirst = ItemStack.EMPTY;
        public int Index, x, y, z;
    }
}
