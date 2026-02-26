package moonfather.workshop_for_handsome_adventurer.other;

import moonfather.workshop_for_handsome_adventurer.blocks.SimpleTable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class AdvancementEvent
{
    @SubscribeEvent
    public static void OnItemCrafted(PlayerEvent.ItemCraftedEvent event)
    {
        if (event.getCrafting().getItem() instanceof BlockItem bi && bi.getBlock() instanceof SimpleTable)
        {
            if (event.getEntity() instanceof ServerPlayer sp)
            {
                AdvancementHolder ah = sp.level().getServer().getAdvancements().get(Identifier.fromNamespaceAndPath("minecraft", "story/root"));
                sp.getAdvancements().award(ah, "crafting_table");
            }
        }
    }
}
