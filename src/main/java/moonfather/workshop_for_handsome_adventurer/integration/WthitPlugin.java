package moonfather.workshop_for_handsome_adventurer.integration;

import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.component.ItemComponent;
import moonfather.workshop_for_handsome_adventurer.block_entities.PotionShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.DualTableBaseBlock;
import moonfather.workshop_for_handsome_adventurer.blocks.PotionShelf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class WthitPlugin implements IWailaPlugin {
    @Override
    public void register(IRegistrar registrar) {
        registrar.addComponent(new WorkstationProvider(), TooltipPosition.HEAD, DualTableBaseBlock.class);
        registrar.addIcon(new WorkstationProvider(), DualTableBaseBlock.class);
        registrar.addComponent(new PotionShelfProvider(), TooltipPosition.TAIL, PotionShelfBlockEntity.class);
        registrar.addBlockData(new PotionShelfDataProvider(), PotionShelf.class);
    }

    ////////////////////////////////////

    private static class WorkstationProvider implements IBlockComponentProvider {
        private static final Map<Block, ItemComponent> map = new HashMap<>();
        private static final ResourceLocation topLine = new ResourceLocation("waila", "object_name");

        @Override
        public @Nullable ITooltipComponent getIcon(IBlockAccessor accessor, IPluginConfig config) {
            return this.getPlacerItem(accessor);
        }

        @Override
        public void appendHead(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
            tooltip.setLine(topLine, this.getPlacerItem(accessor).stack.getHoverName());
            //tooltip.addLine(this.getPlacerItem(accessor).stack.getHoverName());
            //tooltip.addLine().with(Component.literal("asdf")).with(this.getIcon(accessor, config));
        }


        private ItemComponent getPlacerItem(IBlockAccessor accessor) {
            Block block = accessor.getBlock();
            if (map.containsKey(block)) {
                return map.get(block);
            }
            ItemStack placer = block.getCloneItemStack(accessor.getBlockState(), accessor.getHitResult(), accessor.getWorld(), accessor.getPosition(), accessor.getPlayer());
            ItemComponent result = new ItemComponent(placer);
            map.put(block, result);
            return result;
        }
    }



    private static class PotionShelfProvider implements IBlockComponentProvider {
        @Override
        public void appendTail(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
            if (accessor.getBlockEntity() instanceof PotionShelfBlockEntity shelf)
            {
                int slot = PotionShelf.getPotionShelfSlot(accessor.getHitResult(), accessor.getPosition(), accessor.getSide());
                if (! shelf.GetItem(slot).isEmpty())
                {
                    int count, room;
                    if (accessor.getServerData().contains("Bottles" + slot))
                    {
                        count = accessor.getServerData().getInt("Bottles" + slot);
                        room = accessor.getServerData().getInt("Space" + slot);
                    }
                    else
                    {
                        count = shelf.GetRemainingItems(slot);
                        room = shelf.GetRemainingRoom(slot);
                    }
                    tooltip.addLine(Component.translatable(message, count, count+room));
                    tooltip.addLine(shelf.GetItem(slot).getHoverName());
                }
                else
                {
                    tooltip.addLine(Component.translatable(message, 0, shelf.GetRemainingRoom(slot)));
                }
            }
        }

        private static final String message = "message.workshop_for_handsome_adventurer.shelf_probe_tooltip";
    }

    private static class PotionShelfDataProvider implements IServerDataProvider<PotionShelfBlockEntity> {
        @Override
        public void appendServerData(CompoundTag compoundTag, IServerAccessor<PotionShelfBlockEntity> serverAccessor, IPluginConfig config) {
            int max = serverAccessor.getTarget().getNumberOfItemsInOneRow() * 2;
            for (int i = 0; i < max; i++)
            {
                int bottles = serverAccessor.getTarget().GetRemainingItems(i);
                compoundTag.putInt("Bottles" + i, bottles);
                int space = serverAccessor.getTarget().GetRemainingRoom(i);
                compoundTag.putInt("Space" + i, space);
            }
        }
    }
}
