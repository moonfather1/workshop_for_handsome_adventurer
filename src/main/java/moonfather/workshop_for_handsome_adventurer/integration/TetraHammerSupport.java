package moonfather.workshop_for_handsome_adventurer.integration;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
//import se.mickelus.tetra.TetraToolActions;

public class TetraHammerSupport {
    public static boolean isHammer(ItemStack item)
    {
        return false;//return item.canPerformAction(TetraToolActions.hammer);
    }

    public static BlockState getWorkBench()
    {
        Optional<Holder.Reference<Block>> nowINeedAVariableToGetABlockFromARegistry;  // thanks, morons. this wasn't retarded in 1.21.1.
        nowINeedAVariableToGetABlockFromARegistry = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("tetra", "basic_workbench"));
        if (nowINeedAVariableToGetABlockFromARegistry.isEmpty()) { return null; }
        return nowINeedAVariableToGetABlockFromARegistry.get().value().defaultBlockState();
    }
}


