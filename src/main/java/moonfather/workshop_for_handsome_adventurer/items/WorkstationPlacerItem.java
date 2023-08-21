package moonfather.workshop_for_handsome_adventurer.items;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.blocks.AdvancedTableBottomPrimary;
import moonfather.workshop_for_handsome_adventurer.initialization.ExternalWoodSupport;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WorkstationPlacerItem extends Item
{
	public WorkstationPlacerItem(String wood, Properties properties)
	{
		super(properties);
		this.Tooltip1 = Component.translatable("item.workshop_for_handsome_adventurer.workstation_placer.tooltip1").withStyle(Style.EMPTY.withItalic(true).withColor(0x9966cc));
		this.Tooltip2 = Component.translatable("item.workshop_for_handsome_adventurer.workstation_placer.tooltip2").withStyle(Style.EMPTY.withItalic(true).withColor(0x9966cc));
		this.woodType = wood;
	}

	public WorkstationPlacerItem(String wood)
	{
		this(wood, new Properties().stacksTo(1));
	}
	private final String woodType;


	private final MutableComponent Tooltip1, Tooltip2;
	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag advanced)
	{
		super.appendHoverText(itemStack, level, list, advanced);
		list.add(this.Tooltip1);
		list.add(this.Tooltip2);
	}



	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
	{
		if (context.getLevel().isClientSide)
		{
			return InteractionResult.SUCCESS;
		}
		BlockPos position = context.getClickedPos().relative(context.getClickedFace());
		boolean canPlace = this.checkCanPlace(context.getLevel(), position, context.getHorizontalDirection());
		if (!canPlace)
		{
			position = position.relative(context.getHorizontalDirection().getCounterClockWise());
			canPlace = this.checkCanPlace(context.getLevel(), position, context.getHorizontalDirection());
		}
		if (!canPlace)
		{
			context.getPlayer().displayClientMessage(Component.translatable("message.workshop_for_handsome_adventurer.no_room_for_workstation"), true);
			return InteractionResult.FAIL;
		}
		Direction facingToSet = context.getHorizontalDirection().getOpposite();
		Direction right = context.getHorizontalDirection().getClockWise();
		String hostModId = ExternalWoodSupport.getHostMod(this.woodType);
		String prefix = ExternalWoodSupport.getPrefix(this.woodType);
		Block bottomLeft = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(hostModId, prefix + "dual_table_bottom_left_" + this.woodType));
		Block bottomRight = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(hostModId, prefix + "dual_table_bottom_right_" + this.woodType));
		Block topLeft = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(hostModId, prefix + "dual_table_top_left_" + this.woodType));
		Block topRight = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(hostModId, prefix + "dual_table_top_right_" + this.woodType));
		context.getLevel().setBlock(position, bottomLeft.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, true), 0);
		context.getLevel().setBlock(position.above(), topLeft.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, true), 0);
		context.getLevel().setBlock(position.relative(right), bottomRight.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, true), 0);
		context.getLevel().setBlock(position.above().relative(right), topRight.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, true), 0);
		context.getLevel().setBlockAndUpdate(position, bottomLeft.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, false));
		context.getLevel().setBlockAndUpdate(position.above(), topLeft.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, false));
		context.getLevel().setBlockAndUpdate(position.relative(right), bottomRight.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, false));
		context.getLevel().setBlockAndUpdate(position.above().relative(right), topRight.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, false));

		if (!context.getPlayer().isCreative())
		{
			stack.shrink(1);
		}
		return InteractionResult.SUCCESS;
	}

	private boolean checkCanPlace(Level level, BlockPos position, Direction horizontalDirection)
	{
		BlockState current = level.getBlockState(position);
		if (! current.isAir() && ! current.canBeReplaced())
		{
			return false;
		}
		current = level.getBlockState(position.above());
		if (! current.isAir() && ! current.canBeReplaced())
		{
			return false;
		}
		current = level.getBlockState(position.relative(horizontalDirection.getClockWise()));
		if (! current.isAir() && ! current.canBeReplaced())
		{
			return false;
		}
		current = level.getBlockState(position.above().relative(horizontalDirection.getClockWise()));
		if (! current.isAir() && ! current.canBeReplaced())
		{
			return false;
		}
		return true;
	}
}
