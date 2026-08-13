package moonfather.workshop_for_handsome_adventurer.other;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.blocks.SimpleTable;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.WoodTypeLister;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers.BlockTagWriter2;
import moonfather.workshop_for_handsome_adventurer.initialization.ContentRegistration;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class BookShelfRemovalHandler
{
    // handles the missing mappings
    public static void prepareAliases()
    {
        for (String wood : ContentRegistration.woodTypes)
        {
            Identifier missing = Identifier.fromNamespaceAndPath(Constants.MODID, "book_shelf_minimal_" + wood);
            Identifier fallback = Identifier.fromNamespaceAndPath(Constants.MODID, "book_shelf_open_minimal_" + wood);
            BuiltInRegistries.BLOCK.addAlias(missing, fallback);
        }
        for (String wood : WoodTypeLister.getWoodIds())
        {
            Identifier missing = Identifier.fromNamespaceAndPath(Constants.MODID, "book_shelf_minimal_" + wood);
            Identifier fallback = Identifier.fromNamespaceAndPath(Constants.MODID, "book_shelf_open_minimal_" + wood);
            BuiltInRegistries.BLOCK.addAlias(missing, fallback);
        }
    }
}
