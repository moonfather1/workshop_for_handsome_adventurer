package moonfather.workshop_for_handsome_adventurer.block_entities;

import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleTableBlockEntity extends BaseContainerBlockEntity
{
	private final int CustomizationItemsOffset = 9;

	public SimpleTableBlockEntity(BlockPos pos, BlockState state)
	{
		super(Registration.SIMPLE_TABLE_BE.get(), pos, state);
		this.capacity = 9 /*crafting*/ + 2 /*customization*/;
	}

	public void DepositCustomizationItem(int slot, ItemStack itemStack)
	{
		this.DepositItem(slot + CustomizationItemsOffset, itemStack);
	}

	public ItemStack GetCustomizationItem(int slot)
	{
		return this.GetItem(slot + CustomizationItemsOffset);
	}
}
