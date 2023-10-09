package moonfather.workshop_for_handsome_adventurer.block_entities;

import moonfather.workshop_for_handsome_adventurer.OptionsHolder;
import moonfather.workshop_for_handsome_adventurer.blocks.DualTableBaseBlock;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class DualTableMenu extends SimpleTableMenu
{
	public static final int CRAFT_SECONDARY_SLOT_START = ACCESS_SLOT_END + 1; //136
	public static final int CRAFT_SECONDARY_SLOT_END = CRAFT_SECONDARY_SLOT_START + 9 - 1; // 144
	public static final int SECONDARY_RESULT_SLOT = CRAFT_SECONDARY_SLOT_END + 1; // 145
	private final CraftingContainer craftSlotsSecondary = new TransientCraftingContainer(this, 3, 3);
	private final ResultContainer resultSlotsSecondary = new ResultContainer();
	public final int DATA_SLOT_JEI_RECIPE_TARGET;

	public DualTableMenu(int containerId, Inventory inventory, FriendlyByteBuf friendlyByteBuf)
	{
		this(containerId, inventory, ContainerLevelAccess.NULL, Registration.CRAFTING_DUAL_MENU_TYPE.get());
	}

	public DualTableMenu(int containerId, Inventory inventory, ContainerLevelAccess levelAccess, @Nullable MenuType<?> menuType) {
		super(containerId, inventory, levelAccess, menuType);
		this.initialLoading = true;
		this.addDataSlot(this.recipeTargetGridSlot);		DATA_SLOT_JEI_RECIPE_TARGET = this.getNextDataSlotId();
		this.access.execute(this::loadFromWorldPartTwo);

		//---crafting grid slots 2---
		for (int ver = 0; ver < 3; ++ver)
		{
			for (int hor = 0; hor < 3; ++hor)
			{
				this.addSlot(new Slot(this.craftSlotsSecondary, hor + ver * 3, 30-4 + hor * 18, -50 + 17 + ver * 18));
			}
		}
		//---crafting result slot 2---
		this.addSlot(new ResultSlot(inventory.player, this.craftSlotsSecondary, this.resultSlotsSecondary, 0, 124-4, 35+18));

		/////////////////////////////////////////////////////////////
		//fix locations to fit a new table

		Slot current;   int y0 = 0;
		//---player inventory slots---
		for (int k = INV_SLOT_START; k <= HOTBAR_ROW_SLOT_END; k++)
		{
			current = this.slots.get(k);
			current.y = current.y + 67;
		}
		//---cust slots---
		for (int k = CUST_SLOT_START; k <= CUST_SLOT_END; k++)
		{
			current = this.slots.get(k);
			((CustomizationSlot)current).setAcceptsLanterns(true);
			if (k == CUST_SLOT_START) { y0 = current.y - 9; };
			if ((k - CUST_SLOT_START) >= this.getCustomizationSlotCount()) { continue; } // leave them off-screen
			current.y = y0 + (k - CUST_SLOT_START) * (18 + 4);
		}
		//---crafting2---
		for (int k = CRAFT_SECONDARY_SLOT_START; k <= CRAFT_SECONDARY_SLOT_END; k++)
		{
			Slot primary = this.slots.get(k - CRAFT_SECONDARY_SLOT_START + CRAFT_SLOT_START);
			current = this.slots.get(k);
			current.y = primary.y;
			primary.y = primary.y + 3 * 18 + 13; // labels are 13px
		}
		Slot primary = this.slots.get(RESULT_SLOT);
		this.slots.get(SECONDARY_RESULT_SLOT).y = primary.y;
		primary.y = primary.y + 3 * 18 + 13;

		this.initialLoading = false;
	}


	@Override
	public int getCustomizationSlotCount()	{ return OptionsHolder.COMMON.DualTableNumberOfSlots.get(); }


	@Override
	public void slotsChanged(Container container)
	{
		if (container.equals(this.craftSlots))
		{
			this.access.execute( (level, pos) ->
			{
				slotChangedCraftingGrid(this, level, this.player, this.craftSlots, this.resultSlots);
			});
		}
		else if (container.equals(this.craftSlotsSecondary))
		{
			this.access.execute( (level, pos) ->
			{
				slotChangedCraftingGrid(this, level, this.player, this.craftSlotsSecondary, this.resultSlotsSecondary);
			});
		}
		else
		{
			// what container now?
			// this doesn't trigger for SimpleContainer
		}
	}



	public int getRecipeTargetGrid() {
		return this.recipeTargetGridSlot.get();
	}
	private final DataSlotWithNotification recipeTargetGridSlot = new DataSlotWithNotification(1);
	public void registerClientHandlerForRecipeTargetChange(Consumer<Integer> event)	{
		this.recipeTargetGridSlot.setEvent(event);
	}

	public void changeRecipeTargetGridTo(int grid) {
		if (grid >= 1 && grid <= 2 ) {
			this.recipeTargetGridSlot.set(grid);
			this.synchronizeDataSlotToRemote(DATA_SLOT_JEI_RECIPE_TARGET, grid);
		}
	}


	@Override
	protected void clearAdditional() {
		this.clearContainer(player, this.craftSlotsSecondary);
	}


	public boolean stillValid(Player player)
	{
		return access.evaluate((level, pos) ->
		{
			if (!(level.getBlockState(pos).getBlock() instanceof DualTableBaseBlock)) { return false; }
			return player.distanceToSqr((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D) <= 64.0D;
		}, true);
	}

	@Override
	protected boolean isSlotACraftingResultSlot(int index) { return index == RESULT_SLOT || index == SECONDARY_RESULT_SLOT; }
	@Override
	protected boolean isSlotACraftingGridSlot(int index) { return index >= CRAFT_SLOT_START && index <= CRAFT_SLOT_END
			|| index >= CRAFT_SECONDARY_SLOT_START && index <= CRAFT_SECONDARY_SLOT_END; }
	@Override
	protected boolean moveItemStackToCraftingGrid(ItemStack itemstack1) {
		return this.moveItemStackToOccupiedSlotsOnly(itemstack1, CRAFT_SLOT_START, CRAFT_SLOT_END+1, false)
				|| this.moveItemStackToOccupiedSlotsOnly(itemstack1, CRAFT_SECONDARY_SLOT_START, CRAFT_SECONDARY_SLOT_END+1, false);
	}



	public boolean canTakeItemForPickAll(ItemStack p_39381_, Slot slot) {
		return slot.container != this.resultSlotsSecondary && super.canTakeItemForPickAll(p_39381_, slot);
	}

	@Override
	protected int getSlotOffsetInDataStorage(Container container) {
		if (container.equals(this.craftSlots))
			return 0;
		else if (container.equals(this.craftSlotsSecondary))
			return 9 + 4;
		else
			return 101; // error
	}

	protected void loadFromWorldPartTwo(Level level, BlockPos pos)
	{
		DualTableBlockEntity be = (DualTableBlockEntity) level.getBlockEntity(pos);  assert be != null;
		for (int i = 0; i < this.craftSlotsSecondary.getContainerSize(); i++)
		{
			this.craftSlotsSecondary.setItem(i, be.GetItem(this.getSlotOffsetInDataStorage(this.craftSlotsSecondary) + i));
		}
		this.craftSlotsSecondary.setChanged();
		this.changeRecipeTargetGridTo(be.GetIntegerData(0));
	}

	@Override
	protected void storeDataValues(Level level, BlockPos pos) {
		DualTableBlockEntity be = (DualTableBlockEntity) level.getBlockEntity(pos);
		if (be != null)
		{
			be.StoreIntegerData(0, this.getRecipeTargetGrid());
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////

	public static class DataSlotWithNotification extends DataSlot
	{
		private int value = 0;
		private Consumer<Integer> event = null;

		public DataSlotWithNotification(int startingValue) { this.value = startingValue; }

		@Override
		public int get() { return this.value; }

		@Override
		public void set(int v) {
			this.value = v;
			if (event != null) {
				event.accept(v);
			}
		}

		public void setEvent(Consumer<Integer> newEvent) {
			this.event = newEvent;
		}
	}
}
