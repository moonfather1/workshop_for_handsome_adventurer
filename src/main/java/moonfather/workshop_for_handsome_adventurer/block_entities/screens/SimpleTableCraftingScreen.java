package moonfather.workshop_for_handsome_adventurer.block_entities.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.OptionsHolder;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

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

	public SimpleTableCraftingScreen(SimpleTableMenu p_98448_, Inventory p_98449_, Component p_98450_) {
		super(p_98448_, p_98449_, p_98450_);
	}

	protected void init() {
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

		// super.render() calls renderSlot() only for active slot. we want to draw X over inactive slots
		if (this.inventoryComponent.isVisibleTotal()) {
			for (int k = SimpleTableMenu.ACCESS_SLOT_START; k <= SimpleTableMenu.ACCESS_SLOT_END; k++) {
				if (this.menu.slots.get(k) instanceof SimpleTableMenu.VariableSizeContainerSlot slot) {
					if (!slot.isActive() && slot.isExcessSlot() && slot.x >= 0) {
						if (slot.getSlotIndex() < 27 || this.inventoryComponent.areSlotRowsFourToSixVisible()) {
							RenderSystem.setShader(GameRenderer::getPositionTexShader);
							poseStack.translate(0f, 0f, 100f);
							if (this.excessSlotSprite == null) {
								this.excessSlotSprite = this.minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(EXCESS_SLOT_BG);
							}
							RenderSystem.setShaderTexture(0, this.excessSlotSprite.atlasLocation());
							blit(poseStack, this.leftPos + slot.x, this.topPos + slot.y, 0/*z?*/, 16, 16, this.excessSlotSprite);
							poseStack.translate(0f, 0f, -100f);
						}
					}
				}
			}
		}

		// tooltips at the end so that they wouldn't be obstructed by X-es
		this.renderTooltip(poseStack, p_98480_, p_98481_);
		this.inventoryComponent.renderTooltip(poseStack, p_98480_, p_98481_);
		this.renderCustomizationTooltips(poseStack, p_98480_, p_98481_);
	}
	private TextureAtlasSprite excessSlotSprite = null;
	private static final ResourceLocation EXCESS_SLOT_BG = new ResourceLocation(Constants.MODID, "gui/x_slot");



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
								.forEach(text -> tooltipCustomizationsFull.add(Component.literal(text).withStyle(ChatFormatting.DARK_GRAY)));
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

	private final Component tooltipCustomizationsTitle = Component.translatable("message.workshop_for_handsome_adventurer.extension_slotT").withStyle(Style.EMPTY.withColor(0xaa77dd));
	private final Component tooltipCustomizationsShift = Component.translatable("message.workshop_for_handsome_adventurer.extension_slotS").withStyle(ChatFormatting.DARK_GRAY);
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

	public boolean keyPressed(int p_97878_, int p_97879_, int p_97880_) {
		if (p_97878_ == KEY_ESCAPE) {
			this.minecraft.player.closeContainer(); // esc
		}
		if (this.inventoryComponent.isVisibleTotal()
				&& this.inventoryComponent.keyPressed(p_97878_, p_97879_, p_97880_)) {
			return true;
		}
		return super.keyPressed(p_97878_, p_97879_, p_97880_);
	}

	public int getImageWidth()
	{
		return this.imageWidth;
	}
}
