package moonfather.workshop_for_handsome_adventurer.integration;

import moonfather.workshop_for_handsome_adventurer.block_entities.ToolRackBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.ToolRack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.ToolAction;


import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class TetraCompatibleToolRackHelper
{
    public static ToolRack create(boolean dual, int itemCount, String subType)
    {
        if (dual)
        {
            return new moonfather.workshop_for_handsome_adventurer.integration.TetraCompatibleDualToolRack(itemCount, subType);
        }
        else
        {
            return new moonfather.workshop_for_handsome_adventurer.integration.TetraCompatibleToolRack(itemCount, subType);
        }
    }

    public static ToolRack create(boolean dual, int itemCount, String mainType, String subType)
    {
        if (dual)
        {
            return new moonfather.workshop_for_handsome_adventurer.integration.TetraCompatibleDualToolRack(itemCount, subType);
        }
        else
        {
            return new moonfather.workshop_for_handsome_adventurer.integration.TetraCompatibleToolRack(itemCount, mainType, subType);
        }
    }

    public static ToolRack create(boolean dual, int itemCount, String mainType, String subType, BlockBehaviour.Properties properties)
    {
        if (dual)
        {
            return new moonfather.workshop_for_handsome_adventurer.integration.TetraCompatibleDualToolRack(itemCount, subType);
        }
        else
        {
            return new moonfather.workshop_for_handsome_adventurer.integration.TetraCompatibleToolRack(itemCount, mainType, subType, properties);
        }
    }

    ///////////////////////////////////

    public static Collection<ToolAction> getTools(Level world, BlockPos pos, int itemCount)
    {
        Collection<ToolAction> result = new HashSet<>();
        /*if (world.getBlockEntity(pos) instanceof ToolRackBlockEntity be)
        {
            for (int slot = 0; slot < itemCount; slot++)
            {
                ItemStack tool = be.GetItem(slot);
                if (tool.isEmpty()) { continue; }
                ItemStack replacement = ItemUpgradeRegistry.instance.getReplacement(tool);
                if (! replacement.isEmpty()) { tool = replacement; }
                if (tool.getItem() instanceof IToolProvider tp)
                {
                    Map<ToolAction, Integer> map = tp.getToolLevels(tool);
                    for (Map.Entry<ToolAction, Integer> entry: map.entrySet())
                    {
                        if (entry.getValue() > 0) // likely always
                        {
                            result.add(entry.getKey());
                        }
                    }
                }
            }
        }*/
        return result;
    }



    public static int getToolLevel(Level world, BlockPos pos, int itemCount, ToolAction toolAction)
    {
        int result = -1;/*
        if (world.getBlockEntity(pos) instanceof ToolRackBlockEntity be)
        {
            for (int slot = 0; slot < itemCount; slot++)
            {
                ItemStack tool = be.GetItem(slot);
                if (tool.isEmpty()) { continue; }
                ItemStack replacement = ItemUpgradeRegistry.instance.getReplacement(tool);
                if (! replacement.isEmpty()) { tool = replacement; }
                if (tool.getItem() instanceof IToolProvider tp)
                {
                    Map<ToolAction, Integer> map = tp.getToolLevels(tool);
                    int level = map.getOrDefault(toolAction, -1);
                    if (level > result)
                    {
                        result = level;
                    }
                }
            }
        }*/
        return result;
    }

    public static ItemStack onCraftConsumeTool(Level world, BlockPos pos, int itemCount, ItemStack targetStack, Player player, ToolAction requiredTool, int requiredLevel, boolean consumeResources)
    {/*
        if (world.getBlockEntity(pos) instanceof ToolRackBlockEntity be)
        {
            for (int slot = 0; slot < itemCount; slot++)
            {
                ItemStack tool = be.GetItem(slot);
                if (tool.isEmpty()) { continue; }
                ItemStack replacement = ItemUpgradeRegistry.instance.getReplacement(tool);
                if (! replacement.isEmpty()) { tool = replacement; }
                if (tool.getItem() instanceof IToolProvider tp)
                {
                    Map<ToolAction, Integer> map = tp.getToolLevels(tool);
                    int level = map.getOrDefault(requiredTool, -1);
                    if (level >= requiredLevel)
                    {
                        return ((IToolProvider)tool.getItem()).onCraftConsume(tool, targetStack, player, requiredTool, requiredLevel, consumeResources);
                    }
                }
            }
        }*/
        return null;
    }



    public static ItemStack onActionConsumeTool(Level world, BlockPos pos, int itemCount, ItemStack targetStack, Player player, ToolAction requiredTool, int requiredLevel, boolean consumeResources)
    {/*
        if (world.getBlockEntity(pos) instanceof ToolRackBlockEntity be)
        {
            for (int slot = 0; slot < itemCount; slot++)
            {
                ItemStack tool = be.GetItem(slot);
                if (tool.isEmpty()) { continue; }
                ItemStack replacement = ItemUpgradeRegistry.instance.getReplacement(tool);
                if (! replacement.isEmpty()) { tool = replacement; }
                if (tool.getItem() instanceof IToolProvider tp)
                {
                    Map<ToolAction, Integer> map = tp.getToolLevels(tool);
                    int level = map.getOrDefault(requiredTool, -1);
                    if (level >= requiredLevel)
                    {
                        return ((IToolProvider)tool.getItem()).onActionConsume(tool, targetStack, player, requiredTool, requiredLevel, consumeResources);
                    }
                }
            }
        }*/
        return null;
    }
}
