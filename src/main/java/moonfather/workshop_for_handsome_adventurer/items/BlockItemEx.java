package moonfather.workshop_for_handsome_adventurer.items;

import moonfather.workshop_for_handsome_adventurer.blocks.DualToolRack;
import moonfather.workshop_for_handsome_adventurer.blocks.IBlockWithCleverHoverText;
import moonfather.workshop_for_handsome_adventurer.blocks.ToolRack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.function.Consumer;

public class BlockItemEx extends BlockItem
{
    public BlockItemEx(Block block, Properties properties)
    {
        var burnTime = ContextIntProviders.COOKING_TIME_WOOD_BLOCKS; //plank is 300
        if (block instanceof ToolRack)
        {
            if (! (block instanceof DualToolRack))
            {
                burnTime = ContextIntProviders.COOKING_TIME_WOOD_SLABS; // 150
            }
            else
            {
                burnTime = ContextIntProviders.COOKING_TIME_HANGING_SIGNS; // 800
            }
        }
        super(block, properties.cookingFuel(burnTime));
    }

    ///////////////

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag)
    {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
        if (this.getBlock() instanceof IBlockWithCleverHoverText ourBlock)
        {
            for (Component c : ourBlock.getTooltipLines())
            {
                tooltipAdder.accept(c);
            }
        }
    }
}