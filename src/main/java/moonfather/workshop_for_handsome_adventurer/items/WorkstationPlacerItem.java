package moonfather.workshop_for_handsome_adventurer.items;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.blocks.AdvancedTableBottomPrimary;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.Consumer;

public class WorkstationPlacerItem extends Item
{
	public WorkstationPlacerItem(String wood, Properties properties)
	{
		super(properties);
		this.Tooltip1 = Component.translatable("item.workshop_for_handsome_adventurer.workstation_placer.tooltip1").withStyle(Style.EMPTY.withItalic(true).withColor(0x9966cc));
		this.Tooltip2 = Component.translatable("item.workshop_for_handsome_adventurer.workstation_placer.tooltip2").withStyle(Style.EMPTY.withItalic(true).withColor(0x9966cc));
		this.woodType = wood;
	}
	public WorkstationPlacerItem(String wood, Properties properties, String hostMod, String prefix)
	{
		this(wood, properties);
		this.hostModId = hostMod;
		this.prefix = prefix;
	}
	private final String woodType;


	private final MutableComponent Tooltip1, Tooltip2;
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag)
	{
		super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
		tooltipAdder.accept(this.Tooltip1);
		tooltipAdder.accept(this.Tooltip2);
	}

	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		if (context.getLevel().isClientSide())
		{
			return InteractionResult.SUCCESS;
		}
		BlockPos position = context.getClickedPos().relative(context.getClickedFace());
		boolean canPlace = this.checkCanPlace(context.getLevel(), position, context.getHorizontalDirection());
		if (! canPlace)
		{
			position = position.relative(context.getHorizontalDirection().getCounterClockWise());
			canPlace = this.checkCanPlace(context.getLevel(), position, context.getHorizontalDirection());
		}
		if (! canPlace)
		{
			if (context.getPlayer() != null)
			{
				context.getPlayer().displayClientMessage(Component.translatable("message.workshop_for_handsome_adventurer.no_room_for_workstation"), true);
			}
			return InteractionResult.FAIL;
		}
		Direction facingToSet = context.getHorizontalDirection().getOpposite();
		Direction right = context.getHorizontalDirection().getClockWise();
		Block bottomLeft = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(this.hostModId, this.prefix + "dual_table_bottom_left_" + this.woodType)).get().value();
		Block bottomRight = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(this.hostModId, this.prefix + "dual_table_bottom_right_" + this.woodType)).get().value();
		Block topLeft = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(this.hostModId, this.prefix + "dual_table_top_left_" + this.woodType)).get().value();
		Block topRight = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(this.hostModId, this.prefix + "dual_table_top_right_" + this.woodType)).get().value();
		context.getLevel().setBlock(position, bottomLeft.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, true), 0);
		context.getLevel().setBlock(position.above(), topLeft.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, true), 0);
		context.getLevel().setBlock(position.relative(right), bottomRight.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, true), 0);
		context.getLevel().setBlock(position.above().relative(right), topRight.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, true), 0);
		context.getLevel().setBlockAndUpdate(position, bottomLeft.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, false));
		context.getLevel().setBlockAndUpdate(position.above(), topLeft.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, false));
		context.getLevel().setBlockAndUpdate(position.relative(right), bottomRight.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, false));
		context.getLevel().setBlockAndUpdate(position.above().relative(right), topRight.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, false));

		if (context.getPlayer() != null && ! context.getPlayer().isCreative())
		{
			context.getItemInHand().shrink(1); // consume used to do this? this is probably a wrong method on 1.21
		}
		return InteractionResult.CONSUME;
	}
	protected String prefix = "";
	protected String hostModId = Constants.MODID;

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
	{
		return super.onItemUseFirst(stack, context);
//		if (context.getLevel().isClientSide)
//		{
//			return InteractionResult.SUCCESS;
//		}
//		BlockPos position = context.getClickedPos().relative(context.getClickedFace());
//		boolean canPlace = this.checkCanPlace(context.getLevel(), position, context.getHorizontalDirection());
//		if (! canPlace)
//		{
//			position = position.relative(context.getHorizontalDirection().getCounterClockWise());
//			canPlace = this.checkCanPlace(context.getLevel(), position, context.getHorizontalDirection());
//		}
//		if (! canPlace)
//		{
//			context.getPlayer().displayClientMessage(Component.translatable("message.workshop_for_handsome_adventurer.no_room_for_workstation"), true);
//			return InteractionResult.FAIL;
//		}
//		Direction facingToSet = context.getHorizontalDirection().getOpposite();
//		Direction right = context.getHorizontalDirection().getClockWise();
//		String hostModId = ExternalWoodSupport.getHostMod(this.woodType);
//		String prefix = ExternalWoodSupport.getPrefix(this.woodType);
//		Block bottomLeft = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(hostModId, prefix + "dual_table_bottom_left_" + this.woodType));
//		Block bottomRight = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(hostModId, prefix + "dual_table_bottom_right_" + this.woodType));
//		Block topLeft = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(hostModId, prefix + "dual_table_top_left_" + this.woodType));
//		Block topRight = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(hostModId, prefix + "dual_table_top_right_" + this.woodType));
//		context.getLevel().setBlock(position, bottomLeft.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, true), 0);
//		context.getLevel().setBlock(position.above(), topLeft.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, true), 0);
//		context.getLevel().setBlock(position.relative(right), bottomRight.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, true), 0);
//		context.getLevel().setBlock(position.above().relative(right), topRight.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, true), 0);
//		context.getLevel().setBlockAndUpdate(position, bottomLeft.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, false));
//		context.getLevel().setBlockAndUpdate(position.above(), topLeft.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, false));
//		context.getLevel().setBlockAndUpdate(position.relative(right), bottomRight.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, false));
//		context.getLevel().setBlockAndUpdate(position.above().relative(right), topRight.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facingToSet).setValue(AdvancedTableBottomPrimary.BEING_PLACED, false));
//
//		if (! context.getPlayer().isCreative())
//		{
//			stack.shrink(1);
//		}
//		return InteractionResult.SUCCESS;
	}

	private boolean checkCanPlace(Level level, BlockPos position, Direction horizontalDirection)
	{
		if (position.getY() >= level.getMaxY())
		{
			return false;
		}
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
