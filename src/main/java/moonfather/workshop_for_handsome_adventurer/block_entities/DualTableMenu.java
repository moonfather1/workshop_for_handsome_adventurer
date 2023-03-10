package moonfather.workshop_for_handsome_adventurer.block_entities;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.OptionsHolder;
import moonfather.workshop_for_handsome_adventurer.blocks.DualTableBaseBlock;
import moonfather.workshop_for_handsome_adventurer.blocks.SimpleTable;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

public class DualTableMenu extends SimpleTableMenu
{
	public static final int LEFT_PANEL_WIDTH = 176;
	public static final int CRAFT_SECONDARY_SLOT_START = ACCESS54_SLOT_END + 1; //136
	public static final int CRAFT_SECONDARY_SLOT_END = CRAFT_SECONDARY_SLOT_START + 9 - 1; // 144
	public static final int CRAFT_TERTIARY_SLOT_START = CRAFT_SECONDARY_SLOT_END + 1; // 145
	public static final int CRAFT_TERTIARY_SLOT_END = CRAFT_TERTIARY_SLOT_START + 9 - 1; // 153
	public static final int SECONDARY_RESULT_SLOT = CRAFT_TERTIARY_SLOT_END + 1; // 154
	private final CraftingContainer craftSlotsSecondary = new CraftingContainer(this, 3, 3);
	private final CraftingContainer craftSlotsTertiary = new CraftingContainer(this, 3, 3);
	private final ResultContainer resultSlotsSecondary = new ResultContainer();

	public DualTableMenu(int containerId, Inventory inventory, FriendlyByteBuf friendlyByteBuf)
	{
		this(containerId, inventory, ContainerLevelAccess.NULL, Registration.CRAFTING_DUAL_MENU_TYPE.get());
	}

	public DualTableMenu(int containerId, Inventory inventory, ContainerLevelAccess levelAccess, @Nullable MenuType<?> menuType) {
		super(containerId, inventory, levelAccess, menuType);
		this.initialLoading = true;
		this.access.execute(this::loadFromWorldPartTwo);

		//---crafting grid slots 2---
		for (int ver = 0; ver < 3; ++ver)
		{
			for (int hor = 0; hor < 3; ++hor)
			{
				this.addSlot(new Slot(this.craftSlotsSecondary, hor + ver * 3, 30-12 + hor * 18, -50 + 17 + ver * 18));
			}
		}

		//---crafting grid slots 3 (for jei)---
		for (int ver = 0; ver < 3; ++ver)
		{
			for (int hor = 0; hor < 3; ++hor)
			{
				this.addSlot(new Slot(this.craftSlotsTertiary, hor + ver * 3, -20000 + hor * 18, 17 + ver * 18));
			}
		}

		//---crafting result slot 2---
		this.addSlot(new ResultSlot(inventory.player, this.craftSlotsSecondary, this.resultSlotsSecondary, 0, 124-12, 35+18));

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
	protected int getCustomizationSlotCount()	{ return OptionsHolder.COMMON.DualTableNumberOfSlots.get(); }


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
		else if (container.equals(this.craftSlotsTertiary))
		{
			this.access.execute( (level, pos) ->
			{
				slotChangedCraftingGridJEI(this, level, this.player, this.craftSlotsTertiary);
			});
		}
		else
		{
			// what container now?
			// this doesn't trigger for SimpleContainer
		}
	}

	private void slotChangedCraftingGridJEI(DualTableMenu dualTableMenu, Level level, Player player, CraftingContainer craftSlots) {
		System.out.println("~~~ tertiary grid changed");
	}


	@Override
	protected void clearAdditional() {
		this.clearContainer(player, this.craftSlotsSecondary);
		this.verifyEmpty(player, this.craftSlotsTertiary);
	}

	private void verifyEmpty(Player player, CraftingContainer container) {
		for (int i = 0; i < container.getContainerSize(); i++) {
			if (! container.getItem(i).isEmpty()) {
				System.out.println("!!! ERROR  NON EMPTY IN SLOT " + i + ":   " + container.getItem(i));
			}
		}
	}


	public boolean stillValid(Player player)
	{
		return access.evaluate((level, pos) ->
		{
			if (!(level.getBlockState(pos).getBlock() instanceof DualTableBaseBlock)) { return false; }
			return player.distanceToSqr((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D) <= 64.0D;
		}, true);
	}



	public ItemStack quickMoveStack(Player player, int slotIndex) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotIndex);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (slotIndex == 0) { //shift on result
				this.access.execute((level, p_39379_) -> {
					itemstack1.getItem().onCraftedBy(itemstack1, level, player);
				});
				//reverse: hotbar first, then inv
				if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, HOTBAR_ROW_SLOT_END+1, true)) {
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(itemstack1, itemstack);
			} else if (slotIndex >= INV_SLOT_START && slotIndex <= HOTBAR_ROW_SLOT_END) { //from player
				if (!this.moveItemStackTo(itemstack1, CRAFT_SLOT_START, CRAFT_SLOT_END+1, false)) {
					// no room on crafting grid, try chest
					if (!this.moveItemStackToOccupiedSlotsOnly(itemstack1, ACCESS27_SLOT_START, ACCESS27_SLOT_END+1, false)) {
						// try inv->hotbar or hotbar->inv
						if (slotIndex < HOTBAR_ROW_SLOT_START) {
							if (!this.moveItemStackTo(itemstack1, HOTBAR_ROW_SLOT_START, HOTBAR_ROW_SLOT_END + 1, false)) {
								return ItemStack.EMPTY;
							}
						} else if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, INV_SLOT_END + 1, false)) {
							return ItemStack.EMPTY;
						}
					}
				}
			} else if (slotIndex >= ACCESS27_SLOT_START && slotIndex <= ACCESS27_SLOT_END) { //from chest
				if (!this.moveItemStackTo(itemstack1, CRAFT_SLOT_START, CRAFT_SLOT_END+1, false)) {
					if (!this.moveItemStackTo(itemstack1, HOTBAR_ROW_SLOT_START, HOTBAR_ROW_SLOT_END + 1, true)) {
						if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, INV_SLOT_END + 1, false)) {
							return ItemStack.EMPTY;
						}
					}
				}
			} else if (slotIndex >= CRAFT_SLOT_START && slotIndex <= CRAFT_SLOT_END) { //from crafting
				if (! this.showInventoryAccess() || ! this.moveItemStackToOccupiedSlotsOnly(itemstack1, ACCESS27_SLOT_START, ACCESS27_SLOT_END+1, false)) {
					if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, HOTBAR_ROW_SLOT_END+1, false)) {
						return ItemStack.EMPTY;
					}
				}
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, itemstack1);
			if (slotIndex == 0) {
				player.drop(itemstack1, false);
			}
		}

		return itemstack;
	}









	public boolean canTakeItemForPickAll(ItemStack p_39381_, Slot slot) {
		return slot.container != this.resultSlotsSecondary&& super.canTakeItemForPickAll(p_39381_, slot);
	}

	@Override
	protected int getSlotOffsetInDataStorage(Container container) {
		if (container.equals(this.craftSlots))
			return 0;
		else if (container.equals(this.craftSlotsSecondary))
			return 9 + 4;
		else
			return 0; // error
	}

	protected void loadFromWorldPartTwo(Level level, BlockPos pos)
	{
		SimpleTableBlockEntity be = (SimpleTableBlockEntity) level.getBlockEntity(pos);  assert be != null;
		for(int i = 0; i < this.craftSlotsSecondary.getContainerSize(); i++)
		{
			this.craftSlotsSecondary.setItem(i, be.GetItem(this.getSlotOffsetInDataStorage(this.craftSlotsSecondary) + i));
		}
		this.craftSlotsSecondary.setChanged();
	}
}
