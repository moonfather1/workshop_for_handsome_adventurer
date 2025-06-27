package moonfather.workshop_for_handsome_adventurer.integration;

import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.component.ItemComponent;
import moonfather.workshop_for_handsome_adventurer.ClientConfig;
import moonfather.workshop_for_handsome_adventurer.block_entities.BookShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.block_entities.PotionShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.block_entities.ToolRackBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WthitPlugin implements IWailaPlugin {
    @Override
    public void register(IRegistrar registrar) {
        registrar.addComponent(new WorkstationProvider(), TooltipPosition.HEAD, DualTableBaseBlock.class);
        registrar.addIcon(new WorkstationProvider(), DualTableBaseBlock.class);
        registrar.addComponent(new PotionShelfProvider(), TooltipPosition.TAIL, PotionShelfBlockEntity.class);
        registrar.addBlockData(new PotionShelfDataProvider(), PotionShelf.class);
        registrar.addComponent(new BookShelfProvider(), TooltipPosition.TAIL, BookShelfBlockEntity.class);
        registrar.addComponent(new ToolRackProvider1(), TooltipPosition.TAIL, ToolRackBlockEntity.class);
        registrar.addComponent(new ToolRackProvider2(), TooltipPosition.TAIL, DualToolRack.class);
    }

    ////////////////////////////////////

    private static class WorkstationProvider implements IBlockComponentProvider {
        private static final Map<Block, ItemComponent> map = new HashMap<>();
        private static final ResourceLocation topLine = ResourceLocation.fromNamespaceAndPath("waila", "object_name");

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
            ItemStack placer = block.getCloneItemStack(accessor.getBlockState(), accessor.getBlockHitResult(), accessor.getWorld(), accessor.getPosition(), accessor.getPlayer());
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
                int slot = PotionShelf.getPotionShelfSlot(accessor.getBlockHitResult(), accessor.getPosition(), accessor.getSide());
                if (! shelf.GetItem(slot).isEmpty())
                {
                    int count, room;
                    if (accessor.getData().raw().contains("Bottles" + slot))
                    {
                        count = accessor.getData().raw().getInt("Bottles" + slot);
                        room = accessor.getData().raw().getInt("Space" + slot);
                    }
                    else
                    {
                        count = shelf.GetRemainingItems(slot);
                        room = shelf.GetRemainingRoom(slot);
                    }
                    ItemStack bottle = shelf.GetItem(slot);
                    tooltip.addLine().with(new ItemComponent(bottle)).with(bottle.getHoverName());
                    tooltip.addLine(Component.translatable(message, count, count+room));
                }
                else
                {
                    tooltip.addLine(Component.translatable(message, 0, shelf.GetRemainingRoom(slot)));
                }
            }
        }

        private static final String message = "message.workshop_for_handsome_adventurer.shelf_probe_tooltip";
    }

    private static class PotionShelfDataProvider implements IDataProvider<PotionShelfBlockEntity>
    {
        @Override
        public void appendData(IDataWriter writer, IServerAccessor<PotionShelfBlockEntity> serverAccessor, IPluginConfig config) {
            int max = serverAccessor.getTarget().getNumberOfItemsInOneRow() * 2;
            for (int i = 0; i < max; i++)
            {
                int bottles = serverAccessor.getTarget().GetRemainingItems(i);
                writer.raw().putInt("Bottles" + i, bottles);
                int space = serverAccessor.getTarget().GetRemainingRoom(i);
                writer.raw().putInt("Space" + i, space);
            }
        }
    }

    private static class BookShelfProvider extends WailaBaseProvider implements IBlockComponentProvider
    {
        @Override
        public void appendTail(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
            if (accessor.getBlockEntity() instanceof BookShelfBlockEntity shelf)
            {
                int slot = BookShelf.getBookShelfSlot((BookShelf) accessor.getBlock(), accessor.getBlockHitResult());
                if (slot >= 0 && ! shelf.GetItem(slot).isEmpty())
                {
                    tooltip.addLine().with(new ItemComponent(shelf.GetItem(slot))).with(shelf.GetItem(slot).getHoverName());
                    if (ClientConfig.detailedWailaInfoForEnchantedBooks)
                    {
                        List<Component> enchantments = this.getEnchantmentParts(shelf.GetItem(slot));
                        if (enchantments != null)
                        {
                            for (int i = 0; i < enchantments.size(); i += 1)
                            {
                                tooltip.addLine().with(enchantments.get(i));
                            }
                        }
                    }
                }
            }
        }
    }

    private static class ToolRackProvider1 extends WailaBaseProvider implements IBlockComponentProvider
    {
        @Override
        public void appendTail(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
            ItemStack tool = ItemStack.EMPTY;
            if (accessor.getBlockEntity() instanceof PotionShelfBlockEntity || accessor.getBlockEntity() instanceof BookShelfBlockEntity)
            {
                return;
            }
            if (accessor.getBlockEntity() instanceof ToolRackBlockEntity rack)
            {
                int slot = ToolRack.getToolRackSlot((ToolRack) accessor.getBlock(), accessor.getBlockHitResult());
                tool = rack.GetItem(slot);
            }
            if (tool.isEmpty())
            {
                return;
            }
            tooltip.addLine().with(new ItemComponent(tool)).with(tool.getHoverName());
            if (ClientConfig.detailedWailaInfoForEnchantedTools)
            {
                List<Component> enchantments = this.getEnchantmentParts(tool);
                if (enchantments != null)
                {
                    for (int i = 0; i < enchantments.size(); i += 1)
                    {
                        tooltip.addLine().with(enchantments.get(i));
                    }
                }
            }
        }
    }

    private static class ToolRackProvider2 extends WailaBaseProvider implements IBlockComponentProvider
    {
        @Override
        public void appendTail(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
            ItemStack tool = ItemStack.EMPTY;
            if (accessor.getBlock() instanceof DualToolRack block && accessor.getBlockEntity() == null)
            {
                BlockPos above = accessor.getPosition().above();
                ToolRackBlockEntity rack = (ToolRackBlockEntity) accessor.getWorld().getBlockEntity(above);
                int slot = ToolRack.getToolRackSlot(block, accessor.getBlockHitResult().withPosition(above));
                tool = rack.GetItem(slot);
            }
            if (tool.isEmpty())
            {
                return;
            }
            tooltip.addLine().with(new ItemComponent(tool)).with(tool.getHoverName());
            if (ClientConfig.detailedWailaInfoForEnchantedTools)
            {
                List<Component> enchantments = this.getEnchantmentParts(tool);
                if (enchantments != null)
                {
                    for (int i = 0; i < enchantments.size(); i += 1)
                    {
                        tooltip.addLine().with(enchantments.get(i));
                    }
                }
            }
        }
    }
}
