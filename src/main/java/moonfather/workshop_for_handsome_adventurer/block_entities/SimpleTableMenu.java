package moonfather.workshop_for_handsome_adventurer.block_entities;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.OptionsHolder;
import moonfather.workshop_for_handsome_adventurer.block_entities.container_translators.IExcessSlotManager;
import moonfather.workshop_for_handsome_adventurer.block_entities.messaging.PacketSender;
import moonfather.workshop_for_handsome_adventurer.blocks.AdvancedTableBottomPrimary;
import moonfather.workshop_for_handsome_adventurer.blocks.SimpleTable;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import moonfather.workshop_for_handsome_adventurer.integration.TetraBeltSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleTableMenu extends AbstractContainerMenu
{
	public static final int CUST_CONTAINER_SIZE = 4;
	public static final int TAB_SMUGGLING_CONTAINER_SIZE = 32; // 16 tabs max
	public static final int TAB_SMUGGLING_SOFT_LIMIT = 8; // that much fits above dialog
	public static final int LEFT_PANEL_WIDTH = 176;
	public static final int RESULT_SLOT = 0;
	public static final int CRAFT_SLOT_START = 1;
	public static final int CRAFT_SLOT_END = CRAFT_SLOT_START + 9 - 1;//9
	public static final int INV_SLOT_START = CRAFT_SLOT_END + 1;//10;
	public static final int INV_SLOT_END = INV_SLOT_START + 27 - 1;//36;
	public static final int HOTBAR_ROW_SLOT_START = INV_SLOT_END + 1;//37;
	public static final int HOTBAR_ROW_SLOT_END = HOTBAR_ROW_SLOT_START + 9 - 1;//45;
	public static final int CUST_SLOT_START = HOTBAR_ROW_SLOT_END + 1;//46;
	public static final int CUST_SLOT_END = CUST_SLOT_START + CUST_CONTAINER_SIZE - 1;//49;
	public static final int TABS_SLOT_START = CUST_SLOT_END + 1;//50;
	public static final int TABS_SLOT_END = TABS_SLOT_START + TAB_SMUGGLING_CONTAINER_SIZE - 1;//81;
	public static final int ACCESS_SLOT_START = TABS_SLOT_END + 1;//82;
	public static final int ACCESS_SLOT_END = ACCESS_SLOT_START + 54 - 1;//108;
	protected final CraftingContainer craftSlots = new TransientCraftingContainer(this, 3, 3);
	protected final ResultContainer resultSlots = new ResultContainer();
	protected final Player player;
	protected final ContainerLevelAccess access;
	private final SimpleContainer customizationSlots = new SimpleContainer(CUST_CONTAINER_SIZE);
	private final Container tabElements = new SimpleContainer(TAB_SMUGGLING_CONTAINER_SIZE); // magic to transfer to client
	private Container chestSlots = null;
	protected boolean initialLoading = true;


	public SimpleTableMenu(int containerId, Inventory inventory, FriendlyByteBuf friendlyByteBuf)
	{
		this(containerId, inventory, ContainerLevelAccess.NULL, Registration.CRAFTING_SINGLE_MENU_TYPE.get());
	}

	public SimpleTableMenu(int containerId, Inventory inventory, ContainerLevelAccess levelAccess, @Nullable MenuType<?> menuType)
	{
		super(menuType, containerId);
		this.access = levelAccess;
		this.player = inventory.player;
		this.DataSlots.addSlots();

		//---crafting result slot---
		this.addSlot(new ResultSlot(inventory.player, this.craftSlots, this.resultSlots, 0, 124-4, 35));

		//---crafting grid slots---
		for (int ver = 0; ver < 3; ++ver)
		{
			for (int hor = 0; hor < 3; ++hor)
			{
				this.addSlot(new Slot(this.craftSlots, hor + ver * 3, 30-4 + hor * 18, 17 + ver * 18));
			}
		}

		//---player inventory slots---
		for (int ver = 0; ver < 3; ++ver)
		{
			for (int hor = 0; hor < 9; ++hor)
			{
				this.addSlot(new Slot(inventory, hor + ver * 9 + 9, 8 + hor * 18, 84 + ver * 18));
			}
		}

		//---player hotbar slots---
		for (int hor = 0; hor < 9; ++hor)
		{
			this.addSlot(new Slot(inventory, hor, 8 + hor * 18, 142));
		}

		//---customization slots (chest to store items, etc.)---
		int custSlotCount = this.getCustomizationSlotCount();
		this.addSlot(new CustomizationSlot(this.customizationSlots, 0, 152, 17 + 0*22 + ((0 < custSlotCount) ? 0 : 9009)));
		this.addSlot(new CustomizationSlot(this.customizationSlots, 1, 152, 17 + 1*22 + ((1 < custSlotCount) ? 0 : 9009)));
		this.addSlot(new CustomizationSlot(this.customizationSlots, 2, 152, 17 + 2*22 + ((2 < custSlotCount) ? 0 : 9009)));
		this.addSlot(new CustomizationSlot(this.customizationSlots, 3, 152, 17 + 3*22 + ((3 < custSlotCount) ? 0 : 9009)));
		this.access.execute(this::loadFromWorld);
		if (! this.player.level().isClientSide)
		{
			this.customizationSlots.addListener(new CustomizationListenerServer(this));
		}
		else
		{
			this.customizationSlots.addListener(new CustomizationListenerClient(this));
		}

		//---slots to sneak tab images to the client---
		for (int i = 0; i < this.tabElements.getContainerSize(); i++)
		{
			this.addSlot(new Slot(this.tabElements, i, 9009, 9009+i*30));
		}
		this.storeAdjacentInventoriesInSlots();

		//---slots for adjacent inventories - initialization --
		this.chestSlots = new DisabledContainer(54); // we won't call tryInitializeFirstInventoryAccess here. we did, but after some changes, we had all of this double-called.
		this.setUpperContainerTrueSize(54);  // ...instead we'll leave these 2 dummy lines. they do nothing.

		//---slots for adjacent inventories - creation --
		for (int ver = 0; ver < this.chestSlots.getContainerSize()/9; ++ver)
		{
			for (int hor = 0; hor < 9; ++hor)
			{
				this.addSlot(new VariableSizeContainerSlot(this.chestSlots, ver*9+hor, 5 + hor * 18 - LEFT_PANEL_WIDTH, 30 + ver * 18, this::getUpperContainerTrueSize, this::isSlotSpecificallyDisabled));
			}
		}
		this.initialLoading = false;
	}


	public DataSlot addDataSlot(DataSlot slot) { return super.addDataSlot(slot); }



	public int getCustomizationSlotCount()	{ return OptionsHolder.COMMON.SimpleTableNumberOfSlots.get(); }



	protected static void slotChangedCraftingGrid(AbstractContainerMenu menu, Level level, BlockPos tablePos, Player player, CraftingContainer craftingContainer, ResultContainer resultContainer)
	{
		if (! level.isClientSide)
		{
			ServerPlayer serverplayer = (ServerPlayer)player;
			ItemStack itemstack = ItemStack.EMPTY;
			Optional<CraftingRecipe> optional = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingContainer, level);
			if (optional.isPresent())
			{
				CraftingRecipe craftingrecipe = optional.get();
				if (resultContainer.setRecipeUsed(level, serverplayer, craftingrecipe))
				{
					itemstack = craftingrecipe.assemble(craftingContainer, level.registryAccess());
				}
			}

			resultContainer.setItem(0, itemstack);
			menu.setRemoteSlot(0, itemstack);
			serverplayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), 0, itemstack));
			updateInventoryOnClientSide(level, tablePos); // syncs inventory of client BE from sever BE. needed for renderer. all these months, until 1.09 client BE had item list what was read when loading level, and we were fine - menu would get fresh items and BE would stay oblivious.
		}
	}

	public void slotsChanged(Container container)
	{
		if (isCraftingGrid(container))
		{
			this.access.execute( (level, pos) ->
			{
				slotChangedCraftingGrid(this, level, pos, this.player, this.craftSlots, this.resultSlots);
			});
		}
		else
		{
			// what container now?
			// this doesn't trigger for SimpleContainer
		}
	}

	protected void clearAdditional()  {}

	public void removed(Player player)
	{
		super.removed(player);
		boolean hasDrawer = hasChestInCustomizationSlots();
		this.clearContainer(player, this.craftSlots);
		this.clearAdditional();
		this.access.execute( (level, pos) -> this.storeDataValues(level, pos));
		this.access.execute( (level, pos) -> this.storeCustomizationsToWorld(this.customizationSlots, level, pos));
		this.access.execute( (level, pos) -> updateDrawerInWorld(level, pos, hasDrawer));
		this.access.execute( (level, pos) -> updateInventoryOnClientSide(level, pos)); // see comment for the same call in this file
	}

	private static void updateInventoryOnClientSide(Level level, BlockPos pos)
	{
		BlockState state = level.getBlockState(pos);
		level.sendBlockUpdated(pos, state, state, 2);
	}

	protected void storeDataValues(Level level, BlockPos pos) { }

	public boolean stillValid(Player player)
	{
		return access.evaluate((level, pos) ->
		{
			if (!(level.getBlockState(pos).getBlock() instanceof SimpleTable)) { return false; }
			return player.distanceToSqr((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D) <= 64.0D;
		}, true);
	}

	protected boolean isSlotACraftingResultSlot(int index) { return index == RESULT_SLOT; }
	protected boolean isSlotACraftingGridSlot(int index) { return index >= CRAFT_SLOT_START && index <= CRAFT_SLOT_END; }

	protected boolean moveItemStackToCraftingGrid(ItemStack itemstack1)
	{
		return this.moveItemStackToOccupiedSlotsOnly(itemstack1, CRAFT_SLOT_START, CRAFT_SLOT_END+1, false);
	}

	public ItemStack quickMoveStack(Player player, int slotIndex)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotIndex);
		if (slot != null && slot.hasItem())
		{
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (this.isSlotACraftingResultSlot(slotIndex)) { //shift on result
				this.access.execute((level, p_39379_) -> {
					itemstack1.getItem().onCraftedBy(itemstack1, level, player);
				});
				//reverse: hotbar first, then inv
				if (! this.moveItemStackTo(itemstack1, INV_SLOT_START, HOTBAR_ROW_SLOT_END+1, true))
				{
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(itemstack1, itemstack);
			}
			else if (slotIndex >= INV_SLOT_START && slotIndex <= HOTBAR_ROW_SLOT_END)
			{ //from player
				if (! this.moveItemStackToCraftingGrid(itemstack1))
				{
					// not prioritizing crafting grid anymore (or unlikely no room), try chest
					if (! this.showInventoryAccess() || ! this.moveItemStackToOccupiedSlotsOnly(itemstack1, ACCESS_SLOT_START, ACCESS_SLOT_END+1, false))
					{
						// try inv->hotbar or hotbar->inv
						if (slotIndex < HOTBAR_ROW_SLOT_START)
						{
							if (! this.moveItemStackTo(itemstack1, HOTBAR_ROW_SLOT_START, HOTBAR_ROW_SLOT_END + 1, false))
							{
								return ItemStack.EMPTY;
							}
						}
						else if (! this.moveItemStackTo(itemstack1, INV_SLOT_START, INV_SLOT_END + 1, false))
						{
							return ItemStack.EMPTY;
						}
					}
				}
			}
			else if (slotIndex >= ACCESS_SLOT_START && slotIndex <= ACCESS_SLOT_END)
			{ //from chest
				if (! this.moveItemStackToCraftingGrid(itemstack1))
				{
					if (! this.moveItemStackToOccupiedSlotsOnly(itemstack1, INV_SLOT_START, INV_SLOT_END + 1, false))
					{
						if (! this.moveItemStackTo(itemstack1, HOTBAR_ROW_SLOT_START, HOTBAR_ROW_SLOT_END + 1, true))
						{
							if (! this.moveItemStackTo(itemstack1, INV_SLOT_START, INV_SLOT_END + 1, false))
							{
								return ItemStack.EMPTY;
							}
						}
					}
				}
			}
			else if (isSlotACraftingGridSlot(slotIndex))
			{ //from crafting
				if (! this.showInventoryAccess() || ! this.moveItemStackToOccupiedSlotsOnly(itemstack1, ACCESS_SLOT_START, ACCESS_SLOT_END+1, false))
				{
					if (! this.moveItemStackTo(itemstack1, INV_SLOT_START, HOTBAR_ROW_SLOT_END + 1, false))
					{
						return ItemStack.EMPTY;
					}
				}
			}

			if (itemstack1.isEmpty())
			{
				slot.set(ItemStack.EMPTY);
			}
			else
			{
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount())
			{
				return ItemStack.EMPTY;
			}

			slot.onTake(player, itemstack1);
			if (slotIndex == 0)
			{
				player.drop(itemstack1, false);
			}
		}

		return itemstack;
	}



	protected boolean moveItemStackToOccupiedSlotsOnly(ItemStack itemStack, int startingSlot, int endingSlotPlus1, boolean reverse)
	{
		boolean result = false;
		int i = startingSlot;
		if (reverse)
		{
			i = endingSlotPlus1 - 1;
		}

		if (itemStack.isStackable())
		{
			while (! itemStack.isEmpty())
			{
				if (reverse)
				{
					if (i < startingSlot)
					{
						break;
					}
				}
				else if (i >= endingSlotPlus1)
				{
					break;
				}

				Slot slot = this.slots.get(i);
				if (reverse)
				{
					--i;
				}
				else
				{
					++i;
				} // don't use i anymore
				if (! slot.isActive()) { continue; }
				ItemStack itemstack = slot.getItem();
				if (! itemstack.isEmpty() && ItemStack.isSameItemSameTags(itemStack, itemstack))
				{
					int j = itemstack.getCount() + itemStack.getCount();
					int maxSize = Math.min(slot.getMaxStackSize(), itemStack.getMaxStackSize());
					if (j <= maxSize)
					{
						itemStack.setCount(0);
						itemstack.setCount(j);
						slot.setChanged();
						result = true;
					}
					else if (itemstack.getCount() < maxSize)
					{
						itemStack.shrink(maxSize - itemstack.getCount());
						itemstack.setCount(maxSize);
						slot.setChanged();
						result = true;
					}
				}

			}
		}

		return result;
	}



	public boolean canTakeItemForPickAll(ItemStack p_39381_, Slot slot)
	{
		return slot.container != this.resultSlots
				&& slot.container != this.tabElements
				&& slot.container != this.customizationSlots
				&& slot.isActive()
				&& super.canTakeItemForPickAll(p_39381_, slot);
	}



	@Override
	protected void clearContainer(Player player, Container container)
	{
		if (isCraftingGrid(container))
		{
			this.access.execute(
					(level, pos) ->
					{
						if (! (level.getBlockEntity(pos) instanceof SimpleTableBlockEntity))
						{
							super.clearContainer(player, container);
							return;
						}
						if (! this.hasChestInCustomizationSlots())
						{
							if (this.showInventoryAccess())
							{
								this.clearContainerWithInventoryAccess(player, container); // will place to chests
							}
							super.clearContainer(player, container); // will place to player
							this.clearInWorld(container, level, pos);
							return;
						}
						//if (! player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer) player).hasDisconnected())
						// {
						//	super.clearContainer(player, container); // will drop
						//	this.clearInWorld(level, pos);
						//	return;
						//}
						this.storeCraftingGridToWorld(container, level, pos);
					}
			);
		}
		else
		{
			// what container now?
		}
	}



	protected void clearContainerWithInventoryAccess(Player player, Container container)
	{
		if (! player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer)player).hasDisconnected())
		{
			for (int j = 0; j < container.getContainerSize(); ++j)
			{
				player.drop(container.removeItemNoUpdate(j), false);
			}
		}
		else
		{
			for (int i = 0; i < container.getContainerSize(); ++i)
			{
				moveItemStackToOccupiedSlotsOnly(container.getItem(i), ACCESS_SLOT_START, ACCESS_SLOT_END+1, false);
			}
		}
	}



	private boolean isCraftingGrid(Container container)
	{
		return container instanceof CraftingContainer;
	}

	protected int getSlotOffsetInDataStorage(Container container)
	{
		return 0;
	}

	private void storeCraftingGridToWorld(Container container, Level level, BlockPos pos)
	{
		SimpleTableBlockEntity be = (SimpleTableBlockEntity) level.getBlockEntity(pos);  if (be == null) return; // no drop?
		for (int i = 0; i < container.getContainerSize(); i++)
		{
			be.DepositItem(this.getSlotOffsetInDataStorage(container) + i, container.removeItemNoUpdate(i));
		}
	}

	private void storeCustomizationsToWorld(Container container, Level level, BlockPos pos)
	{
		SimpleTableBlockEntity be = (SimpleTableBlockEntity) level.getBlockEntity(pos);
		if (be == null) return;
		for (int i = 0; i < container.getContainerSize(); i++)
		{
			be.DepositCustomizationItem(i, container.removeItemNoUpdate(i));
		}
	}

	protected void loadFromWorld(Level level, BlockPos pos)
	{
		SimpleTableBlockEntity be = (SimpleTableBlockEntity) level.getBlockEntity(pos);  if (be == null) return;
		for (int i = 0; i < this.craftSlots.getContainerSize(); i++)
		{
			this.craftSlots.setItem(i, be.GetItem(i));
		}
		this.craftSlots.setChanged();
		for (int i = 0; i < this.customizationSlots.getContainerSize(); i++)
		{
			this.customizationSlots.setItem(i, be.GetCustomizationItem(i));
		}
	}



	private void clearInWorld(Container container, Level level, BlockPos pos)
	{
		BaseContainerBlockEntity be = (BaseContainerBlockEntity) level.getBlockEntity(pos);
		for (int i = 0; i < container.getContainerSize(); i++)
		{
			be.ClearItem(this.getSlotOffsetInDataStorage(container) + i);
		}
	}



	private void updateDrawerInWorld(Level level, BlockPos pos, boolean hasDrawer)
	{
		BlockState state = level.getBlockState(pos);
		if (! (state.getBlock() instanceof SimpleTable)) { return; }
		if (state.getValue(SimpleTable.HAS_INVENTORY) == false && hasDrawer)
		{
			level.setBlock(pos, state.setValue(SimpleTable.HAS_INVENTORY, true), 2 + 128);
		}
		else if (state.getValue(SimpleTable.HAS_INVENTORY) && ! hasDrawer)
		{
			level.setBlock(pos, state.setValue(SimpleTable.HAS_INVENTORY, false), 2 + 128);
		}
	}

	private boolean hasChestInCustomizationSlots()
	{
		for (int i = 0; i < this.customizationSlots.getContainerSize(); i++)
		{
			if (this.customizationSlots.getItem(i).is(CustomizationSlot.ChestTag))
			{
				return true;
			}
		}
		return false;
	}


	public boolean showInventoryAccess()
	{
		if (this.tabElements.getItem(0).isEmpty())
		{
			return false; // no tabs
		}
		for (int i = 0; i < this.customizationSlots.getContainerSize(); i++)
		{
			if (this.customizationSlots.getItem(i).is(CustomizationSlot.getAccessItem()))
			{
				return true;
			}
		} // name tags?
		return false;
	}

	public int getInventoryAccessRange()
	{
		int result = 0;
		for (int i = 0; i < this.customizationSlots.getContainerSize(); i++)
		{
			if (this.customizationSlots.getItem(i).is(CustomizationSlot.getAccessItem()))
			{
				result += 1;
			}
		}
		if (result > 2) result = 2;
		return result;
	}

	public int getLanternCount()
	{
		int result = 0;
		for (int i = 0; i < this.customizationSlots.getContainerSize(); i++)
		{
			ItemStack stack = this.customizationSlots.getItem(i);
			if (stack.is(CustomizationSlot.LanternTag))
			{
				if (stack.getCount() > result)
				{
					result = stack.getCount();
				}
			}
		}
		return result;
	}

	private void setLanternState(Level level, BlockPos pos, boolean value)
	{
		if (level.getBlockState(pos).getBlock() instanceof AdvancedTableBottomPrimary block)
		{
			block.setLanternState(level, pos, value);
		}
	}

	////////////////////////////////////////

	private void storeAdjacentInventoriesInSlots()
	{
		int range = this.getInventoryAccessRange();
		this.access.execute((level, pos) -> this.inventoryAccessHelper.loadAdjacentInventories(level, pos, this.player, range));
		this.lastInventoryAccessRange = range;
		this.inventoryAccessHelper.putInventoriesIntoAContainerForTransferToClient(this.tabElements, TAB_SMUGGLING_CONTAINER_SIZE/2);
		this.tabElements.setChanged();
	}

	private int lastInventoryAccessRange = 0;
	private int lastLanternCount = 0;
	private final InventoryAccessHelper inventoryAccessHelper = new InventoryAccessHelper();

	public void changeTabTo(int index)
	{
		Optional<Boolean> haveContainer = this.access.evaluate( (level, pos) -> this.inventoryAccessHelper.tryInitializeInventoryAccess(level, this.player, index) );
		if (haveContainer.isPresent() && haveContainer.get())
		{
			this.chestSlots = this.inventoryAccessHelper.chosenContainer;
			this.setUpperContainerTrueSize(this.inventoryAccessHelper.chosenContainerTrueSize);
			for (int i = ACCESS_SLOT_START; i <= ACCESS_SLOT_END; i++)
			{
				this.getSlot(i).container = this.chestSlots;
			}
			this.initExcessSlotMap();
		}
		else
		{
			if (! (this.chestSlots instanceof DisabledContainer))
			{
				this.chestSlots = new DisabledContainer(54);
				this.setUpperContainerTrueSize(54);
				for (int i = ACCESS_SLOT_START; i <= ACCESS_SLOT_END; i++)
				{
					this.getSlot(i).container = this.chestSlots;
				}
			}
			this.clearExcessSlotMap();
		}
		this.selectedTab = index; //this only happens on server side. we need to separately set this value on client side. it is on client where i need it so this line is for academic purposes.
		this.DataSlots.resetDataSlotFlagForClientFlag(SimpleTableDataSlots.DATA_SLOT_TABS_NEED_UPDATE); // 1->0 because change in tab list invokes tab change call
		this.sendAllDataToRemote(); //todo!! do i need?
	}
	public int selectedTab = -1;


	public void renameChest(String newName)
	{
		//System.out.println("rename on server");
		if (this.player.experienceLevel == 0 && ! this.player.isCreative())
		{
			return;
		}
		if (newName.equals("")) { return; }
		if (this.inventoryAccessHelper.currentType.equals(InventoryAccessHelper.RecordTypes.BLOCK))
		{
			if (this.inventoryAccessHelper.chosenContainerForRename instanceof net.minecraft.world.level.block.entity.BaseContainerBlockEntity bcbe)
			{
				if (! bcbe.hasCustomName() || ! bcbe.getCustomName().getString().equals(newName))
				{
					bcbe.setCustomName(Component.literal(newName));
					player.giveExperienceLevels(-1);
				}
			}
		}
		else if (this.inventoryAccessHelper.currentType.equals(InventoryAccessHelper.RecordTypes.TOOLBELT))
		{
			ItemStack s = (ItemStack) TetraBeltSupport.findToolbelt(player);
			s.setHoverName(Component.literal(newName));
			player.giveExperienceLevels(-1);
		}
		else if (this.inventoryAccessHelper.currentType.equals(InventoryAccessHelper.RecordTypes.LEGGINGS) || this.inventoryAccessHelper.currentType.equals(InventoryAccessHelper.RecordTypes.CHESTSLOT) || this.inventoryAccessHelper.currentType.equals(InventoryAccessHelper.RecordTypes.BACKSLOT))
		{
			ItemStack s = InventoryAccessHelper.getItemFromNamedSlot(player, this.inventoryAccessHelper.currentType);
			s.setHoverName(Component.literal(newName));
			player.giveExperienceLevels(-1);
		}
	}

	protected SimpleTableDataSlots DataSlots = new SimpleTableDataSlots(this);

	///////////////////////// actual values in data slots //////////////

	private int getUpperContainerTrueSize()
	{
		return this.DataSlots.getSlotValue(SimpleTableDataSlots.DATA_SLOT_UPPER_CONTAINER_TRUE_SIZE);
	}
	private void setUpperContainerTrueSize(int value)
	{
		if (value != this.getUpperContainerTrueSize())
		{
			this.DataSlots.setSlotValue(SimpleTableDataSlots.DATA_SLOT_UPPER_CONTAINER_TRUE_SIZE, value);
			this.synchronizeDataSlotToRemote(SimpleTableDataSlots.DATA_SLOT_UPPER_CONTAINER_TRUE_SIZE, value);
		}
	}

	private boolean isSlotSpecificallyDisabled(int slotIndex)
	{
		return slotIndex < 27 && ((this.DataSlots.getSlotValue(SimpleTableDataSlots.DATA_SLOT_SLOTS_00_TO_26_EXCESS) & (1 << slotIndex)) != 0)
				|| slotIndex >= 27 && slotIndex < 54 && ((this.DataSlots.getSlotValue(SimpleTableDataSlots.DATA_SLOT_SLOTS_27_TO_53_EXCESS) & (1 << (slotIndex - 27))) != 0);
	}
	private void initExcessSlotMap()
	{
		this.initExcessSlotMapInternal(SimpleTableDataSlots.DATA_SLOT_SLOTS_00_TO_26_EXCESS, 0);
		this.initExcessSlotMapInternal(SimpleTableDataSlots.DATA_SLOT_SLOTS_27_TO_53_EXCESS, 27);
	}
	private void clearExcessSlotMap()
	{
		this.DataSlots.setSlotValue(SimpleTableDataSlots.DATA_SLOT_SLOTS_00_TO_26_EXCESS, 0);
		this.synchronizeDataSlotToRemote(SimpleTableDataSlots.DATA_SLOT_SLOTS_00_TO_26_EXCESS, 0);
		this.DataSlots.setSlotValue(SimpleTableDataSlots.DATA_SLOT_SLOTS_27_TO_53_EXCESS, 0);
		this.synchronizeDataSlotToRemote(SimpleTableDataSlots.DATA_SLOT_SLOTS_27_TO_53_EXCESS, 0);
	}
	private void initExcessSlotMapInternal(int slot, int offset)
	{
		if (this.inventoryAccessHelper.chosenContainer instanceof IExcessSlotManager esm)
		{
			int map = 0;
			int mask = 1;
			for (int k = 0; k < 27; k++)
			{
				if (esm.isSlotSpecificallyDisabled(k + offset))
				{
					map = map | mask;
				}
				mask = mask << 1;
			}
			this.DataSlots.setSlotValue(slot, map);
			this.synchronizeDataSlotToRemote(slot, map);
		}
	}

	//////////////////////// data slot support ////////////////////

	public void registerClientHandlerForDataSlot(int slotIndex, Consumer<Integer> event)
	{
		this.DataSlots.registerClientHandlerForDataSlot(slotIndex, event);
	}

	/////////////////////////////////////////////////////////////////

	public void updateAccessSlotsOnClient()	{
		if (this.initialLoading == false && this.showInventoryAccess() && this.chestSlots.getMaxStackSize() == DisabledContainer.MARKER_FOR_DISABLED)
		{
			if (this.chestSlots.getMaxStackSize() == DisabledContainer.MARKER_FOR_DISABLED)
			{
				//happens on client
				((DisabledContainer)this.chestSlots).disabled = false;
			}
			PacketSender.sendTabChangeToServer(0);
		}
		// and again, we hide/show access slots here (other direction)
		if (this.initialLoading == false && ! this.showInventoryAccess() && this.chestSlots.getMaxStackSize() != DisabledContainer.MARKER_FOR_DISABLED)
		{
			if (this.selectedTab != 0) { this.changeTabTo(0); }
			if (this.chestSlots instanceof DisabledContainer && this.chestSlots.getMaxStackSize() != DisabledContainer.MARKER_FOR_DISABLED)
			{
				//happens on client
				((DisabledContainer)this.chestSlots).disabled = true;
			}
			this.sendAllDataToRemote();
		}
		// range change - again this worked, but apparently we're "fixing" everything
		int range = this.getInventoryAccessRange();
		if	(range != this.lastInventoryAccessRange)
		{
			PacketSender.sendTabChangeToServer(0);
		}
		this.lastInventoryAccessRange = range; // separate value from one on server, but we'll use the same variable. client-copy is only used within this method, below this line.
	}

	/////////////////////////////////////////////////////////////////

	public static class CustomizationSlot extends Slot
	{
		private static final TagKey<Item> ChestTag = TagKey.create(Registries.ITEM, new ResourceLocation("forge:chests"));
		private static final TagKey<Item> LanternTag = TagKey.create(Registries.ITEM, new ResourceLocation(Constants.MODID, "lanterns"));
		private static final ResourceLocation EMPTY_SLOT_BG = new ResourceLocation(Constants.MODID, "gui/c_slot");


		public CustomizationSlot(Container p_39521_, int p_39522_, int p_39523_, int p_39524_)
		{
			super(p_39521_, p_39522_, p_39523_, p_39524_);
			this.setBackground(InventoryMenu.BLOCK_ATLAS, EMPTY_SLOT_BG);
			// to make own atlas, start with PaintingTextureManager. gave up during 1.19.4 porting. too much effort.
		}

		public boolean mayPlace(ItemStack itemStack)
		{
			return itemStack.is(ChestTag)
					|| itemStack.is(CustomizationSlot.getAccessItem())
					|| this.acceptsLanterns && itemStack.is(LanternTag);
		}

		public int getMaxStackSize(ItemStack itemStack)
		{
			return itemStack.is(LanternTag) ? 2 : 1;
		}

		private static Item accessItem = null;
		private static Item getAccessItem()
		{
			if (accessItem == null)
			{
				accessItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(OptionsHolder.COMMON.AccessCustomizationItem.get()));
				if (accessItem.equals(Items.AIR)) { accessItem = Items.NAME_TAG; }
			}
			return accessItem;
		}

		private boolean acceptsLanterns = false;
		public void setAcceptsLanterns(boolean value) { this.acceptsLanterns = value; }
	}

	//////////////////////////////////////////////////////////////

	public static class OptionallyDrawnSlot extends Slot
	{
		private final Supplier<Boolean> condition;

		public OptionallyDrawnSlot(Container p_40223_, int p_40224_, int p_40225_, int p_40226_, Supplier<Boolean> condition)
		{
			super(p_40223_, p_40224_, p_40225_, p_40226_);
			this.condition = condition;
		}

		public boolean shouldRender()
		{
			return this.condition != null && this.condition.get();
		}

		@Override
		public boolean isActive()
		{
			return this.shouldRender();
		}
	}

	////////////////////////////////////////////////////////////////

	private static class DisabledContainer extends SimpleContainer
	{
		private final static int MARKER_FOR_DISABLED = 707;
		private boolean disabled = true;
		@Override
		public int getMaxStackSize()
		{
			return this.disabled ? MARKER_FOR_DISABLED : super.getMaxStackSize()/*999*/;
		}

		@Override
		public boolean canPlaceItem(int p_18952_, ItemStack p_18953_)
		{
			return ! this.disabled && super.canPlaceItem(p_18952_, p_18953_);
		}

		public DisabledContainer(int size)
		{
			super(size);
		}
	}

	/////////////////////////////////////////////////////////////////////////

	public static class VariableSizeContainerSlot extends Slot
	{
		private Supplier<Integer> containerTrueSizeGetter = null;
		private Function<Integer, Boolean> excessSettingGetter = null;

		public VariableSizeContainerSlot(Container p_40223_, int p_40224_, int p_40225_, int p_40226_, Supplier<Integer> containerTrueSize, Function<Integer, Boolean> excessSetting)
		{
			super(p_40223_, p_40224_, p_40225_, p_40226_);
			this.containerTrueSizeGetter = containerTrueSize;
			this.excessSettingGetter = excessSetting;
		}

		@Override
		public boolean isActive()
		{
			return this.container.getMaxStackSize() != DisabledContainer.MARKER_FOR_DISABLED // whole container not disabled
					&& ! this.isExcessSlot();  // not beyond the limit of variable-size containers
		}

		@Override
		public boolean mayPlace(ItemStack itemStack)
		{
			return this.isActive() && this.container.canPlaceItem(this.getSlotIndex(), itemStack);
		}

		@Override
		public ItemStack getItem()
		{
			if (! this.isExcessSlot())
			{
				return super.getItem();
			}
			else
			{
				return  ItemStack.EMPTY;
			}
		}

		@Override
		public void set(ItemStack stack)
		{
			if (this.getSlotIndex() < this.getContainerTrueSize())
			{
				super.set(stack);
			}
		}

		public int getContainerTrueSize() { return this.containerTrueSizeGetter.get(); }

		public boolean isExcessSlot()
		{
			return this.getSlotIndex() >= this.getContainerTrueSize()
					|| (excessSettingGetter != null && excessSettingGetter.apply(this.getSlotIndex()));
		}
	}

	public static class VariableSizeContainerWrapper extends SimpleContainer implements IExcessSlotManager
	{
		private IExcessSlotManager excessManager = null;
		private final Container internal;

		public VariableSizeContainerWrapper(Container wrapped)
		{
			super(54);
			this.internal = wrapped;
			if (this.internal instanceof IExcessSlotManager esm)
			{
				this.excessManager = esm;
			}
		}

		@Override
		public boolean canPlaceItem(int slot, ItemStack itemStack) { return slot < internal.getContainerSize() && internal.canPlaceItem(slot, itemStack); }

		@Override
		public ItemStack getItem(int slot) { return slot < internal.getContainerSize() ? internal.getItem(slot) : ItemStack.EMPTY; }

		@Override
		public ItemStack removeItem(int slot, int count) { return slot < internal.getContainerSize() ? internal.removeItem(slot, count) : ItemStack.EMPTY; }

		@Override
		public ItemStack removeItemNoUpdate(int slot) {	return slot < internal.getContainerSize() ? internal.removeItemNoUpdate(slot) : ItemStack.EMPTY; }

		@Override
		public void setItem(int slot, ItemStack itemStack) { if (slot < internal.getContainerSize()) { internal.setItem(slot, itemStack); } }

		@Override
		public boolean isEmpty() { return internal.isEmpty(); }

		@Override
		public void setChanged() { internal.setChanged(); }

		@Override
		public int getMaxStackSize() { return internal.getMaxStackSize(); }

		@Override
		public boolean isSlotSpecificallyDisabled(int slotIndex)
		{
			return this.excessManager != null && this.excessManager.isSlotSpecificallyDisabled(slotIndex);
		}
	}

	//////////////////////////////////////////////////////////////////////////

	public static class VariableSizeItemStackHandlerWrapper extends SimpleContainer
	{
		private final IItemHandler internal;
		public VariableSizeItemStackHandlerWrapper(IItemHandler wrapped)
		{
			super(54);
			this.internal = wrapped;
		}

		@Override
		public boolean canPlaceItem(int slot, ItemStack itemStack) { return slot < internal.getSlots() && internal.isItemValid(slot, itemStack); }

		@Override
		public ItemStack getItem(int slot) { return slot < internal.getSlots() ? internal.getStackInSlot(slot) : ItemStack.EMPTY; }

		@Override
		public ItemStack removeItem(int slot, int count) { return slot < internal.getSlots() ? internal.extractItem(slot, count, false) : ItemStack.EMPTY; }

		@Override
		public ItemStack removeItemNoUpdate(int slot) {	return slot < internal.getSlots() ? internal.extractItem(slot, 9999, false) : ItemStack.EMPTY; }

		@Override
		public void setItem(int slot, ItemStack itemStack) { if (slot < internal.getSlots()) { if (internal instanceof IItemHandlerModifiable i2) i2.setStackInSlot(slot, itemStack); else internal.insertItem(slot, itemStack, false); } }

		@Override
		public boolean isEmpty()
		{
			for (int i = 0; i < internal.getSlots(); i++)
			{
				if (! internal.getStackInSlot(i).isEmpty())
				{
					return false;
				}
			}
			return true;
		}

		@Override
		public void setChanged() { }

		@Override
		public int getMaxStackSize() { return internal.getSlots() > 1 ? internal.getSlotLimit(1) : 64; }
	}
	/////////////////////////////////////////////////////////////////////////

	private class CustomizationListenerClient implements ContainerListener {
		private final SimpleTableMenu parent;
		public CustomizationListenerClient(SimpleTableMenu simpleTableMenu)
		{
			this.parent = simpleTableMenu;
		}

		@Override
		public void containerChanged(Container container)
		{
			// changing drawer state of block in world here causes duping (fixed onRemove, might work now)
			// anyway we need to hide/show access slots here
			this.parent.updateAccessSlotsOnClient();
		}
	}

	private class CustomizationListenerServer implements ContainerListener {
		private final SimpleTableMenu parent;
		public CustomizationListenerServer(SimpleTableMenu simpleTableMenu)
		{
			this.parent = simpleTableMenu;
		}
		@Override
		public void containerChanged(Container container)
		{
			// name tags
			int range = this.parent.getInventoryAccessRange();
			int lastRange = this.parent.lastInventoryAccessRange; // will be overwritten before i need it
			if (range != this.parent.lastInventoryAccessRange)
			{
				this.parent.storeAdjacentInventoriesInSlots();
				this.parent.DataSlots.resetDataSlotFlagForClientFlag(SimpleTableDataSlots.DATA_SLOT_TABS_NEED_UPDATE); // for some reason i managed to get it stuck on 1
				this.parent.DataSlots.raiseDataSlotFlagForClientFlag(SimpleTableDataSlots.DATA_SLOT_TABS_NEED_UPDATE);
				if (lastRange == 0)
				{
					this.parent.sendAllDataToRemote();
				}
				this.parent.lastInventoryAccessRange = range;
			}
			// lanterns
			int lanternCount = this.parent.getLanternCount();
			if (lanternCount == 2 && this.parent.lastLanternCount != 2)
			{
				this.parent.access.execute( (l, p) -> this.parent.setLanternState(l, p, true) );
				this.parent.lastLanternCount = lanternCount;
			}
			else if (lanternCount != 2 && this.parent.lastLanternCount == 2)
			{
				this.parent.access.execute( (l, p) -> this.parent.setLanternState(l, p, false) );
				this.parent.lastLanternCount = lanternCount;
			}
		}
	}
}
