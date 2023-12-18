package moonfather.workshop_for_handsome_adventurer.integration;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
//import se.mickelus.tetra.TetraToolActions;

public class TetraHammerSupport {
    public static boolean isHammer(ItemStack item)
    {
        return false;//return item.canPerformAction(TetraToolActions.hammer);
    }

    public static BlockState getWorkBench()
    {
        return BuiltInRegistries.BLOCK.get(new ResourceLocation("tetra", "basic_workbench")).defaultBlockState();
    }
}


