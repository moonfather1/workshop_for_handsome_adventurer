package moonfather.workshop_for_handsome_adventurer.block_entities;

import moonfather.workshop_for_handsome_adventurer.blocks.AdvancedTableBottomPrimary;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class InventoryAccessHelper
{
    private void resolveContainer(BlockEntity be, Player player, BlockPos pos, Level level)
    {
        this.chosenContainer = null;   this.addonContainer = null;
        if (be == null) {
            return;
        }
        if (be instanceof ChestBlockEntity) {
            if (ChestBlock.isChestBlockedAt(level, pos)) {
                return;
            }
            //this is how chest combines inv:   Container container = new CompoundContainer(p_51604_, p_51605_);
            DoubleBlockCombiner.BlockType type = ChestBlock.getBlockType(be.getBlockState());
            if (type == DoubleBlockCombiner.BlockType.SINGLE) {
                this.chosenContainer = (Container)be;
            }
            if (type == DoubleBlockCombiner.BlockType.FIRST) {
                BlockPos pos2 = pos.relative(ChestBlock.getConnectedDirection(be.getBlockState()));
                BlockEntity be2 = level.getBlockEntity(pos2);
                if (be2 instanceof ChestBlockEntity && be2.getBlockState().getValue(ChestBlock.TYPE) != be.getBlockState().getValue(ChestBlock.TYPE))
                {
                    if (ChestBlock.isChestBlockedAt(level, pos2)) {
                        return;
                    }
                    this.chosenContainer = (Container)be;
                    this.addonContainer = (Container)be2;
                } else {
                    System.out.println("~~~WTF2");
                    return;
                }
            }
            if (type == DoubleBlockCombiner.BlockType.SECOND) {
                BlockPos pos2 = pos.relative(ChestBlock.getConnectedDirection(be.getBlockState()));
                BlockEntity be2 = level.getBlockEntity(pos2);
                if (be2 instanceof ChestBlockEntity && be2.getBlockState().getValue(ChestBlock.TYPE) != be.getBlockState().getValue(ChestBlock.TYPE))
                {
                    if (ChestBlock.isChestBlockedAt(level, pos2)) {
                        return;
                    }
                    this.addonContainer = (Container)be;
                    this.chosenContainer = (Container)be2;
                } else {
                    System.out.println("~~~WTF");
                    return;
                }
            }
            return;
        }
        if (be instanceof Container container && container.getContainerSize() == 27) {
            if (be instanceof ShulkerBoxBlockEntity && ! canOpenShulkerBox(level, be.getBlockState(), pos)) {
                return;
            }
            this.chosenContainer = container;
            return;
        }
        if (be instanceof EnderChestBlockEntity) {
            if (ChestBlock.isChestBlockedAt(level, pos)) {
                return;
            }
            this.chosenContainer =  player.getEnderChestInventory();
            return;
        }
    }

    private static boolean canOpenShulkerBox(Level level, BlockState blockState, BlockPos pos) {
        AABB aabb = Shulker.getProgressDeltaAabb(blockState.getValue(DirectionalBlock.FACING), 0.0F, 0.5F).move(pos).deflate(1.0E-6D);
        return level.noCollision(aabb);
    }



    private List<InventoryAccessHelper.InventoryAccessRecord> adjacentInventories = null;

    public void loadAdjacentInventories(Level level, BlockPos pos, Player player, int inventoryAccessRange)
    {
        if (this.adjacentInventories == null)
        {
            this.adjacentInventories = new ArrayList<>();
        }
        this.adjacentInventories.clear();
        this.loadAdjacentInventoriesCore(level, pos, player, inventoryAccessRange);
        BlockState statePrimary = level.getBlockState(pos);
        BlockPos posSecondary = null;
        if (statePrimary.getBlock() instanceof AdvancedTableBottomPrimary)
        {
            posSecondary = pos.relative(statePrimary.getValue(BlockStateProperties.HORIZONTAL_FACING).getCounterClockWise());
            this.loadAdjacentInventoriesCore(level, posSecondary, player, inventoryAccessRange);
        }
        this.sort(pos, posSecondary);
    }

    private void loadAdjacentInventoriesCore(Level level, BlockPos pos, Player player, int range)
    {
        BlockPos.MutableBlockPos pos2 = new BlockPos.MutableBlockPos();
        for (int dy = 1; dy >= 0; dy--)
        {
            if (pos.getY() + dy > level.getMaxBuildHeight())
            {
                continue;
            }
            for (int dx = -range; dx <= range; dx++)
            {
                for (int dz = -range; dz <= range; dz++)
                {
                    if (! this.isInRange(dx, dz, range))
                    {
                        continue;
                    }
                    if (this.adjacentInventories.size() == SimpleTableMenu.TAB_SMUGGLING_SOFT_LIMIT) { return; }
                    pos2.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    BlockEntity be = level.getBlockEntity(pos2);
                    this.resolveContainer(be, player, pos2, level);
                    if (this.chosenContainer != null)
                    {
                        if (be instanceof net.minecraft.world.level.block.entity.BaseContainerBlockEntity bcbe && ! bcbe.canOpen(player))
                        {
                            continue; //locked
                        }
                        InventoryAccessRecord record = new InventoryAccessRecord();
                        record.ItemChest = be.getBlockState().getBlock().asItem().getDefaultInstance();
                        if (this.chosenContainer instanceof Nameable nameable) {
                            record.Name = nameable.getName();
                            record.Nameable = true;
                        }
                        else {
                            record.Name = record.ItemChest.getHoverName();
                            record.Nameable = false;
                        }
                        if (this.addonContainer != null)
                        {
                            record.HasDoubleInventory = true;
                        }
                        //ih.ifPresent(inventory -> record.ItemFirst = inventory.getStackInSlot(0) );
                        record.ItemFirst = this.chosenContainer.getItem(0);
                        record.x = pos2.getX(); record.y = pos2.getY(); record.z = pos2.getZ();
                        record.Index = this.adjacentInventories.size();
                        this.adjacentInventories.add(record);
                    }
                }
            }
        }
    }


    private boolean isInRange(int dx, int dz, int range)
    {
        if (dx == 0 && dz == 0) return false;
        return Math.abs(dx) + Math.abs(dz) <= range;
    }

    ////////////////////////////////////////////

    public Container chosenContainer, addonContainer;

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
            if (current.HasDoubleInventory) { chest.setCount(2); }
            if (! current.Nameable) { chest.setCount(chest.getCount() | 4); }
            ItemStack suff = current.ItemFirst.copy();
            tabElements.setItem(i*2, chest);
            tabElements.setItem(i*2+1, suff);
        }
        for (int i = max * 2; i < tabElements.getContainerSize(); i++) {
            if (! tabElements.getItem(i).isEmpty()) {
                tabElements.setItem(i, ItemStack.EMPTY);
            }
        }
    }


    public boolean tryInitializeAnotherInventoryAccess(Level level, Player player, int index) {
        if (this.adjacentInventories == null || this.adjacentInventories.size() <= index)
        {
            return false;
        }
        InventoryAccessHelper.InventoryAccessRecord record = this.adjacentInventories.get(index);
        BlockPos pos = new BlockPos(record.x, record.y, record.z);
        BlockEntity be = level.getBlockEntity(pos);
        resolveContainer(be, player, pos, level);
        return this.chosenContainer != null;
    }

    ///////////////////////////////////////////////////////

    private void sort(BlockPos pos, BlockPos posSecondary) {
        InventoryAccessRecord left, right;
        for (int i = 0; i < this.adjacentInventories.size()-1; i++) {
            for (int k = i + 1; k < this.adjacentInventories.size(); k++) {
                left = this.adjacentInventories.get(i);
                right = this.adjacentInventories.get(k);
                if (left.x == right.x && left.y == right.y && left.z == right.z)
                {   // same location? remove.
                    this.adjacentInventories.remove(k);
                    k--;
                }
            }
        }
        for (int i = 0; i < this.adjacentInventories.size()-1; i++) {
            for (int k = i+1; k < this.adjacentInventories.size(); k++) {
                left = this.adjacentInventories.get(i);
                right = this.adjacentInventories.get(k);
                if (left.y < right.y)
                {   // right from upper level, left from floor level? swap.
                    this.adjacentInventories.set(i, right);
                    this.adjacentInventories.set(k, left);
                    continue;
                }
                if (posSecondary != null && left.y == right.y) {
                    if (pos.getX() == posSecondary.getX() && pos.getZ() < posSecondary.getZ()) {
                        if (left.z > right.z) {
                            this.adjacentInventories.set(i, right);
                            this.adjacentInventories.set(k, left);
                        }
                    } else if (pos.getX() == posSecondary.getX() && pos.getZ() > posSecondary.getZ()) {
                        if (left.z < right.z) {
                            this.adjacentInventories.set(i, right);
                            this.adjacentInventories.set(k, left);
                        }
                    } else if (pos.getZ() == posSecondary.getZ() && pos.getX() < posSecondary.getX()) {
                        // player facing north, table facing south. sort.
                        if (left.x > right.x) {
                            this.adjacentInventories.set(i, right);
                            this.adjacentInventories.set(k, left);
                        }
                    } else if (pos.getZ() == posSecondary.getZ() && pos.getX() > posSecondary.getX()) {
                        // player facing south, table facing north. sort.
                        if (left.x < right.x) {
                            this.adjacentInventories.set(i, right);
                            this.adjacentInventories.set(k, left);
                        }
                    }
                }
            }
        }
    }

    ////////////////////////////////////////////

    static class InventoryAccessRecord
    {
        public Component Name = null;
        public ItemStack ItemChest = ItemStack.EMPTY;
        public ItemStack ItemFirst = ItemStack.EMPTY;
        public int Index, x, y, z;
        public boolean HasDoubleInventory = false, Nameable = false;
    }
}
