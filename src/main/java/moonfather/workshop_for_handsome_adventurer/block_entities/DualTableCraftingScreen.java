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
public class DualTableCraftingScreen extends SimpleTableCraftingScreen
{
	private static final ResourceLocation[] BACKGROUND_LOCATION = new ResourceLocation[5];
	protected int renderLeftPos;

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
}
