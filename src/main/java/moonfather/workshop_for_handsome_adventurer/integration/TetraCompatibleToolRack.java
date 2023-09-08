package moonfather.workshop_for_handsome_adventurer.integration;

import moonfather.workshop_for_handsome_adventurer.blocks.ToolRack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import se.mickelus.tetra.blocks.IToolProviderBlock;

import java.util.Collection;

public class TetraCompatibleToolRack extends ToolRack implements IToolProviderBlock
{
    private ToolRack rack;
    public TetraCompatibleToolRack(int itemCount, String subType)
    {
        super(itemCount, subType);
    }

    public TetraCompatibleToolRack(int itemCount, String mainType, String subType)
    {
        super(itemCount, mainType, subType);
    }

    public TetraCompatibleToolRack(int itemCount, String mainType, String subType, Properties properties)
    {
        super(itemCount, mainType, subType, properties);
    }

    ////////////////////////////////////////////////

    public boolean canProvideTools(Level world, BlockPos pos, BlockPos targetPos) {
        return true;
    }

    public Collection<ToolAction> getTools(Level world, BlockPos pos, BlockState blockState)
    {
        return TetraCompatibleToolRackHelper.getTools(world, pos, this.itemCount);
    }

    public int getToolLevel(Level world, BlockPos pos, BlockState blockState, ToolAction toolAction)
    {
        return TetraCompatibleToolRackHelper.getToolLevel(world, pos, this.itemCount, toolAction);
    }

    public ItemStack onCraftConsumeTool(Level world, BlockPos pos, BlockState blockState, ItemStack targetStack, String slotArg, boolean isReplacing, Player player, ToolAction requiredTool, int requiredLevel, boolean consumeResources)
    {
        return TetraCompatibleToolRackHelper.onCraftConsumeTool(world, pos, this.itemCount, targetStack, player, requiredTool, requiredLevel, consumeResources);
    }

    public ItemStack onActionConsumeTool(Level world, BlockPos pos, BlockState blockState, ItemStack targetStack, Player player, ToolAction requiredTool, int requiredLevel, boolean consumeResources)
    {
        return TetraCompatibleToolRackHelper.onActionConsumeTool(world, pos, this.itemCount, targetStack, player, requiredTool, requiredLevel, consumeResources);
    }
}
