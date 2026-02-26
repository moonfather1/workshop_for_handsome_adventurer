package moonfather.workshop_for_handsome_adventurer.integration;

import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.component.ItemComponent;
import moonfather.workshop_for_handsome_adventurer.ClientConfig;
import moonfather.workshop_for_handsome_adventurer.CommonConfig;
import moonfather.workshop_for_handsome_adventurer.block_entities.BookShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.block_entities.PotionShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.block_entities.ToolRackBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.block.Block;
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
        private static final Identifier topLine = Identifier.fromNamespaceAndPath("waila", "object_name");

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
            ItemStack placer = block.getCloneItemStack(accessor.getWorld(), accessor.getPosition(), accessor.getBlockState(), false, accessor.getPlayer());
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
                String message;  int slot;
                if (shelf.getNumberOfItems() == 9)
                {
                    message = messageD;
                    slot = DiscShelf.getDiscShelfSlot(accessor.getBlockHitResult(), accessor.getPosition(), accessor.getSide());
                }
                else
                {
                    message = messageP;
                    slot = PotionShelf.getPotionShelfSlot(accessor.getBlockHitResult(), accessor.getPosition(), accessor.getSide());
                }
                if (! shelf.GetItem(slot).isEmpty())
                {
                    int count, room;
                    if (accessor.getData().raw().contains("Bottles" + slot))
                    {
                        count = accessor.getData().raw().getIntOr("Bottles" + slot, 0);
                        room = accessor.getData().raw().getIntOr("Space" + slot, CommonConfig.SlotRoomMaximum.get()-count);
                    }
                    else
                    {
                        count = shelf.GetRemainingItems(slot);
                        room = shelf.GetRemainingRoom(slot);
                    }
                    ItemStack bottle = shelf.GetItem(slot);
                    tooltip.addLine().with(new ItemComponent(bottle)).with(bottle.getHoverName());
                    JukeboxPlayable songContainer = bottle.get(DataComponents.JUKEBOX_PLAYABLE);
                    if (songContainer != null)
                    {
                        EitherHolder<JukeboxSong> song = songContainer.song();
                        song.unwrap(accessor.getPlayer().registryAccess()).ifPresent(holder -> tooltip.addLine().with(holder.value().description()));
                    }
                    if (count+room > 1)  // no msg if no stacking
                    {
                        tooltip.addLine(Component.translatable(message, count, count + room));
                    }
                }
                else
                {
                    int roomTotal = shelf.GetRemainingRoom(slot);
                    if (roomTotal > 1)  // no msg if no stacking
                    {
                        tooltip.addLine(Component.translatable(message, 0, roomTotal));
                    }
                }
            }
        }

        private static final String messageP = "message.workshop_for_handsome_adventurer.shelf_probe_tooltip";
        private static final String messageD = "message.workshop_for_handsome_adventurer.shelf_probe_tooltip2";
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
