package moonfather.workshop_for_handsome_adventurer.block_entities;

import com.mojang.blaze3d.vertex.PoseStack;
import moonfather.workshop_for_handsome_adventurer.OptionsHolder;
import moonfather.workshop_for_handsome_adventurer.block_entities.messaging.PacketSender;
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
	protected void renderLabels(PoseStack p_97808_, int p_97809_, int p_97810_) {
		this.font.draw(p_97808_, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
		this.font.draw(p_97808_, this.title, (float)this.titleLabelX, (float)this.titleLabelY + 3*18+13, 4210752);
		this.font.draw(p_97808_, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY + 3*18+13, 4210752);
	}

	//////jei////////
	private StateSwitchingButton jeiButton = null;
	private int lastDestinationGrid = 1;
	@Override
	protected void init() {
		super.init();
		this.createJeiButton();
	}

	@Override
	public void setPositionsX() {
		super.setPositionsX();
		if (this.jeiButton != null) {
			this.jeiButton.setPosition(DestinationPickerButton.JEI_BUTTON_RENDERX + this.renderLeftPos, DestinationPickerButton.JEI_BUTTON_RENDERY);
		}
	}

	private void createJeiButton() {
		if (ModList.get().isLoaded("jei")) {
			this.getBackgroundImage();
			this.jeiButton = new DestinationPickerButton(0, 0, DestinationPickerButton.JEI_BUTTON_WIDTH, DestinationPickerButton.JEI_BUTTON_HEIGTH, false);  // last par is initial state
			this.jeiButton.initTextureValues(DestinationPickerButton.JEI_BUTTON_POSX, DestinationPickerButton.JEI_BUTTON_POSY, DestinationPickerButton.JEI_BUTTON_WIDTH + DestinationPickerButton.JEI_BUTTON_MARGIN, DestinationPickerButton.JEI_BUTTON_HEIGTH + DestinationPickerButton.JEI_BUTTON_MARGIN, BACKGROUND_LOCATION[0]);
			this.jeiButton.setPosition(DestinationPickerButton.JEI_BUTTON_RENDERX + this.renderLeftPos, DestinationPickerButton.JEI_BUTTON_RENDERY + this.topPos);
			this.addRenderableWidget(this.jeiButton);
		}
	}

	@Override
	public void render(PoseStack poseStack, int p_98480_, int p_98481_, float p_98482_) {
		super.render(poseStack, p_98480_, p_98481_, p_98482_);
		if (this.jeiButton != null) {
		//	this.jeiButton.render(poseStack,p_98480_, p_98481_, p_98482_);
		}
	}

	private class DestinationPickerButton extends StateSwitchingButton
	{
		private static final int JEI_BUTTON_WIDTH = 40, JEI_BUTTON_HEIGTH = 28, JEI_BUTTON_POSX = 176, JEI_BUTTON_POSY = 0, JEI_BUTTON_MARGIN = 0, JEI_BUTTON_RENDERX = 80, JEI_BUTTON_RENDERY = 62;

		public DestinationPickerButton(int p_94615_, int p_94616_, int p_94617_, int p_94618_, boolean p_94619_) {
			super(p_94615_, p_94616_, p_94617_, p_94618_, p_94619_);
		}

		@Override
		public void onClick(double p_93634_, double p_93635_) {
		}

		@Override
		public boolean mouseClicked(double p_93641_, double p_93642_, int p_93643_) {
			if (p_93641_ >= this.x && p_93641_ <= this.x + JEI_BUTTON_WIDTH
				&& p_93642_ >= this.y && p_93642_ <= this.y + JEI_BUTTON_HEIGTH) {
				this.setStateTriggered(! this.isStateTriggered);
				int destination = this.isStateTriggered ? 2 : 1;
				if (destination != lastDestinationGrid) {
					lastDestinationGrid = destination;
					PacketSender.sendDestinationGridChangeToServer(destination);
				}
				return true;
			}
			return super.mouseClicked(p_93641_, p_93642_, p_93643_);
		}
	}
}
