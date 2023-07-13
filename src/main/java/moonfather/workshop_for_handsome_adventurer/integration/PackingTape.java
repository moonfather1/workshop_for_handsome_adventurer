package moonfather.workshop_for_handsome_adventurer.integration;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class PackingTape
{
    public static boolean isTape(ItemStack stack) {
        if (! checkInitialized) {
            if (ModList.get().isLoaded("packingtape")) {
                tape = ForgeRegistries.ITEMS.getValue(new ResourceLocation("packingtape", "tape"));
            }
            checkInitialized = true;
        }
        return tape != null && stack.is(tape);
    }
    private static boolean checkInitialized = false;
    private static Item tape = null;
}
