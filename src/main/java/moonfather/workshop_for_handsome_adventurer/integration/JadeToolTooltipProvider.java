package moonfather.workshop_for_handsome_adventurer.integration;


import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.OptionsHolder;
import moonfather.workshop_for_handsome_adventurer.block_entities.ToolRackBlockEntity;
import moonfather.workshop_for_handsome_adventurer.block_entities.PotionShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.ToolRack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeConfigSpec;


public class JadeToolTooltipProvider extends JadeBaseTooltipProvider implements IComponentProvider
{
    private static final JadeToolTooltipProvider instance = new JadeToolTooltipProvider();
    public static JadeToolTooltipProvider getInstance() { return instance; }


    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config)
    {
        if (accessor.getBlockEntity() instanceof ToolRackBlockEntity rack && ! (rack instanceof PotionShelfBlockEntity))
        {
            int slot = ToolRack.getToolRackSlot((ToolRack) accessor.getBlock(), accessor.getHitResult());
            if (slot >= 0 && ! rack.GetItem(slot).isEmpty())
            {
                this.appendTooltipInternal(tooltip, rack.GetItem(slot));
            }
        }
        else if (accessor.getBlock() instanceof ToolRack rackBlock)
        {
            if (accessor.getLevel().getBlockEntity(accessor.getPosition().above()) instanceof ToolRackBlockEntity rack)
            {
                int slot = ToolRack.getToolRackSlot((ToolRack) accessor.getBlock(), new BlockHitResult(accessor.getHitResult().getLocation(), accessor.getHitResult().getDirection(), accessor.getHitResult().getBlockPos().above(), false));
                if (slot >= 0 && ! rack.GetItem(slot).isEmpty())
                {
                    this.appendTooltipInternal(tooltip, rack.GetItem(slot));
                }
            }
        }
    }

    @Override
    protected ForgeConfigSpec.ConfigValue<Boolean> getOption()
    {
        return OptionsHolder.CLIENT.DetailedWailaInfoForEnchantedTools;
    }
}
