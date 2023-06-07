package moonfather.workshop_for_handsome_adventurer.block_entities;

import moonfather.workshop_for_handsome_adventurer.blocks.AdvancedTableBottomPrimary;
import moonfather.workshop_for_handsome_adventurer.integration.CuriosAccessor;
import moonfather.workshop_for_handsome_adventurer.integration.TetraBeltSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryAccessHelper
{
    private void resolveContainer(BlockEntity be, Player player, BlockPos pos, Level level)
    {
        this.chosenContainer = null;   this.addonContainer = null;   this.chestPrimaryPos = null;     this.chosenContainerTrueSize = 0;      this.currentType = "";
        if (be == null) {
            return;
        }
        if (be instanceof ChestBlockEntity) {
            if (ChestBlock.isChestBlockedAt(level, pos)) {
                return;
            }
            this.chosenContainerTrueSize = 27; // upper part only
            this.currentType = RecordTypes.BLOCK;
            //this is how chest combines inv:   Container container = new CompoundContainer(p_51604_, p_51605_);
            this.chestPrimaryPos = pos;
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
                    this.chestPrimaryPos = pos;
                } else {
                    //System.out.println("~~~WTF2");
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
                    this.chestPrimaryPos = pos2;
                } else {
                    //System.out.println("~~~WTF");
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
            this.chosenContainerTrueSize = 27;
            this.currentType = RecordTypes.BLOCK;
            return;
        }
        if (be instanceof EnderChestBlockEntity) {
            if (ChestBlock.isChestBlockedAt(level, pos)) {
                return;
            }
            this.chosenContainer =  player.getEnderChestInventory();
            this.chosenContainerTrueSize = 27;
            this.currentType = RecordTypes.BLOCK;
            return;
        }
        // IItemHandler capability
        LazyOptional<IItemHandler> oih = be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        if (! oih.isPresent()) {
            oih = be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
        }
        oih.ifPresent( ih -> {
            this.chosenContainer = new SimpleTableMenu.VariableSizeItemStackHandlerWrapper(ih);
            this.chosenContainerTrueSize = Math.min(ih.getSlots(), 27);
            this.currentType = RecordTypes.BLOCK;
            return;
        } );
    }

    private static boolean canOpenShulkerBox(Level level, BlockState blockState, BlockPos pos) {
        AABB aabb = Shulker.getProgressDeltaAabb(blockState.getValue(DirectionalBlock.FACING), 0.0F, 0.5F).move(pos).deflate(1.0E-6D);
        return level.noCollision(aabb);
    }



    private List<InventoryAccessHelper.InventoryAccessRecord> adjacentInventories = null;
    public String currentType;

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
        this.loadNonBlockInventories(level, player);
        this.sort(pos, posSecondary);
    }

    private void loadNonBlockInventories(Level level, Player player) {
        if (this.adjacentInventories.size() >= SimpleTableMenu.TAB_SMUGGLING_SOFT_LIMIT) {
            return;
        }
        Object beltSearch = TetraBeltSupport.findToolbelt(player);
        if (TetraBeltSupport.hasToolbelt(beltSearch) && TetraBeltSupport.getToolbeltStorage(beltSearch).getContainerSize() > 0) {
            InventoryAccessRecord record = new InventoryAccessRecord();
            record.ItemChest = TetraBeltSupport.getToolbeltIcon(beltSearch);
            record.Nameable = true;
            record.Name = record.ItemChest.getHoverName();
            record.Type = RecordTypes.TOOLBELT;
            record.HasDoubleInventory = false;
            record.ItemFirst = TetraBeltSupport.getToolbeltStorageFirst(beltSearch);
            record.Index = this.adjacentInventories.size();
            this.adjacentInventories.add(record);
        }
        if (this.adjacentInventories.size() >= SimpleTableMenu.TAB_SMUGGLING_SOFT_LIMIT) {
            return;
        }
        // storage inside items (tinker's plate leggings)
        for (int slot = 0; slot < RecordTypes.NAMED_SLOTS.length; slot++) {
            String slotName = RecordTypes.NAMED_SLOTS[slot];
            ItemStack maybeStorageItem = getItemFromNamedSlot(player, slotName);
            LazyOptional<IItemHandler> pockets = maybeStorageItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            pockets.ifPresent(inventory -> {
                InventoryAccessRecord record = new InventoryAccessRecord();
                record.ItemChest = maybeStorageItem.copy();
                record.Nameable = true;
                record.Name = record.ItemChest.getHoverName();
                record.Type = slotName;
                record.HasDoubleInventory = false;
                record.ItemFirst = ItemStack.EMPTY;
                record.Index = this.adjacentInventories.size();
                this.adjacentInventories.add(record);
            });
            if (this.adjacentInventories.size() >= SimpleTableMenu.TAB_SMUGGLING_SOFT_LIMIT) {
                return;
            }
        }
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
                        if (this.chosenContainer instanceof net.minecraft.world.level.block.entity.BaseContainerBlockEntity bcbe) {
                            record.Name = bcbe.getName();  // Nameable interface doesn't have a set method so i'd have to check for ToolboxBlockEntity and every other separately... doable but meh.
                            record.Nameable = true;
                        }
                        else {
                            record.Name = record.ItemChest.getHoverName();
                            record.Nameable = false;
                        }
                        record.x = pos2.getX(); record.y = pos2.getY(); record.z = pos2.getZ();
                        record.Type = RecordTypes.BLOCK;
                        if (this.addonContainer != null)
                        {
                            record.HasDoubleInventory = true;
                            record.x = this.chestPrimaryPos.getX(); record.y = this.chestPrimaryPos.getY(); record.z = this.chestPrimaryPos.getZ();
                            record.Type = RecordTypes.BLOCK;
                        }
                        record.ItemFirst = this.chosenContainer.getItem(0);
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
    public int chosenContainerTrueSize = 0;
    private BlockPos chestPrimaryPos;

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
        this.chosenContainerTrueSize = 0;   this.addonContainer = this.chosenContainer = null;
        if (this.adjacentInventories == null || this.adjacentInventories.size() <= index)
        {
            return false;
        }
        InventoryAccessHelper.InventoryAccessRecord record = this.adjacentInventories.get(index);
        if (record.Type.equals(RecordTypes.BLOCK)) {
            BlockPos pos = new BlockPos(record.x, record.y, record.z);
            BlockEntity be = level.getBlockEntity(pos);
            resolveContainer(be, player, pos, level);
            return this.chosenContainer != null;
        }
        else if (record.Type.equals(RecordTypes.TOOLBELT)) {
            Container belt = TetraBeltSupport.getToolbeltStorage(player);
            this.chosenContainer = new SimpleTableMenu.VariableSizeContainerWrapper(belt);
            this.addonContainer = null;
            this.chosenContainerTrueSize = belt.getContainerSize();
            this.currentType = RecordTypes.TOOLBELT;
            return true;
        }
        else if (record.Type.equals(RecordTypes.LEGGINGS) || record.Type.equals(RecordTypes.CHESTSLOT) || record.Type.equals(RecordTypes.BACKSLOT)) {
            ItemStack item = getItemFromNamedSlot(player, record.Type);
            LazyOptional<IItemHandler> pockets = item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            pockets.ifPresent(inventory -> {
                this.chosenContainer = new SimpleTableMenu.VariableSizeItemStackHandlerWrapper(inventory);
                this.addonContainer = null;
                this.chosenContainerTrueSize = inventory.getSlots();
                this.currentType = record.Type;
            });
            return this.chosenContainerTrueSize > 0;
        }
        else {
            return false;
        }
    }

    public static ItemStack getItemFromNamedSlot(Player player, String slot) {
        if (slot.equals(RecordTypes.LEGGINGS))
            return player.getInventory().getArmor(1);
        else if (slot.equals(RecordTypes.CHESTSLOT))
            return player.getInventory().getArmor(2);
        else if (slot.equals(RecordTypes.BACKSLOT) && ModList.get().isLoaded("curios"))
            return CuriosAccessor.getFirstItem(player, "back");
        else
            return ItemStack.EMPTY;
    }

    ///////////////////////////////////////////////////////

    private void sort(BlockPos pos, BlockPos posSecondary) {
        InventoryAccessRecord left, right;
        for (int i = 0; i < this.adjacentInventories.size()-1; i++) {
            for (int k = i + 1; k < this.adjacentInventories.size(); k++) {
                left = this.adjacentInventories.get(i);
                right = this.adjacentInventories.get(k);
                if (left.Type.equals(RecordTypes.BLOCK) && right.Type.equals(RecordTypes.BLOCK)
                       && left.x == right.x && left.y == right.y && left.z == right.z)
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
                if (right.Type.equals(RecordTypes.BLOCK) || left.Type.equals(RecordTypes.BLOCK)) {
                    // either is belt?
                    if (right.Type.equals(RecordTypes.BLOCK) && !left.Type.equals(RecordTypes.BLOCK)) {
                        // block entities first. swap.
                        this.adjacentInventories.set(i, right);
                        this.adjacentInventories.set(k, left);
                    }
                    continue; // rest of the method is for blocks
                }
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
        public String Type = "";
    }

    static class RecordTypes
    {
        public static final String BLOCK = "block";
        public static final String TOOLBELT = "belt";
        public static final String LEGGINGS = "leggings_item";
        public static final String CHESTSLOT = "chest_item";
        public static final String BACKSLOT = "back_item";
        public static final String[] NAMED_SLOTS = {LEGGINGS, CHESTSLOT, BACKSLOT};
    }
}
