package moonfather.workshop_for_handsome_adventurer.block_entities.screens;

import moonfather.workshop_for_handsome_adventurer.CommonConfig;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.mojang.blaze3d.platform.InputConstants.KEY_ESCAPE;

@OnlyIn(Dist.CLIENT)
public class SimpleTableCraftingScreen extends AbstractContainerScreen<SimpleTableMenu>
{
	private static final ResourceLocation[] CRAFTING_TABLE_LOCATION = new ResourceLocation[3];
	private List<Component> tooltipCustomizationsFull = null, tooltipCustomizationsBrief = null;
	private final InventoryAccessComponent inventoryComponent = new InventoryAccessComponent();
	protected int renderLeftPos;

	public SimpleTableCraftingScreen(SimpleTableMenu p_98448_, Inventory p_98449_, Component p_98450_)
	{
		super(p_98448_, p_98449_, p_98450_);
	}

	protected void init()
	{
		super.init();
		this.inventoryComponent.init(this, this.width < 400);
		this.setPositionsX();
		this.addWidget(this.inventoryComponent);
	}

	public void setPositionsX()
	{
		int leftPanel = this.inventoryComponent.getWidth();
		this.leftPos = (this.width - this.imageWidth - leftPanel) / 2;
		this.renderLeftPos = this.leftPos + leftPanel + (leftPanel > 0 ? 2 : 0);
		this.titleLabelX = 17 + this.renderLeftPos - this.leftPos;
		this.inventoryLabelX = 8 + this.renderLeftPos - this.leftPos;
	}

	@Override
	public int getXSize()
	{
		int leftPanel = this.inventoryComponent.getWidth();
		return imageWidth + leftPanel + (leftPanel > 0 ? 2 : 0);
	}

	public void containerTick()
	{
		super.containerTick();
		this.inventoryComponent.tick();
	}

	@Override
	public void render(GuiGraphics graphics, int p_98480_, int p_98481_, float p_98482_)
	{
		super.render(graphics, p_98480_, p_98481_, p_98482_);

		// super.render() calls renderSlot() only for active slot. we want to draw X over inactive slots
		if (this.inventoryComponent.isVisibleTotal())
		{
			for (int k = SimpleTableMenu.ACCESS_SLOT_START; k <= SimpleTableMenu.ACCESS_SLOT_END; k++)
			{
				if (this.menu.slots.get(k) instanceof SimpleTableMenu.VariableSizeContainerSlot slot)
				{
					if (! slot.isActive() && slot.isExcessSlot() && slot.x >= 0)
					{
						if (slot.getSlotIndex() < 27 || this.inventoryComponent.areSlotRowsFourToSixVisible())
						{
//							graphics.pose().translate(0f, 0f, 101f);
							if (this.excessSlotSprite == null)
							{
								this.excessSlotSprite = this.minecraft.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(EXCESS_SLOT_BG);
							}
//							graphics.blit(this.excessSlotSprite, this.leftPos + slot.x, this.topPos + slot.y, 0/*z?*/, 16, 16);
//							graphics.pose().translate(0f, 0f, -101f);
						}
					}
				}
			}
		}

		// tooltips at the end so that they wouldn't be obstructed by X-es
		this.renderTooltip(graphics, p_98480_, p_98481_);
		this.inventoryComponent.renderTooltip(graphics, p_98480_, p_98481_);
		this.renderCustomizationTooltips(graphics, p_98480_, p_98481_);
	}
	private TextureAtlasSprite excessSlotSprite = null;
	private static final ResourceLocation EXCESS_SLOT_BG = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "gui/x_slot");

	@Override
	protected void renderBg(GuiGraphics graphics, float p_98475_, int p_98476_, int p_98477_)
	{
		int i = this.renderLeftPos;
		int j = (this.height - this.imageHeight) / 2;
		graphics.blit(this.getBackgroundImage(), i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
	}

	@Override
	public void renderBackground(GuiGraphics graphics, int p_297538_, int p_300104_, float p_298759_)
	{
		super.renderBackground(graphics, p_297538_, p_300104_, p_298759_); // renders gray shading in the back, than calls renderBg
		if (this.inventoryComponent.isVisibleTotal())
		{
			this.inventoryComponent.render(graphics, p_297538_, p_300104_, p_298759_);
		}
	}

	/// this is to make small numbers for item stacks with 100+ and 1000+ pieces.
	@Override
	protected void renderSlotContents(GuiGraphics guiGraphics, ItemStack itemstack, Slot slot, @Nullable String countString) {
		int x = slot.x;
		int y = slot.y;
		int index = slot.x + slot.y * this.imageWidth;
		if (slot.isFake()) {
			guiGraphics.renderFakeItem(itemstack, x, y, index);
		} else {
			guiGraphics.renderItem(itemstack, x, y, index);
		}
		float scale = 1.0f, dx = 0f, dy = 0f;
		if (itemstack.getCount() > 99) { scale = 0.75f; dx = 4; dy = 5; }
		if (itemstack.getCount() > 999) { scale = 0.50f; dx = 14f; dy = 16f; }
		guiGraphics.pose().pushMatrix();
		guiGraphics.pose().scale(scale, scale);
		guiGraphics.pose().translate(dx, dy);
		x = (int) (x * (1/scale));
		y = (int) (y * (1/scale));
		guiGraphics.renderItemDecorations(this.font, itemstack, x, y, countString);
		guiGraphics.pose().popMatrix();
	}



	private void renderCustomizationTooltips(GuiGraphics graphics, int mouseX, int mouseY)
	{
		if (this.hoveredSlot == null || this.hoveredSlot.hasItem())
		{
			return; //only over empty slots
		}
		if (this.hoveredSlot instanceof SimpleTableMenu.CustomizationSlot && this.hoveredSlot.getSlotIndex() < this.getMenu().getCustomizationSlotCount())
		{
			if (this.minecraft.screen != null)
			{
				if (hasShiftDown())
				{
					if (tooltipCustomizationsFull == null)
					{
						String itemKey = "??";
						Optional<Holder.Reference<Item>> stupidWrapper = BuiltInRegistries.ITEM.get(ResourceLocation.parse(CommonConfig.AccessCustomizationItem.get()));
						if (! stupidWrapper.isEmpty()) { itemKey = stupidWrapper.get().value().getDescriptionId(); }
						String itemName = Language.getInstance().getOrDefault(itemKey);
						tooltipCustomizationsFull = new ArrayList<>(15);
						tooltipCustomizationsFull.add(tooltipCustomizationsTitle);
						Arrays.stream(Language.getInstance().getOrDefault(this.getCustomizationTooltipPath())
												.replace("[ITEM]", itemName)
												.split("\n"))
								.forEach(text -> tooltipCustomizationsFull.add(Component.literal(text).withStyle(ChatFormatting.DARK_GRAY)));
					}
					graphics.setComponentTooltipForNextFrame(this.font, tooltipCustomizationsFull, mouseX, mouseY);
				}
				else
				{
					if (tooltipCustomizationsBrief == null)
					{
						tooltipCustomizationsBrief = new ArrayList<>(2);
						tooltipCustomizationsBrief.add(tooltipCustomizationsTitle);
						tooltipCustomizationsBrief.add(tooltipCustomizationsShift);
					}
					graphics.setComponentTooltipForNextFrame(this.font, tooltipCustomizationsBrief, mouseX, mouseY);
				}
			}
		}
	}

	private final Component tooltipCustomizationsTitle = Component.translatable("message.workshop_for_handsome_adventurer.extension_slotT").withStyle(Style.EMPTY.withColor(0xaa77dd));
	private final Component tooltipCustomizationsShift = Component.translatable("message.workshop_for_handsome_adventurer.extension_slotS").withStyle(ChatFormatting.DARK_GRAY);
	protected String getCustomizationTooltipPath() {
		return "message.workshop_for_handsome_adventurer.extension_slot1";
	}



	protected ResourceLocation getBackgroundImage()
	{
		if (CRAFTING_TABLE_LOCATION[0] == null)
		{
			CRAFTING_TABLE_LOCATION[0] = ResourceLocation.parse("workshop_for_handsome_adventurer:textures/gui/gui_simple_table_0_slots.png");
			CRAFTING_TABLE_LOCATION[1] = ResourceLocation.parse("workshop_for_handsome_adventurer:textures/gui/gui_simple_table_1_slots.png");
			CRAFTING_TABLE_LOCATION[2] = ResourceLocation.parse("workshop_for_handsome_adventurer:textures/gui/gui_simple_table_2_slots.png");
		}
		if (this.backgroundImageLocation == null)
		{
			this.backgroundImageLocation = CRAFTING_TABLE_LOCATION[CommonConfig.SimpleTableNumberOfSlots.get()];
		}
		return this.backgroundImageLocation;
	}
	protected ResourceLocation backgroundImageLocation = null;



	protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button)
	{
		boolean flag = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.imageWidth) || mouseY >= (double)(top + this.imageHeight);
		return this.inventoryComponent.hasClickedOutside(mouseX, mouseY, this.leftPos, this.topPos, this.imageWidth + this.inventoryComponent.getWidth() + 1, this.imageHeight, button) && flag;
	}

	protected void slotClicked(Slot p_98469_, int p_98470_, int p_98471_, ClickType p_98472_)
	{
		super.slotClicked(p_98469_, p_98470_, p_98471_, p_98472_);
		this.inventoryComponent.slotClicked(p_98469_);
	}

	public void removed()
	{
		this.inventoryComponent.removed();
		super.removed();
	}

	public boolean keyPressed(int p_97878_, int p_97879_, int p_97880_)
	{
		if (p_97878_ == KEY_ESCAPE)
		{
			this.minecraft.player.closeContainer(); // esc
		}
		if (this.inventoryComponent.isVisibleTotal()
				&& this.inventoryComponent.keyPressed(p_97878_, p_97879_, p_97880_))
		{
			return true;
		}
		return super.keyPressed(p_97878_, p_97879_, p_97880_);
	}

	public int getImageWidth()
	{
		return this.imageWidth;
	}

	public Font getFont() { return this.font; }
}
