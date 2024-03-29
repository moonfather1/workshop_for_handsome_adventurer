package moonfather.workshop_for_handsome_adventurer.block_entities;

import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleTableBlockEntity extends BaseContainerBlockEntity
{
	private final int CustomizationItemsOffset = 9;

	public SimpleTableBlockEntity(BlockPos pos, BlockState state)
	{
		super(Registration.SIMPLE_TABLE_BE.get(), pos, state);
		this.setCapacity(9 /*crafting*/ + 4 /*customization, was forced to put max here*/);
	}

	public SimpleTableBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state)
	{
		super(blockEntityType, pos, state);
		this.setCapacity(9 + 4);
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
