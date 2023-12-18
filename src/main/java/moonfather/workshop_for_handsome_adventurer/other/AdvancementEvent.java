package moonfather.workshop_for_handsome_adventurer.other;

import moonfather.workshop_for_handsome_adventurer.items.BlockItemEx;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@Mod.EventBusSubscriber
public class AdvancementEvent
{
    @SubscribeEvent
    public static void OnItemCrafted(PlayerEvent.ItemCraftedEvent event)
    {
        if (event.getCrafting().getItem() instanceof BlockItemEx bi && bi.isTable())
        {
            if (event.getEntity() instanceof ServerPlayer sp)
            {
                sp.getAdvancements().award(GetAdvancement(sp, "minecraft", "story/root"), "crafting_table");
            }
        }
    }



    private static AdvancementHolder GetAdvancement(ServerPlayer sp, String namespace, String name)
    {
        return sp.getServer().getAdvancements().get(new ResourceLocation(namespace, name));
    }
}
