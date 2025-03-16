package moonfather.workshop_for_handsome_adventurer.other;

import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber
public class TableLockManager
{
    public static boolean isLocked(Level level, BlockPos pos)
    {
        if (level == null || pos == null) { return false; }
        Long key = makeKey(level, pos);
        return map.getOrDefault(key, null) != null;
    }



    private static Long makeKey(Level level, BlockPos pos)
    {
        return Long.rotateLeft(level.hashCode(), 32) + pos.hashCode();
    }
    private static final Map<Long, UUID> map = new HashMap<>();
    private static final Map<Integer, Long> indexMap = new HashMap<>();
    private static final Map<Integer, UUID> protoMap = new HashMap<>();



    public static void register(int containerId, Level level, BlockPos pos)
    {
        if (level == null || pos == null || level.isClientSide())
        {
            return;
        }
        Long key = makeKey(level, pos);
        indexMap.put(containerId, key);
        if (protoMap.containsKey(containerId))
        {
            map.put(key, protoMap.get(containerId));
        }
    }



    public static Component getPlayerName(Level level, BlockPos pos)
    {
        if (level != null && pos != null && ! level.isClientSide())
        {
            Long key = makeKey(level, pos);
            UUID id = map.getOrDefault(key, null);
            if (id != null)
            {
                Player player = level.getPlayerByUUID(id);
                if (player != null)
                {
                    return player.getDisplayName();
                }
            }
        }
        return Component.literal("--");
    }

    //////////////////////////////////////

    @SubscribeEvent
    public static void onOpenGUI(PlayerContainerEvent.Open event)
    {
        // server-only event; not cancellable.
        if (event.getContainer() instanceof SimpleTableMenu)
        {
            int containerId = event.getContainer().containerId;
            protoMap.put(containerId, event.getEntity().getUUID());
            // register method was supposed to finish the job, but now it fires before this.
            if (indexMap.containsKey(containerId))
            {
                Long key = indexMap.get(containerId);
                map.put(key, event.getEntity().getUUID());
            }
        }
    }



    @SubscribeEvent
    public static void onCloseGUI(PlayerContainerEvent.Close event)
    {
        // server-only event; not cancellable.
        if (event.getContainer() instanceof SimpleTableMenu)
        {
            if (indexMap.containsKey(event.getContainer().containerId))
            {
                long key = indexMap.get(event.getContainer().containerId);
                indexMap.remove(event.getContainer().containerId);
                map.remove(key); // check player??
                protoMap.remove(event.getContainer().containerId);
            }
        }
    }
}
