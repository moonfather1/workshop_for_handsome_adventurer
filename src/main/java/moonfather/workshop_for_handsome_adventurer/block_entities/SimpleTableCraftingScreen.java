package moonfather.workshop_for_handsome_adventurer.block_entities;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import moonfather.workshop_for_handsome_adventurer.OptionsHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class SimpleTableCraftingScreen extends AbstractContainerScreen<SimpleTableMenu>
{
	private static final ResourceLocation[] CRAFTING_TABLE_LOCATION = new ResourceLocation[3];
	private static final TranslatableComponent TooltipCustomizationsRaw = new TranslatableComponent("message.workshop_for_handsome_adventurer.extension_slot1");
	private static List<Component> TooltipCustomizations = null;
	private final InventoryAccessComponent inventoryComponent = new InventoryAccessComponent();
	private boolean widthTooNarrow1;
	private ImageButton buttonBook, buttonChest;

	public SimpleTableCraftingScreen(SimpleTableMenu p_98448_, Inventory p_98449_, Component p_98450_) {
		super(p_98448_, p_98449_, p_98450_);
	}

	protected void init() {
		super.init();
		this.widthTooNarrow1 = this.width < 400;
		this.inventoryComponent.init(this, this.widthTooNarrow1);
		this.leftPos = this.inventoryComponent.preferredScreenPositionX(this.width, this.imageWidth);
		//this.buttonBook = this.addRenderableWidget(new ImageButton(this.leftPos + 148, this.height / 2 - 49+25, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (button) -> {
		//	this.recipeBookComponent.toggleVisibility();
		//	this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
		//	this.buttonBook.setPosition(this.leftPos + 148, this.height / 2 - 49+25);
		//	this.buttonChest.setPosition(this.leftPos + 148-22, this.height / 2 - 49+25);
		//}));
		this.addWidget(this.inventoryComponent);
		//this.setInitialFocus(this.recipeBookComponent);
		this.titleLabelX = 29-12;
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
		this.inventoryComponent.renderTooltip(poseStack, this.leftPos, this.topPos, p_98480_, p_98481_);
		this.renderCustomizationTooltips(poseStack, p_98480_, p_98481_);
	}

	private void renderCustomizationTooltips(PoseStack poseStack, int mouseX, int mouseY)
	{
		if (this.hoveredSlot != null && this.hoveredSlot.hasItem())
		{
			return; //only over empty slots
		}

		if (this.isHovering(152,17, 16, 16, mouseX, mouseY) && OptionsHolder.COMMON.SimpleTableNumberOfSlots.get() >= 1
			|| this.isHovering(152,39, 16, 16, mouseX, mouseY) && OptionsHolder.COMMON.SimpleTableNumberOfSlots.get() >= 2)
		{
			if (this.minecraft.screen != null)
			{
				if (TooltipCustomizations == null)
				{
					String itemKey = ForgeRegistries.ITEMS.getValue(new ResourceLocation(OptionsHolder.COMMON.AccessCustomizationItem.get())).getDescriptionId();
					String itemName = Language.getInstance().getOrDefault(itemKey);
					TooltipCustomizations = Arrays.stream(
								Language.getInstance().getOrDefault(TooltipCustomizationsRaw.getKey())
										.replace("[ITEM]", itemName)
										.split("\n"))
							.map(text -> ((Component) new TextComponent(text).withStyle(ChatFormatting.DARK_GRAY)))
							.toList();
				}
				this.minecraft.screen.renderTooltip(poseStack, TooltipCustomizations, Optional.empty(), mouseX, mouseY);
			}
		}
	}



	protected void renderBg(PoseStack p_98474_, float p_98475_, int p_98476_, int p_98477_) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		this.checkPaths();
		RenderSystem.setShaderTexture(0, CRAFTING_TABLE_LOCATION[OptionsHolder.COMMON.SimpleTableNumberOfSlots.get()]);
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(p_98474_, i, j, 0, 0, this.imageWidth, this.imageHeight);
	}

	private void checkPaths()
	{
		if (CRAFTING_TABLE_LOCATION[0] == null)
		{
			CRAFTING_TABLE_LOCATION[0] = new ResourceLocation("workshop_for_handsome_adventurer:textures/gui/gui_simple_table_0_slots.png");
			CRAFTING_TABLE_LOCATION[1] = new ResourceLocation("workshop_for_handsome_adventurer:textures/gui/gui_simple_table_1_slots.png");
			CRAFTING_TABLE_LOCATION[2] = new ResourceLocation("workshop_for_handsome_adventurer:textures/gui/gui_simple_table_2_slots.png");
		}
	}

	//protected boolean isHovering(int p_98462_, int p_98463_, int p_98464_, int p_98465_, double p_98466_, double p_98467_) {
	//	return (this.widthTooNarrow1 || !this.inventoryComponent.isVisible()) && super.isHovering(p_98462_, p_98463_, p_98464_, p_98465_, p_98466_, p_98467_);
	//}

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

	protected boolean hasClickedOutside(double p_98456_, double p_98457_, int p_98458_, int p_98459_, int p_98460_) {
		boolean flag = p_98456_ < (double)p_98458_ || p_98457_ < (double)p_98459_ || p_98456_ >= (double)(p_98458_ + this.imageWidth) || p_98457_ >= (double)(p_98459_ + this.imageHeight);
		return this.inventoryComponent.hasClickedOutside(p_98456_, p_98457_, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, p_98460_) && flag;
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

	public void setLeft(int newX) {
		this.leftPos = newX;
	}
}
