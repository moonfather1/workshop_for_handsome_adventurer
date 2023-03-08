package moonfather.workshop_for_handsome_adventurer.block_entities;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.OptionsHolder;
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
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;
import java.util.function.Supplier;

public class SimpleTableMenu extends AbstractContainerMenu
{
	public static final int CUST_CONTAINER_SIZE = 4;
	public static final int TAB_SMUGGLING_CONTAINER_SIZE = 32; // 16 tabs max
	public static final int TAB_SMUGGLING_SOFT_LIMIT = 8; // that much fits above dialog
	public static final int TEMP_CHEST_LIMIT = 27;//todo:rework
	public static final int LEFT_PANEL_WIDTH = 176;
	private static final TagKey<Item> ChestTag = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge:chests"));
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
	public static final int ACCESS27_SLOT_START = TABS_SLOT_END + 1;//82;
	public static final int ACCESS27_SLOT_END = ACCESS27_SLOT_START + TEMP_CHEST_LIMIT - 1;//108;
	public static final int ACCESS54_SLOT_START = ACCESS27_SLOT_END + 1;//109;
	public static final int ACCESS54_SLOT_END = ACCESS54_SLOT_START + TEMP_CHEST_LIMIT - 1;//135;
	private final CraftingContainer craftSlots = new CraftingContainer(this, 3, 3);
	private final ResultContainer resultSlots = new ResultContainer();
	private final SimpleContainer customizationSlots = new SimpleContainer(CUST_CONTAINER_SIZE);
	private final Container tabElements = new SimpleContainer(TAB_SMUGGLING_CONTAINER_SIZE); // magic to transfer to client
	private Container chestSlots, chestSlots2 = null;
	private final ContainerLevelAccess access;
	private final Player player;

	private boolean initialLoading = true;
	private boolean isValidAccessContainer = true;

	public SimpleTableMenu(int containerId, Inventory inventory) {
		this(containerId, inventory, ContainerLevelAccess.NULL);
	}

	public SimpleTableMenu(int containerId, Inventory inventory, ContainerLevelAccess levelAccess) {
		super(Registration.CRAFTING_SINGLE_MENU_TYPE.get(), containerId);
		this.access = levelAccess;
		this.player = inventory.player;
		//---crafting result slot---
		this.addSlot(new ResultSlot(inventory.player, this.craftSlots, this.resultSlots, 0, 124-12, 35));

		//---crafting grid slots---
		for (int ver = 0; ver < 3; ++ver)
		{
			for(int hor = 0; hor < 3; ++hor)
			{
				this.addSlot(new Slot(this.craftSlots, hor + ver * 3, 30-12 + hor * 18, 17 + ver * 18));
			}
		}

		//---player inventory slots---
		for (int ver = 0; ver < 3; ++ver)
		{
			for(int hor = 0; hor < 9; ++hor)
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
		this.customizationSlots.addListener(new CustomizationListener(this));
		((SimpleContainer)this.tabElements).addListener(new CustomizationListener(this));

		//---slots to sneak tab images to the client---
		for (int i = 0; i < this.tabElements.getContainerSize(); i++)
		{
			this.addSlot(new Slot(this.tabElements, i, 9009, 9009+i*30));
		}
		this.storeAdjacentInventoriesInSlots();

		//---slots for adjacent inventories---
		Optional<Boolean> haveContainer = Optional.of(false);
		if (this.showInventoryAccess())
		{
			haveContainer = this.access.evaluate((level, pos) -> this.inventoryAccessHelper.tryInitializeFirstInventoryAccess(level, this.player));
		}
		if (this.inventoryAccessHelper.chosenContainer != null)	{
			this.chestSlots = this.inventoryAccessHelper.chosenContainer;
			if (this.inventoryAccessHelper.addonContainer != null)	{ this.chestSlots2 = this.inventoryAccessHelper.addonContainer; } else { this.chestSlots2 = new DisabledContainer(27); }
		} else {
			this.chestSlots = new DisabledContainer(27);
			this.chestSlots2 = new DisabledContainer(27);
		}
		this.isValidAccessContainer = haveContainer.isPresent() && haveContainer.get();
		for (int ver = 0; ver < this.chestSlots.getContainerSize()/9; ++ver)
		{
			for (int hor = 0; hor < 9; ++hor)
			{
				this.addSlot(new OptionallyDrawnSlot2(this.chestSlots, ver*9+hor, 5 + hor * 18 - LEFT_PANEL_WIDTH, 30 + ver * 18));
			}
		}
		for (int ver = 0; ver < this.chestSlots.getContainerSize()/9; ++ver)
		{
			for (int hor = 0; hor < 9; ++hor)
			{
				this.addSlot(new OptionallyDrawnSlot2(this.chestSlots2, ver*9+hor, 5 + hor * 18 - LEFT_PANEL_WIDTH, 30 + (ver + 3) * 18));
			}
		}
		this.customizationSlots.setChanged();
		this.initialLoading = false;
	}


	//todo:override
	protected int getCustomizationSlotCount()	{ return OptionsHolder.COMMON.SimpleTableNumberOfSlots.get(); }

	public SimpleTableMenu(int containerId, Inventory inventory, FriendlyByteBuf friendlyByteBuf)
	{
		this(containerId, inventory, ContainerLevelAccess.NULL);
	}

	protected static void slotChangedCraftingGrid(AbstractContainerMenu p_150547_, Level p_150548_, Player p_150549_, CraftingContainer p_150550_, ResultContainer p_150551_) {
		if (!p_150548_.isClientSide) {
			ServerPlayer serverplayer = (ServerPlayer)p_150549_;
			ItemStack itemstack = ItemStack.EMPTY;
			Optional<CraftingRecipe> optional = p_150548_.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, p_150550_, p_150548_);
			if (optional.isPresent()) {
				CraftingRecipe craftingrecipe = optional.get();
				if (p_150551_.setRecipeUsed(p_150548_, serverplayer, craftingrecipe)) {
					itemstack = craftingrecipe.assemble(p_150550_);
				}
			}

			p_150551_.setItem(0, itemstack);
			p_150547_.setRemoteSlot(0, itemstack);
			serverplayer.connection.send(new ClientboundContainerSetSlotPacket(p_150547_.containerId, p_150547_.incrementStateId(), 0, itemstack));
		}
	}

	public void slotsChanged(Container container)
	{
		if (isCraftingGrid(container))
		{
			this.access.execute( (p_39386_, p_39387_) ->
			{
				slotChangedCraftingGrid(this, p_39386_, this.player, this.craftSlots, this.resultSlots);
			});
		}
		else
		{
			// what container now?
			// this doesn't trigger for SimpleContainer
		}
	}

	public void fillCraftSlotsStackedContents(StackedContents contents) {
		this.craftSlots.fillStackedContents(contents);
	}

	public void clearCraftingContent()
	{
		this.craftSlots.clearContent();
		this.resultSlots.clearContent();
	}

	public boolean recipeMatches(Recipe<? super CraftingContainer> recipe) {
		return recipe.matches(this.craftSlots, this.player.level);
	}

	public void removed(Player player) {
		super.removed(player);
		boolean hasDrawer = hasChestInCustomizationSlots();
		this.clearContainer(player, this.craftSlots);
		this.clearContainer(player, this.customizationSlots);
		this.access.execute((level, pos) -> updateDrawerInWorld(level, pos, hasDrawer));
	}

	public boolean stillValid(Player player)
	{
		return stillValidInstanceOf(this.access, player);
	}

	protected static boolean stillValidInstanceOf(ContainerLevelAccess access, Player player)
	{
		return access.evaluate((level, pos) ->
		{
			if (!(level.getBlockState(pos).getBlock() instanceof SimpleTable)) { return false; }
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



	protected boolean moveItemStackToOccupiedSlotsOnly(ItemStack itemStack, int startingSlot, int endingSlotPlus1, boolean reverse)
	{
		boolean result = false;
		int i = startingSlot;
		if (reverse) {
			i = endingSlotPlus1 - 1;
		}

		if (itemStack.isStackable()) {
			while(!itemStack.isEmpty()) {
				if (reverse) {
					if (i < startingSlot) {
						break;
					}
				} else if (i >= endingSlotPlus1) {
					break;
				}

				Slot slot = this.slots.get(i);
				if (reverse) {
					--i;
				} else {
					++i;
				} // don't use i anymore
				if (! slot.isActive()) { continue; }
				ItemStack itemstack = slot.getItem();
				if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(itemStack, itemstack)) {
					int j = itemstack.getCount() + itemStack.getCount();
					int maxSize = Math.min(slot.getMaxStackSize(), itemStack.getMaxStackSize());
					if (j <= maxSize) {
						itemStack.setCount(0);
						itemstack.setCount(j);
						slot.setChanged();
						result = true;
					} else if (itemstack.getCount() < maxSize) {
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



	public boolean canTakeItemForPickAll(ItemStack p_39381_, Slot slot) {
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
						BlockState state = level.getBlockState(pos);
						if (! (state.getBlock() instanceof SimpleTable))
						{
							super.clearContainer(player, container);
							return;
						}
						if (! this.hasChestInCustomizationSlots())
						{
							if (this.showInventoryAccess()) {
								this.clearContainerWithInventoryAccess(player, container); // will place to chests
							}
							super.clearContainer(player, container); // will place to player
							this.clearInWorld(level, pos);
							return;
						}
						//if (!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer) player).hasDisconnected()) {
						//	super.clearContainer(player, container); // will drop
						//	this.clearInWorld(level, pos);
						//	return;
						//}
						this.storeCraftingGridToWorld(container, level, pos);
					}
			);
		}
		else if (isCustomizationContainer(container))
		{
			this.access.execute(
					(level, pos) -> this.storeCustomizationsToWorld(container, level, pos)
			);
		}
		else
		{
			// what container now?
		}
	}



	protected void clearContainerWithInventoryAccess(Player player, Container container) {
		if (!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer)player).hasDisconnected()) {
			for(int j = 0; j < container.getContainerSize(); ++j) {
				player.drop(container.removeItemNoUpdate(j), false);
			}
		} else {
			for(int i = 0; i < container.getContainerSize(); ++i) {
				moveItemStackToOccupiedSlotsOnly(container.getItem(i), ACCESS27_SLOT_START, ACCESS27_SLOT_END+1, false);
			}
		}
	}



	private boolean isCraftingGrid(Container container)
	{
		return container.getContainerSize() == 3*3;
	}

	private boolean isCustomizationContainer(Container container)
	{
		return container.getContainerSize() == CUST_CONTAINER_SIZE;
	}

	private void storeCraftingGridToWorld(Container container, Level level, BlockPos pos)
	{
		SimpleTableBlockEntity be = (SimpleTableBlockEntity) level.getBlockEntity(pos);  assert be != null;
		for(int i = 0; i < container.getContainerSize(); i++)
		{
			be.DepositItem(i, container.removeItemNoUpdate(i));
		}
	}

	private void storeCustomizationsToWorld(Container container, Level level, BlockPos pos)
	{
		SimpleTableBlockEntity be = (SimpleTableBlockEntity) level.getBlockEntity(pos);
		if (be == null) return;
		for(int i = 0; i < container.getContainerSize(); i++)
		{
			be.DepositCustomizationItem(i, container.removeItemNoUpdate(i));
		}
	}

	private void loadFromWorld(Level level, BlockPos pos)
	{
		SimpleTableBlockEntity be = (SimpleTableBlockEntity) level.getBlockEntity(pos);  assert be != null;
		for(int i = 0; i < this.craftSlots.getContainerSize(); i++)
		{
			this.craftSlots.setItem(i, be.GetItem(i));
		}
		this.craftSlots.setChanged();
		for(int i = 0; i < this.customizationSlots.getContainerSize(); i++)
		{
			this.customizationSlots.setItem(i, be.GetCustomizationItem(i));
		}
	}

	private void clearInWorld(Level level, BlockPos pos)
	{
		BaseContainerBlockEntity be = (BaseContainerBlockEntity) level.getBlockEntity(pos);
		for(int i = 0; i < this.craftSlots.getContainerSize(); i++)
		{
			be.ClearItem(i);
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
			if (this.customizationSlots.getItem(i).is(ChestTag))
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

	private void storeAdjacentInventoriesInSlots()
	{
		this.access.execute((level, pos) -> this.inventoryAccessHelper.loadAdjacentInventories(level, pos, this.player));
		this.inventoryAccessHelper.putInventoriesIntoAContainerForTransferToClient(this.tabElements, TAB_SMUGGLING_CONTAINER_SIZE/2);
		this.tabElements.setChanged();
	}

	private final InventoryAccessHelper inventoryAccessHelper = new InventoryAccessHelper();

	public void changeTabTo(int index)
	{
		Optional<Boolean> haveContainer = this.access.evaluate( (level, pos) -> this.inventoryAccessHelper.tryInitializeAnotherInventoryAccess(level, this.player, index) );
		this.isValidAccessContainer = haveContainer.isPresent() && haveContainer.get();
		if (this.isValidAccessContainer)
		{
			this.chestSlots = this.inventoryAccessHelper.chosenContainer;
			for (int i = ACCESS27_SLOT_START; i <= ACCESS27_SLOT_END; i++) {
				this.getSlot(i).container = this.chestSlots;
			}
			if (this.inventoryAccessHelper.addonContainer != null)
			{
				this.chestSlots2 = this.inventoryAccessHelper.addonContainer;
				for (int i = ACCESS54_SLOT_START; i <= ACCESS54_SLOT_END; i++) {
					this.getSlot(i).container = this.chestSlots2;
				}
			}
		}
		else
		{
			if (! (this.chestSlots instanceof DisabledContainer)) {
				this.chestSlots = new DisabledContainer(27);
				for (int i = ACCESS27_SLOT_START; i <= ACCESS27_SLOT_END; i++) {
					this.getSlot(i).container = this.chestSlots;
				}
				if (! (this.chestSlots2 instanceof DisabledContainer)) {
					this.chestSlots2 = new DisabledContainer(27);
					for (int i = ACCESS54_SLOT_START; i <= ACCESS54_SLOT_END; i++) {
						this.getSlot(i).container = this.chestSlots2;
					}
				}
			}
		}
		this.selectedTab = index; //this only happens on server side. we need to separately set this value on client side. it is on client where i need it so this line is for academic purposes.
		this.sendAllDataToRemote();
		this.customizationSlots.setChanged(); // cheaty call to client to re-enable/hide bottom container
	}

	public int selectedTab = -1;

	////////////////////////////////////////////

	private class CustomizationSlot extends Slot
	{
		public static final ResourceLocation EMPTY_SLOT_BG = new ResourceLocation(Constants.MODID, "gui/c_slot");

		public CustomizationSlot(Container p_39521_, int p_39522_, int p_39523_, int p_39524_)
		{
			super(p_39521_, p_39522_, p_39523_, p_39524_);
			this.setBackground(InventoryMenu.BLOCK_ATLAS, EMPTY_SLOT_BG);
		}

		public boolean mayPlace(ItemStack itemStack)
		{
			return itemStack.is(ChestTag) || itemStack.is(CustomizationSlot.getAccessItem());
		}

		public int getMaxStackSize(ItemStack itemStack) {
			return 1;
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
		public boolean isActive() {
			return this.shouldRender();
		}
	}

	//////////////////////////////////////////////////////////////

	public static class OptionallyDrawnSlot2 extends Slot
	{
		public OptionallyDrawnSlot2(Container p_40223_, int p_40224_, int p_40225_, int p_40226_)
		{
			super(p_40223_, p_40224_, p_40225_, p_40226_);
		}

		@Override
		public boolean isActive() {
			return this.container.getMaxStackSize() != DisabledContainer.MARKER_FOR_DISABLED;
		}
	}

	////////////////////////////////////////////////////////////////

	private static class DisabledContainer extends SimpleContainer
	{
		private final static int MARKER_FOR_DISABLED = 707;
		private boolean disabled = true;
		@Override
		public int getMaxStackSize() {
			return this.disabled ? MARKER_FOR_DISABLED : super.getMaxStackSize();
		}

		@Override
		public boolean canPlaceItem(int p_18952_, ItemStack p_18953_) {
			return !this.disabled && super.canPlaceItem(p_18952_, p_18953_);
		}

		public DisabledContainer(int size) {
			super(size);
		}
	}

	/////////////////////////////////////////////////////////////////////////

	private class CustomizationListener implements ContainerListener {
		private final SimpleTableMenu parent;
		public CustomizationListener(SimpleTableMenu simpleTableMenu) {
			this.parent = simpleTableMenu;
		}

		@Override
		public void containerChanged(Container container) {
			// changing drawer state of block in world here causes duping (fixed onRemove, might work now)
			// anyway we need to hide/show access slots here
			if (this.parent.initialLoading == false && this.parent.showInventoryAccess() && this.parent.chestSlots.getMaxStackSize() == DisabledContainer.MARKER_FOR_DISABLED) {
				if (this.parent.selectedTab != 0) { this.parent.changeTabTo(0); }
				if (this.parent.chestSlots.getMaxStackSize() == DisabledContainer.MARKER_FOR_DISABLED)
				{
					//happens on client
					((DisabledContainer)this.parent.chestSlots).disabled = false;
					if (! this.parent.tabElements.getItem(0).isEmpty() && this.parent.tabElements.getItem(0*2).getCount() == 2) {
						((DisabledContainer) this.parent.chestSlots2).disabled = false;
					}
				}
				this.parent.sendAllDataToRemote();
			}
			// and again, we hide/show access slots here (other direction)
			if (this.parent.initialLoading == false && ! this.parent.showInventoryAccess() && this.parent.chestSlots.getMaxStackSize() != DisabledContainer.MARKER_FOR_DISABLED) {
				if (this.parent.selectedTab != 0) { this.parent.changeTabTo(0); }
				if (this.parent.chestSlots instanceof DisabledContainer && this.parent.chestSlots.getMaxStackSize() != DisabledContainer.MARKER_FOR_DISABLED)
				{
					//happens on client
					((DisabledContainer)this.parent.chestSlots).disabled = true;
					((DisabledContainer)this.parent.chestSlots2).disabled = true;
				}
				this.parent.sendAllDataToRemote();
			}
			// more work to do: we need to enable/disable lower part of double chest here
			if (this.parent.initialLoading == false && this.parent.showInventoryAccess() && this.parent.chestSlots2 instanceof DisabledContainer) { // 3rd condition means client
				if (this.parent.chestSlots.getMaxStackSize() != DisabledContainer.MARKER_FOR_DISABLED) {
					((DisabledContainer) this.parent.chestSlots2).disabled = this.parent.tabElements.getItem(this.parent.selectedTab*2).isEmpty() || this.parent.tabElements.getItem(this.parent.selectedTab*2).getCount() != 2;
				}
				else {
					((DisabledContainer) this.parent.chestSlots2).disabled = true;
				}
			}
		}
	}
}
