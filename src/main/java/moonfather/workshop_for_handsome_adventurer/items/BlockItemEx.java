package moonfather.workshop_for_handsome_adventurer.items;

import moonfather.workshop_for_handsome_adventurer.blocks.DualToolRack;
import moonfather.workshop_for_handsome_adventurer.blocks.SimpleTable;
import moonfather.workshop_for_handsome_adventurer.blocks.ToolRack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class BlockItemEx extends BlockItem
{
	public BlockItemEx(Block block, Properties properties)
	{
		super(block, properties);
		if (block instanceof ToolRack && ! (block instanceof DualToolRack))
		{
			burnTime = 300;
		}
		else
		{
			burnTime = 900;
		}
		this.isSmallTable = block instanceof SimpleTable;
	}


	public boolean isTable() { return this.isSmallTable; }

	private final boolean isSmallTable;
	private int burnTime = 300; //plank is 300
	@Override
	public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType)
	{
		return burnTime;
	}
}
