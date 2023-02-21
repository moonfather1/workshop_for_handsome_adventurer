package moonfather.workshop_for_handsome_adventurer.block_entities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.naming.Name;
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
                        if (be instanceof net.minecraft.world.level.block.entity.BaseContainerBlockEntity bcbe) {
                            record.Name = bcbe.getName();
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
    ////////////////////////////////////////////

    static class InventoryAccessRecord
    {
        public Component Name = null;
        public ItemStack ItemChest = ItemStack.EMPTY;
        public ItemStack ItemFirst = ItemStack.EMPTY;
        public int Index, x, y, z;
    }
}
