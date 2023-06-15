package moonfather.workshop_for_handsome_adventurer.block_entities.screens;

import moonfather.workshop_for_handsome_adventurer.OptionsHolder;
import moonfather.workshop_for_handsome_adventurer.block_entities.DualTableMenu;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import moonfather.workshop_for_handsome_adventurer.block_entities.messaging.PacketSender;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;

@OnlyIn(Dist.CLIENT)
public class DualTableCraftingScreen extends SimpleTableCraftingScreen
{
	private static final ResourceLocation[] BACKGROUND_LOCATION = new ResourceLocation[5];

	public DualTableCraftingScreen(SimpleTableMenu p_98448_, Inventory p_98449_, Component p_98450_) {
		super(p_98448_, p_98449_, p_98450_);
		this.imageHeight = 233;
	}


	@Override
	protected String getCustomizationTooltipPath() {
		return "message.workshop_for_handsome_adventurer.extension_slot2";
	}



	@Override
	protected ResourceLocation getBackgroundImage()
	{
		if (BACKGROUND_LOCATION[0] == null)
		{
			for (int i = 0; i <= 4; i++) {
				BACKGROUND_LOCATION[i] = new ResourceLocation(("workshop_for_handsome_adventurer:textures/gui/gui_dual_table_%d_slots.png".formatted(i)));
			}
		}
		return BACKGROUND_LOCATION[OptionsHolder.COMMON.DualTableNumberOfSlots.get()];
	}



	@Override
	protected void renderLabels(GuiGraphics graphics, int p_97809_, int p_97810_) {
		graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
		graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY + 3*18+13, 4210752, false);
		graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY + 3*18+13, 4210752, false);
	}

	//////jei////////
	private StateSwitchingButton jeiButton = null;
	private int lastDestinationGrid = 1;
	@Override
	protected void init() {
		super.init();
		((DualTableMenu) this.menu).registerClientHandlerForRecipeTargetChange(this::recipeTargetButtonChangeHandler);
		this.createJeiButton();
	}

	private void recipeTargetButtonChangeHandler(Integer value) {
		lastDestinationGrid = value;
		if (this.jeiButton != null) {
			this.jeiButton.setStateTriggered(value == 2);
		}
	}

	@Override
	public void setPositionsX() {
		super.setPositionsX();
		if (this.jeiButton != null) {
			this.jeiButton.setPosition(DestinationPickerButton.JEI_BUTTON_RENDERX + this.renderLeftPos, DestinationPickerButton.JEI_BUTTON_RENDERY + this.topPos);
		}
	}

	private void createJeiButton() {
		if (ModList.get().isLoaded("jei")) {
			this.getBackgroundImage();
			this.jeiButton = new DestinationPickerButton(0, 0, DestinationPickerButton.JEI_BUTTON_WIDTH, DestinationPickerButton.JEI_BUTTON_HEIGTH, false);  // last par is initial state
			this.jeiButton.initTextureValues(DestinationPickerButton.JEI_BUTTON_POSX, DestinationPickerButton.JEI_BUTTON_POSY, DestinationPickerButton.JEI_BUTTON_WIDTH + DestinationPickerButton.JEI_BUTTON_MARGIN, DestinationPickerButton.JEI_BUTTON_HEIGTH + DestinationPickerButton.JEI_BUTTON_MARGIN, BACKGROUND_LOCATION[0]);
			this.jeiButton.setPosition(DestinationPickerButton.JEI_BUTTON_RENDERX + this.renderLeftPos, DestinationPickerButton.JEI_BUTTON_RENDERY + this.topPos);
			this.jeiButton.setStateTriggered(((DualTableMenu) this.menu).getRecipeTargetGrid() == 2);
			this.addRenderableWidget(this.jeiButton);
		}
	}



	private class DestinationPickerButton extends StateSwitchingButton
	{
		private static final int JEI_BUTTON_WIDTH = 40, JEI_BUTTON_HEIGTH = 28, JEI_BUTTON_POSX = 176, JEI_BUTTON_POSY = 0, JEI_BUTTON_MARGIN = 0, JEI_BUTTON_RENDERX = 80, JEI_BUTTON_RENDERY = 62;

		public DestinationPickerButton(int p_94615_, int p_94616_, int p_94617_, int p_94618_, boolean p_94619_) {
			super(p_94615_, p_94616_, p_94617_, p_94618_, p_94619_);
		}


		@Override
		public boolean mouseClicked(double x, double y, int p_93643_) {
			double localX = x - this.getX();
			double localY = y - this.getY();
			if (localX >= 10 && localX <= 20 && localY >= 4 && localY <= 14) {
				if (lastDestinationGrid != 2)
				{
					PacketSender.sendDestinationGridChangeToServer(2);
					lastDestinationGrid = 2;
					this.setStateTriggered(true);
				}
				return true;
			}
			else if (localX >= 10 && localX <= 20 && localY >= 14 && localY <= 24) {
				if (lastDestinationGrid != 1)
				{
					PacketSender.sendDestinationGridChangeToServer(1);
					lastDestinationGrid = 1;
					this.setStateTriggered(false);
				}
				return true;
			}
			return super.mouseClicked(x, y, p_93643_);
		}

		@Override
		public boolean isFocused() {
			return false;
		}
	}
}
