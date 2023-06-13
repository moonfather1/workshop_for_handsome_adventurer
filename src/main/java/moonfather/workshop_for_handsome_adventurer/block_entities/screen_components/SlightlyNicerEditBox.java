package moonfather.workshop_for_handsome_adventurer.block_entities.screen_components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class SlightlyNicerEditBox extends EditBox {
    public SlightlyNicerEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
    }

    @Override
    public void renderWidget(PoseStack p_94160_, int p_94161_, int p_94162_, float p_94163_) {
        int borderColor = this.isFocused() ? 0xffffffff : 0xff555555;
        borderColor = this.isHovered ? 0xffffcc88 : borderColor;
        int textboxBgColor = this.isFocused() ? 0xff8b8b8b : 0xff8b8b8b;
        int xOffset = -2, yOffset = -3;
        fill(p_94160_, this.getX() - 1 + xOffset, this.getY() - 1 + yOffset, this.getX() + this.width + 1 + xOffset, this.getY() + this.height + 1 + yOffset, borderColor);
        fill(p_94160_, this.getX() + xOffset, this.getY() + yOffset, this.getX() + this.width + xOffset, this.getY() + this.height + yOffset, textboxBgColor);
        super.renderWidget(p_94160_, p_94161_, p_94162_, p_94163_);
    }

    @Override
    public void render(PoseStack p_93657_, int p_93658_, int p_93659_, float p_93660_) {
        super.render(p_93657_, p_93658_, p_93659_, p_93660_);
    }
}
