package moonfather.workshop_for_handsome_adventurer.integration;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.OptionsHolder;
import moonfather.workshop_for_handsome_adventurer.block_entities.BookShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.block_entities.PotionShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.block_entities.ToolRackBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.BookShelf;
import moonfather.workshop_for_handsome_adventurer.blocks.PotionShelf;
import moonfather.workshop_for_handsome_adventurer.blocks.ToolRack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class TOPInfoProvider extends WailaBaseProvider implements IProbeInfoProvider
{
    @Override
    public ResourceLocation getID()
    {
        return new ResourceLocation(Constants.MODID, "top_shelf");
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo probeInfo, Player player, Level level, BlockState blockState, IProbeHitData probeHitData)
    {
        if (this.optionsCacheTime <= 0)
        {
            this.optionsTools = OptionsHolder.CLIENT.DetailedWailaInfoForEnchantedTools.get();
            this.optionBooks = OptionsHolder.CLIENT.DetailedWailaInfoForEnchantedBooks.get();
            this.optionsCacheTime = 55;
        }
        if (blockState.getBlock() instanceof PotionShelf)
        {
            int slot = PotionShelf.getPotionShelfSlot(new BlockHitResult(probeHitData.getHitVec(), blockState.getValue(PotionShelf.FACING).getOpposite(), probeHitData.getPos(), true));
            PotionShelfBlockEntity shelf = (PotionShelfBlockEntity) level.getBlockEntity(probeHitData.getPos());
            int count = shelf.GetRemainingItems(slot);
            int total =  shelf.GetRemainingRoom(slot) + count;
            probeInfo.text(Component.translatable("message.workshop_for_handsome_adventurer.shelf_probe_tooltip", count, total).withStyle(Style.EMPTY.withColor(0xaa77dd)));
            return;
        }
        if (blockState.getBlock() instanceof BookShelf)
        {
            int slot = BookShelf.getBookShelfSlot((BookShelf) blockState.getBlock(), new BlockHitResult(probeHitData.getHitVec(), blockState.getValue(BookShelf.FACING).getOpposite(), probeHitData.getPos(), true));
            if (slot >= 0)
            {
                BookShelfBlockEntity shelf = (BookShelfBlockEntity) level.getBlockEntity(probeHitData.getPos());
                ItemStack book = shelf.GetItem(slot);
                probeInfo.horizontal().item(book).vertical().padding(2, 4).itemLabel(book);
                if (this.optionBooks)
                {
                    List<Component> enchantments = this.getEnchantmentParts(book);
                    if (enchantments != null)
                    {
                        for (int i = 0; i < enchantments.size(); i += 2)
                        {
                            probeInfo.horizontal().padding(30, 4).text(enchantments.get(i)).text(enchantments.get(i + 1));
                        }
                    }
                }
            }
            return; // because bookshelves are toolracks too and would enter the branch below.
        }
        if (blockState.getBlock() instanceof ToolRack block)
        {
            ToolRackBlockEntity rack = (ToolRackBlockEntity) level.getBlockEntity(probeHitData.getPos());
            int slot;
            if (rack != null)
            {
                slot = ToolRack.getToolRackSlot(block, new BlockHitResult(probeHitData.getHitVec(), blockState.getValue(PotionShelf.FACING).getOpposite(), probeHitData.getPos(), true));
            }
            else
            {
                rack = (ToolRackBlockEntity) level.getBlockEntity(probeHitData.getPos().above());
                slot = ToolRack.getToolRackSlot(block, new BlockHitResult(probeHitData.getHitVec(), blockState.getValue(PotionShelf.FACING).getOpposite(), probeHitData.getPos().above(), true));
            }
            ItemStack tool = rack.GetItem(slot);
            if (slot >= 0 && ! tool.isEmpty())
            {
                probeInfo.horizontal().item(tool).vertical().padding(2, 4).itemLabel(tool);
                if (this.optionsTools)
                {
                    List<Component> enchantments = this.getEnchantmentParts(tool);
                    if (enchantments != null)
                    {
                        for (int i = 0; i < enchantments.size(); i += 2)
                        {
                            probeInfo.horizontal().padding(30, 4).text(enchantments.get(i)).text(enchantments.get(i + 1));
                        }
                    }
                }
            }
        }
    }

    private int optionsCacheTime = 0;
    private boolean optionBooks = false, optionsTools = false;
}
