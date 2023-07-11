package moonfather.workshop_for_handsome_adventurer.integration;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.PotionShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.PotionShelf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class TOPInfoProvider implements IProbeInfoProvider
{
    @Override
    public ResourceLocation getID()
    {
        return new ResourceLocation(Constants.MODID, "top_shelf");
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo probeInfo, Player player, Level level, BlockState blockState, IProbeHitData probeHitData)
    {
        if (blockState.getBlock() instanceof PotionShelf)
        {
            int slot = PotionShelf.getPotionShelfSlot(new BlockHitResult(probeHitData.getHitVec(), blockState.getValue(PotionShelf.FACING).getOpposite(), probeHitData.getPos(), true));
            PotionShelfBlockEntity shelf = (PotionShelfBlockEntity) level.getBlockEntity(probeHitData.getPos());
            int count = shelf.GetRemainingItems(slot);
            int total =  shelf.GetRemainingRoom(slot) + count;
            probeInfo.text(Component.translatable("message.workshop_for_handsome_adventurer.shelf_probe_tooltip", count, total).withStyle(Style.EMPTY.withColor(0xaa77dd)));
        }
    }
}
