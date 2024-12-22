package moonfather.workshop_for_handsome_adventurer.block_entities.containers;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class SimpleContainerEx extends SimpleContainer
{
    // constructor
    public SimpleContainerEx(int size) { super(size); }

    /// it was a question where to put this.
    /// can be in slot code or container code, but all containers and slots are 1-purpose classes and i didn't feel like another wrapper.
    @Override
    public boolean canPlaceItem(int slot, ItemStack stack)
    {
        if (! stack.getItem().canFitInsideContainerItems())
        {
            return false; //todo: check portable.
        } // todo: VariableSizeContainerWrapper doesn't call this
        return super.canPlaceItem(slot, stack);
    }
}
