package moonfather.workshop_for_handsome_adventurer.block_entities;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import moonfather.workshop_for_handsome_adventurer.OptionsHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class SimpleTableCraftingScreen extends AbstractContainerScreen<SimpleTableMenu>
{
	private static final ResourceLocation[] CRAFTING_TABLE_LOCATION = new ResourceLocation[3];
	private List<Component> tooltipCustomizationsFull = null, tooltipCustomizationsBrief = null;
	private final InventoryAccessComponent inventoryComponent = new InventoryAccessComponent();
	//private ImageButton buttonBook, buttonChest;
	protected int renderLeftPos;

	public SimpleTableCraftingScreen(SimpleTableMenu p_98448_, Inventory p_98449_, Component p_98450_) {
		super(p_98448_, p_98449_, p_98450_);
	}

	protected void init() {
		super.init();
		this.inventoryComponent.init(this, this.width < 400);
		this.setPositionsX();

		//this.buttonBook = this.addRenderableWidget(new ImageButton(this.leftPos + 148, this.height / 2 - 49+25, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (button) -> {
		//	this.recipeBookComponent.toggleVisibility();
		//	this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
		//	this.buttonBook.setPosition(this.leftPos + 148, this.height / 2 - 49+25);
		//	this.buttonChest.setPosition(this.leftPos + 148-22, this.height / 2 - 49+25);
		//}));
		this.addWidget(this.inventoryComponent);
		//this.setInitialFocus(this.recipeBookComponent);
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
	public int getXSize() {
		int leftPanel = this.inventoryComponent.getWidth();
		return imageWidth + leftPanel + (leftPanel > 0 ? 2 : 0);
	}

	public void containerTick()
	{
		super.containerTick();
		this.inventoryComponent.tick();
	}

	public void render(PoseStack poseStack, int p_98480_, int p_98481_, float p_98482_) {
		this.renderBackground(poseStack);
		if (this.inventoryComponent.isVisibleTotal())
		{
			this.inventoryComponent.render(poseStack, p_98480_, p_98481_, p_98482_);
		}
		super.render(poseStack, p_98480_, p_98481_, p_98482_);

		this.renderTooltip(poseStack, p_98480_, p_98481_);
		this.inventoryComponent.renderTooltip(poseStack, p_98480_, p_98481_);
		this.renderCustomizationTooltips(poseStack, p_98480_, p_98481_);
	}

	@Override
	public void renderSlot(PoseStack poseStack, Slot slot)
	{
		if (! this.inventoryComponent.isVisibleTotal() && slot.x < 0) // see comment below
		{
			return;
		}
		super.renderSlot(poseStack, slot);
	}

	@Override
	public boolean isHovering(Slot slot, double x, double y) {
		if (! this.inventoryComponent.isVisibleTotal() && /*slot instanceof SimpleTableMenu.OptionallyDrawnSlot2*/ slot.x < 0)
		{
			return false; //leaving 0 above even though we move slots. tried  this.renderLeftPos briefly.
		}
		return this.isHovering(slot.x, slot.y, 16, 16, x, y);
	}

	private void renderCustomizationTooltips(PoseStack poseStack, int mouseX, int mouseY)
	{
		if (this.hoveredSlot == null || this.hoveredSlot.hasItem())
		{
			return; //only over empty slots
		}
		if (this.hoveredSlot instanceof SimpleTableMenu.CustomizationSlot && this.hoveredSlot.getSlotIndex() < this.getMenu().getCustomizationSlotCount())
		{
			if (this.minecraft.screen != null)
			{
				if (hasShiftDown()) {
					if (tooltipCustomizationsFull == null) {
						String itemKey = ForgeRegistries.ITEMS.getValue(new ResourceLocation(OptionsHolder.COMMON.AccessCustomizationItem.get())).getDescriptionId();
						String itemName = Language.getInstance().getOrDefault(itemKey);
						tooltipCustomizationsFull = new ArrayList<>(15);
						tooltipCustomizationsFull.add(tooltipCustomizationsTitle);
						Arrays.stream(Language.getInstance().getOrDefault(this.getCustomizationTooltipPath())
												.replace("[ITEM]", itemName)
												.split("\n"))
								.forEach(text -> tooltipCustomizationsFull.add(new TextComponent(text).withStyle(ChatFormatting.DARK_GRAY)));
					}
					this.minecraft.screen.renderTooltip(poseStack, tooltipCustomizationsFull, Optional.empty(), mouseX, mouseY);
				}
				else {
					if (tooltipCustomizationsBrief == null) {
						tooltipCustomizationsBrief = new ArrayList<>(2);
						tooltipCustomizationsBrief.add(tooltipCustomizationsTitle);
						tooltipCustomizationsBrief.add(tooltipCustomizationsShift);
					}
					this.minecraft.screen.renderTooltip(poseStack, tooltipCustomizationsBrief, Optional.empty(), mouseX, mouseY);
				}
			}
		}
	}

	private final Component tooltipCustomizationsTitle = new TranslatableComponent("message.workshop_for_handsome_adventurer.extension_slotT").withStyle(Style.EMPTY.withColor(0xaa77dd));
	private final Component tooltipCustomizationsShift = new TranslatableComponent("message.workshop_for_handsome_adventurer.extension_slotS").withStyle(ChatFormatting.DARK_GRAY);
	protected String getCustomizationTooltipPath() {
		return "message.workshop_for_handsome_adventurer.extension_slot1";
	}



	protected void renderBg(PoseStack p_98474_, float p_98475_, int p_98476_, int p_98477_) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, this.getBackgroundImage());
		int i = this.renderLeftPos;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(p_98474_, i, j, 0, 0, this.imageWidth, this.imageHeight);
	}

	protected ResourceLocation getBackgroundImage()
	{
		if (CRAFTING_TABLE_LOCATION[0] == null)
		{
			CRAFTING_TABLE_LOCATION[0] = new ResourceLocation("workshop_for_handsome_adventurer:textures/gui/gui_simple_table_0_slots.png");
			CRAFTING_TABLE_LOCATION[1] = new ResourceLocation("workshop_for_handsome_adventurer:textures/gui/gui_simple_table_1_slots.png");
			CRAFTING_TABLE_LOCATION[2] = new ResourceLocation("workshop_for_handsome_adventurer:textures/gui/gui_simple_table_2_slots.png");
		}
		return CRAFTING_TABLE_LOCATION[OptionsHolder.COMMON.SimpleTableNumberOfSlots.get()];
	}



	public boolean mouseClicked(double p_98452_, double p_98453_, int p_98454_) {
		//System.out.println("~~~mouseclii  " + p_98452_ + "   " + p_98453_ + "    " + p_98454_);ppp
		//if (this.inventoryComponent.mouseClicked(p_98452_, p_98453_, p_98454_)) {
		//	this.setFocused(this.inventoryComponent);
		//	return true;
		//} else {
		//	return this.widthTooNarrow && this.inventoryComponent.isVisible() ? true : super.mouseClicked(p_98452_, p_98453_, p_98454_);
		//}
		return super.mouseClicked(p_98452_, p_98453_, p_98454_);
	}

	protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
		boolean flag = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.imageWidth) || mouseY >= (double)(top + this.imageHeight);
		return this.inventoryComponent.hasClickedOutside(mouseX, mouseY, this.leftPos, this.topPos, this.imageWidth + this.inventoryComponent.getWidth() + 1, this.imageHeight, button) && flag;
	}

	protected void slotClicked(Slot p_98469_, int p_98470_, int p_98471_, ClickType p_98472_) {
		super.slotClicked(p_98469_, p_98470_, p_98471_, p_98472_);
		this.inventoryComponent.slotClicked(p_98469_);
	}

	public void removed() {
		this.inventoryComponent.removed();
		super.removed();
	}

	public int getImageWidth()
	{
		return this.imageWidth;
	}
}
