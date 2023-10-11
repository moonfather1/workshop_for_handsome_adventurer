package moonfather.workshop_for_handsome_adventurer.block_entities;

import moonfather.workshop_for_handsome_adventurer.block_entities.container_translators.TetraBeltTranslator;
import moonfather.workshop_for_handsome_adventurer.blocks.AdvancedTableBottomPrimary;
import moonfather.workshop_for_handsome_adventurer.integration.CuriosAccessor;
import moonfather.workshop_for_handsome_adventurer.integration.TetraBeltSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.CompoundContainer;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandler;

import java.util.LinkedList;

public class InventoryAccessHelper
{
    private void resolveBlockContainer(BlockEntity be, Player player, BlockPos pos, Level level)
    {
        this.chosenContainer = null; this.chestPrimaryPos = null; this.chosenContainerTrueSize = 0;
        this.currentType = ""; this.chosenContainerVisibleSize = 27; this.chosenContainerForRename = null;
        if (be == null)
        {
            return;
        }
        if (be instanceof ChestBlockEntity cbe && cbe.getContainerSize() <= 54)
        {
            if (ChestBlock.isChestBlockedAt(level, pos))
            {
                return;
            }
            this.chosenContainerTrueSize = cbe.getContainerSize(); // we'll deal with double chests below
            this.chosenContainerVisibleSize = cbe.getContainerSize() <= 27 ? 27 : 54;
            this.currentType = RecordTypes.BLOCK;
            DoubleBlockCombiner.BlockType type = ChestBlock.getBlockType(be.getBlockState());
            if (type == DoubleBlockCombiner.BlockType.SINGLE)
            {
                this.chosenContainer = new SimpleTableMenu.VariableSizeContainerWrapper((Container) be);
                this.chosenContainerForRename = be;
            }
            if (type == DoubleBlockCombiner.BlockType.FIRST)
            {
                BlockPos pos2 = pos.relative(ChestBlock.getConnectedDirection(be.getBlockState()));
                BlockEntity be2 = level.getBlockEntity(pos2);
                if (be2 instanceof ChestBlockEntity && be2.getBlockState().getValue(ChestBlock.TYPE) != be.getBlockState().getValue(ChestBlock.TYPE))
                {
                    if (ChestBlock.isChestBlockedAt(level, pos2))
                    {
                        return;
                    }
                    Container result = new CompoundContainer((Container) be, (Container) be2);
                    this.chosenContainer = result.getContainerSize() == 54 ? result : new SimpleTableMenu.VariableSizeContainerWrapper(result);
                    this.chosenContainerTrueSize = result.getContainerSize();
                    this.chosenContainerVisibleSize = result.getContainerSize() <= 27 ? 27 : 54;
                    this.chosenContainerForRename = be;
                }
                else
                {
                    return;
                }
            }
            if (type == DoubleBlockCombiner.BlockType.SECOND)
            {
                BlockPos pos2 = pos.relative(ChestBlock.getConnectedDirection(be.getBlockState()));
                BlockEntity be2 = level.getBlockEntity(pos2);
                if (be2 instanceof ChestBlockEntity && be2.getBlockState().getValue(ChestBlock.TYPE) != be.getBlockState().getValue(ChestBlock.TYPE))
                {
                    if (ChestBlock.isChestBlockedAt(level, pos2))
                    {
                        return;
                    }
                    Container result = new CompoundContainer((Container) be2, (Container) be);
                    this.chosenContainer = result.getContainerSize() == 54 ? result : new SimpleTableMenu.VariableSizeContainerWrapper(result);
                    this.chosenContainerTrueSize = result.getContainerSize();
                    this.chosenContainerVisibleSize = result.getContainerSize() <= 27 ? 27 : 54;
                    this.chosenContainerForRename = be2;
                    this.chestPrimaryPos = pos2;
                }
                else
                {
                    return;
                }
            }
            return;
        }
        if (be instanceof Container container && container.getContainerSize() <= 54)
        {
            if (be instanceof ShulkerBoxBlockEntity && !canOpenShulkerBox(level, be.getBlockState(), pos))
            {
                return;
            }
            this.chosenContainer = new SimpleTableMenu.VariableSizeContainerWrapper(container);
            this.chosenContainerTrueSize = container.getContainerSize();
            this.chosenContainerVisibleSize = container.getContainerSize() <= 27 ? 27 : 54;
            this.chosenContainerForRename = be;
            this.currentType = RecordTypes.BLOCK;
            return;
        }
        if (be instanceof EnderChestBlockEntity)
        {
            if (ChestBlock.isChestBlockedAt(level, pos))
            {
                return;
            }
            this.chosenContainer = new SimpleTableMenu.VariableSizeContainerWrapper(player.getEnderChestInventory());
            this.chosenContainerTrueSize = 27;
            this.chosenContainerVisibleSize = 27;
            this.currentType = RecordTypes.BLOCK;
            return;
        }
        if (be.getBlockState().getBlock().getDescriptionId().contains("storagedrawers"))
        {
            // support for storage drawers is killed as there is a nasty duplication issue and i have no strength now.
            return;
            //LazyOptional<IItemHandler> oih = be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            //oih.ifPresent( ih -> {
            //    this.chosenContainer = new SimpleTableMenu.VariableSizeContainerWrapper(new StorageDrawersSimpleTranslator(ih));
            //    this.chosenContainerTrueSize = ih.getSlots() - 1;
            //    this.currentType = RecordTypes.BLOCK;
            //} );
            //if (this.currentType.equals(RecordTypes.BLOCK))
            //{
            //    return;
            //}
        }
        // IItemHandler capability
        LazyOptional<IItemHandler> oih = be.getCapability(ForgeCapabilities.ITEM_HANDLER);
        if (!oih.isPresent())
        {
            oih = be.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP);
        }
        oih.ifPresent(ih -> {
            if (ih.getSlots() <= 54)
            {
                this.chosenContainer = new SimpleTableMenu.VariableSizeItemStackHandlerWrapper(ih);
                this.chosenContainerTrueSize = ih.getSlots();
                this.chosenContainerVisibleSize = ih.getSlots() <= 27 ? 27 : 54;
                this.chosenContainerForRename = be;
                this.currentType = RecordTypes.BLOCK;
                return;
            }
        });
    }

    private static boolean canOpenShulkerBox(Level level, BlockState blockState, BlockPos pos)
    {
        AABB aabb = Shulker.getProgressDeltaAabb(blockState.getValue(DirectionalBlock.FACING), 0.0F, 0.5F).move(pos).deflate(1.0E-6D);
        return level.noCollision(aabb);
    }



    private LinkedList<InventoryAccessHelper.InventoryAccessRecord> adjacentInventories = null;
    public String currentType;

    public void loadAdjacentInventories(Level level, BlockPos pos, Player player, int inventoryAccessRange)
    {
        if (this.adjacentInventories == null)
        {
            this.adjacentInventories = new LinkedList<>();
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
        while (this.adjacentInventories.size() > SimpleTableMenu.TAB_SMUGGLING_SOFT_LIMIT)
        {
            this.adjacentInventories.removeLast();
        }
    }

    private void loadNonBlockInventories(Level level, Player player)
    {
        Object beltSearch = TetraBeltSupport.findToolbelt(player);
        if (TetraBeltSupport.hasToolbelt(beltSearch))
        {
            int size = TetraBeltSupport.getToolbeltStorage(beltSearch).getContainerSize();
            if (size > 0)
            {
                InventoryAccessRecord record = new InventoryAccessRecord();
                record.ItemChest = TetraBeltSupport.getToolbeltIcon(beltSearch);
                record.Nameable = true;
                record.Name = record.ItemChest.getHoverName();
                record.Type = RecordTypes.TOOLBELT;
                record.VisibleSlotCount = 54; // it's fine we'll adjust it later
                record.ItemFirst = TetraBeltSupport.getToolbeltStorageFirst(beltSearch);
                record.Index = this.adjacentInventories.size();
                this.adjacentInventories.add(record);
            }
        }
        // storage inside items (tinker's plate leggings)
        for (int slot = 0; slot < RecordTypes.NAMED_SLOTS.length; slot++)
        {
            String slotName = RecordTypes.NAMED_SLOTS[slot];
            ItemStack maybeStorageItem = getItemFromNamedSlot(player, slotName);
            LazyOptional<IItemHandler> pockets = maybeStorageItem.getCapability(ForgeCapabilities.ITEM_HANDLER);
            pockets.ifPresent(inventory -> {
                InventoryAccessRecord record = new InventoryAccessRecord();
                record.ItemChest = maybeStorageItem.copy();
                record.Nameable = true;
                record.Name = record.ItemChest.getHoverName();
                record.Type = slotName;
                record.VisibleSlotCount = inventory.getSlots() <= 27 ? 27 : 54;
                record.ItemFirst = ItemStack.EMPTY;
                record.Index = this.adjacentInventories.size();
                this.adjacentInventories.add(record);
            });
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
                    if (!this.isInRange(dx, dz, range))
                    {
                        continue;
                    }
                    pos2.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    BlockEntity be = level.getBlockEntity(pos2);
                    this.resolveBlockContainer(be, player, pos2, level);
                    if (this.chosenContainer != null)
                    {
                        if (be instanceof net.minecraft.world.level.block.entity.BaseContainerBlockEntity bcbe && !bcbe.canOpen(player))
                        {
                            continue; //locked
                        }
                        InventoryAccessRecord record = new InventoryAccessRecord();
                        record.ItemChest = be.getBlockState().getBlock().asItem().getDefaultInstance();
                        if (this.chosenContainerForRename instanceof net.minecraft.world.level.block.entity.BaseContainerBlockEntity bcbe)
                        {
                            record.Name = bcbe.getName();  // Nameable interface doesn't have a set method so i'd have to check for ToolboxBlockEntity and every other separately... doable but meh.
                            record.Nameable = true;
                        }
                        else
                        {
                            record.Name = record.ItemChest.getHoverName();
                            record.Nameable = false;
                        }
                        record.x = pos2.getX(); record.y = pos2.getY(); record.z = pos2.getZ();
                        record.Type = RecordTypes.BLOCK;
                        record.VisibleSlotCount = this.chosenContainerVisibleSize;
                        if (this.chestPrimaryPos != null)
                        {
                            record.x = this.chestPrimaryPos.getX(); record.y = this.chestPrimaryPos.getY();
                            record.z = this.chestPrimaryPos.getZ();
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
        if (dx == 0 && dz == 0)
        {
            return false;
        }
        return Math.abs(dx) + Math.abs(dz) <= range;
    }

    ////////////////////////////////////////////

    public Container chosenContainer;
    public int chosenContainerTrueSize = 0, chosenContainerVisibleSize = 0;
    private BlockPos chestPrimaryPos;
    public BlockEntity chosenContainerForRename = null;


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
            if (current.VisibleSlotCount > 27)
            {
                chest.setCount(2);
            }
            if (!current.Nameable)
            {
                chest.setCount(chest.getCount() | 4);
            }
            ItemStack suff = current.ItemFirst.copy();
            tabElements.setItem(i * 2, chest);
            tabElements.setItem(i * 2 + 1, suff);
        }
        for (int i = max * 2; i < tabElements.getContainerSize(); i++)
        {
            if (!tabElements.getItem(i).isEmpty())
            {
                tabElements.setItem(i, ItemStack.EMPTY);
            }
        }
    }


    public boolean tryInitializeInventoryAccess(Level level, Player player, int index)
    {
        this.chosenContainerTrueSize = 0; this.chosenContainer = null;
        if (this.adjacentInventories == null || this.adjacentInventories.size() <= index)
        {
            return false;
        }
        InventoryAccessHelper.InventoryAccessRecord record = this.adjacentInventories.get(index);
        if (record.Type.equals(RecordTypes.BLOCK))
        {
            BlockPos pos = new BlockPos(record.x, record.y, record.z);
            BlockEntity be = level.getBlockEntity(pos);
            resolveBlockContainer(be, player, pos, level);
            return this.chosenContainer != null;
        }
        else if (record.Type.equals(RecordTypes.TOOLBELT))
        {
            Container belt = TetraBeltSupport.getToolbeltStorage(player);
            if (belt == null)
            {
                return false;
            }
            this.chosenContainer = new SimpleTableMenu.VariableSizeContainerWrapper(new TetraBeltTranslator(belt));
            this.chosenContainerTrueSize = belt.getContainerSize() / TetraBeltTranslator.GetRowWidth(belt) * 9;
            this.chosenContainerVisibleSize = this.chosenContainerTrueSize <= 27 ? 27 : 54;
            this.currentType = RecordTypes.TOOLBELT;
            return true;
        }
        else if (record.Type.equals(RecordTypes.LEGGINGS) || record.Type.equals(RecordTypes.CHESTSLOT) || record.Type.equals(RecordTypes.BACKSLOT))
        {
            ItemStack item = getItemFromNamedSlot(player, record.Type);
            LazyOptional<IItemHandler> pockets = item.getCapability(ForgeCapabilities.ITEM_HANDLER);
            pockets.ifPresent(inventory -> {
                this.chosenContainer = new SimpleTableMenu.VariableSizeItemStackHandlerWrapper(inventory);
                this.chosenContainerTrueSize = inventory.getSlots();
                this.currentType = record.Type;
            });
            return this.chosenContainerTrueSize > 0;
        }
        else
        {
            return false;
        }
    }

    public static ItemStack getItemFromNamedSlot(Player player, String slot)
    {
        if (slot.equals(RecordTypes.LEGGINGS))
        {
            return player.getInventory().getArmor(1);
        }
        else if (slot.equals(RecordTypes.CHESTSLOT))
        {
            return player.getInventory().getArmor(2);
        }
        else if (slot.equals(RecordTypes.BACKSLOT) && ModList.get().isLoaded("curios"))
        {
            return CuriosAccessor.getFirstItem(player, "back");
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

    ///////////////////////////////////////////////////////

    private void sort(BlockPos pos, BlockPos posSecondary)
    {
        InventoryAccessRecord left, right;
        for (int i = 0; i < this.adjacentInventories.size() - 1; i++)
        {
            for (int k = i + 1; k < this.adjacentInventories.size(); k++)
            {
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
        for (int i = 0; i < this.adjacentInventories.size() - 1; i++)
        {
            for (int k = i + 1; k < this.adjacentInventories.size(); k++)
            {
                left = this.adjacentInventories.get(i);
                right = this.adjacentInventories.get(k);
                if (right.Type.equals(RecordTypes.BLOCK) || left.Type.equals(RecordTypes.BLOCK))
                {
                    // either is belt?
                    if (right.Type.equals(RecordTypes.BLOCK) && !left.Type.equals(RecordTypes.BLOCK))
                    {
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
                if (posSecondary != null && left.y == right.y)
                {
                    if (pos.getX() == posSecondary.getX() && pos.getZ() < posSecondary.getZ())
                    {
                        if (left.z > right.z)
                        {
                            this.adjacentInventories.set(i, right);
                            this.adjacentInventories.set(k, left);
                        }
                    }
                    else if (pos.getX() == posSecondary.getX() && pos.getZ() > posSecondary.getZ())
                    {
                        if (left.z < right.z)
                        {
                            this.adjacentInventories.set(i, right);
                            this.adjacentInventories.set(k, left);
                        }
                    }
                    else if (pos.getZ() == posSecondary.getZ() && pos.getX() < posSecondary.getX())
                    {
                        // player facing north, table facing south. sort.
                        if (left.x > right.x)
                        {
                            this.adjacentInventories.set(i, right);
                            this.adjacentInventories.set(k, left);
                        }
                    }
                    else if (pos.getZ() == posSecondary.getZ() && pos.getX() > posSecondary.getX())
                    {
                        // player facing south, table facing north. sort.
                        if (left.x < right.x)
                        {
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
        public boolean Nameable = false;
        public String Type = "";
        public int VisibleSlotCount = 3;
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
