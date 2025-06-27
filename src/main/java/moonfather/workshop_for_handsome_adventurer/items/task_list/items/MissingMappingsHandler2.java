package moonfather.workshop_for_handsome_adventurer.items.task_list.items;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegisterEvent;

public class MissingMappingsHandler2
{
    // handles the missing mappings from old versions where i had to make a separate fire immune item
    public static void handleRegistryEvent(final RegisterEvent event)
    {
        if (event.getRegistryKey().equals(Registries.ITEM))
        {
            ResourceLocation missing = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "task_list_fi");
            ResourceLocation fallback = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "task_list");
            BuiltInRegistries.ITEM.addAlias(missing, fallback);
        }
    }
}
