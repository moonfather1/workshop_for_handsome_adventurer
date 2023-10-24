package moonfather.workshop_for_handsome_adventurer.other;

import moonfather.workshop_for_handsome_adventurer.items.BlockItemEx;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
