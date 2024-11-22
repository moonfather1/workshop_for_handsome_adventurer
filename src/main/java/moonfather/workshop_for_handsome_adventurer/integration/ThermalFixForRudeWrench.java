package moonfather.workshop_for_handsome_adventurer.integration;

import moonfather.workshop_for_handsome_adventurer.blocks.DualTableBaseBlock;
import moonfather.workshop_for_handsome_adventurer.blocks.ToolRack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ThermalFixForRudeWrench
{
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void OnPlayerInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if (! event.getItemStack().isEmpty() && event.getItemStack().getItem().getClass().getSimpleName().equals("WrenchItem"))
        {
            Block block = event.getLevel().getBlockState(event.getHitVec().getBlockPos()).getBlock();
            if (block instanceof ToolRack || block instanceof DualTableBaseBlock)
            {
                event.setUseItem(Event.Result.DENY);
                event.setUseBlock(Event.Result.ALLOW);
            }
        }
    }
}
