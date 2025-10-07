package moonfather.workshop_for_handsome_adventurer.integration;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.util.Optional;

public class PackingTape
{
    public static boolean isTape(ItemStack stack) {
        if (! checkInitialized) {
            if (ModList.get().isLoaded("packingtape")) {
                Optional<Holder.Reference<Item>> optionalHolder = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("packingtape", "tape"));
                if (optionalHolder.isPresent()) { tape = optionalHolder.get().value();}
            }
            checkInitialized = true;
        }
        return tape != null && stack.is(tape);
    }
    private static boolean checkInitialized = false;
    private static Item tape = null;
}
