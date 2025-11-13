package moonfather.workshop_for_handsome_adventurer.items;

import moonfather.workshop_for_handsome_adventurer.blocks.DualToolRack;
import moonfather.workshop_for_handsome_adventurer.blocks.IBlockWithCleverHoverText;
import moonfather.workshop_for_handsome_adventurer.blocks.SimpleTable;
import moonfather.workshop_for_handsome_adventurer.blocks.ToolRack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.FuelValues;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class BlockItemEx extends BlockItem
{
    public BlockItemEx(Block block, Properties properties)
    {
        super(block, properties);
        if (block instanceof ToolRack)
        {
            if (! (block instanceof DualToolRack))
            {
                burnTime = 150;
            }
            else
            {
                burnTime = 900;
            }
        }
        else
        {
            burnTime = 300; //plank is 300
        }
    }


    private final int burnTime; //plank is 300
    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType, FuelValues fuelValues)
    {
        return this.burnTime;
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