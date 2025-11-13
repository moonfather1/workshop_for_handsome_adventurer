package moonfather.workshop_for_handsome_adventurer.blocks;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IBlockWithCleverHoverText
{
    // lines to add to item hover tooltip.
    @NotNull
    List<Component> getTooltipLines();
}
