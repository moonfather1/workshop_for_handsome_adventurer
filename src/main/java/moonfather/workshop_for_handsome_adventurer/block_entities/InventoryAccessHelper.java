package moonfather.workshop_for_handsome_adventurer.block_entities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class InventoryAccessHelper
{
    public static void getAdjacentInventories(Level level, BlockPos pos, List<InventoryAccessRecord> listToFill)
    {
        BlockPos.MutableBlockPos pos2 = new BlockPos.MutableBlockPos();
        for (int dy = 1; dy >= 0; dy--)
        {
            if (pos.getY()
                    + dy > level.getMaxBuildHeight())
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
                    if (level.getBlockState(pos2).is(Tags.Blocks.CHESTS))
                    {
                        BlockEntity be = level.getBlockEntity(pos2);
                        InventoryAccessRecord record = new InventoryAccessRecord();
                        record.ItemChest = be.getBlockState().getBlock().asItem().getDefaultInstance();
                        if (be instanceof Nameable nameable) {
                            record.Name = nameable.getName();
                        }else{
                            record.Name = record.ItemChest.getHoverName();
                        }
                        LazyOptional<IItemHandler> ih = be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
                        ih.ifPresent(inventory -> record.ItemFirst = inventory.getStackInSlot(0)); //getSlots==27??
                        record.x = pos2.getX(); record.y = pos2.getY(); record.z = pos2.getZ();
                        record.Index = listToFill.size();
                        listToFill.add(record);
                    }
                }
            }
        }

    }
    private List<InventoryAccessHelper.InventoryAccessRecord> adjacentInventories = null;

    public void loadAdjacentInventories(Level level, BlockPos pos)
    {
        if (this.adjacentInventories == null)
        {
            this.adjacentInventories = new ArrayList<>();
        }
        getAdjacentInventories(level, pos, this.adjacentInventories);
    }
    ////////////////////////////////////////////

    private LazyOptional<IItemHandler> itemHandler;
    public Container cont2;

    public void initializeFirstInventoryAccess(Level level, Player player)
    {
        //container.clearContent();
        if (this.adjacentInventories == null || this.adjacentInventories.size() == 0)
        {
            return;
        }
        InventoryAccessHelper.InventoryAccessRecord record = this.adjacentInventories.get(0);
        BlockEntity be = level.getBlockEntity(new BlockPos(record.x, record.y, record.z));
        if (be == null) { return; }
        //this.itemHandler = be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        //this.itemHandler.ifPresent(
        //        inventory ->
        //        {
        //            for (int i = 0; i < inventory.getSlots(); i++) {
        //                container.setItem(i, inventory.getStackInSlot(i));
        //            }
        //        }
        //);
        //this.cont2 = menu2.getSlot(0).container;
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
            //chest.getOrCreateTag().putInt("w_index", current.Index);
            tabElements.setItem(i*2, chest);
            tabElements.setItem(i*2+1, suff);
        }
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
