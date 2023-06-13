package moonfather.workshop_for_handsome_adventurer.block_entities.screen_components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SimpleButton extends Button {
    private final ResourceLocation resourceLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int yDiffTex;
    private final int textureWidth;
    private final int textureHeight;

    private String tooltipKey = null;
    private Component tooltipInset = null;
    List<Component> tooltipLines = null;

    public SimpleButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation texture, int textureWidth, int textureHeight, OnPress onPress, Component message) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.yDiffTex = yDiffTex;
        this.resourceLocation = texture;
    }


    @Override
    public void renderWidget(PoseStack poseStack, int mouseX, int mouseY, float p_94285_) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.resourceLocation);
        int yOffset = this.yTexStart;
        if (this.isActive() && this.isHoveredOrFocused()) {
            yOffset += this.yDiffTex;
        }
        else if (! this.isActive()) {
            yOffset += 2 * this.yDiffTex;
        }
        RenderSystem.enableDepthTest();
        blit(poseStack, this.getX(), this.getY(), (float)this.xTexStart, (float)yOffset, this.width, this.height, this.textureWidth, this.textureHeight);
    }

    public void renderTooltipsSeparately(PoseStack poseStack, int mouseX, int mouseY) {
        if (this.isHovered) {
            if (this.tooltipLines == null) {
                this.tooltipLines = new ArrayList<>(2);
                Arrays.stream(Language.getInstance().getOrDefault(this.tooltipKey)
                                .split("\n"))
                        .forEach(text -> {
                            int pos = text.indexOf("%s");
                            if (pos == -1) {
                                this.tooltipLines.add(Component.literal(text).withStyle(ChatFormatting.GRAY));
                            }
                            else {
                                this.tooltipLines.add(Component.literal(text.substring(0, pos)).withStyle(ChatFormatting.GRAY)
                                        .append(this.tooltipInset.copy().withStyle(Style.EMPTY.withColor(0xeeaa77)))
                                        .append(Component.literal(text.substring(pos+2)).withStyle(ChatFormatting.GRAY))
                                );
                            }
                        });
            }
            Minecraft.getInstance().screen.renderTooltip(poseStack, this.tooltipLines, Optional.empty(), mouseX, mouseY-8);
        }
    }

    public void setTooltipKey(String newKey) {
        this.tooltipKey = newKey;
        this.tooltipLines = null;
    }

    public void setTooltipInset(Component newTooltip) {
        this.tooltipInset = newTooltip;
        this.tooltipLines = null;
    }
}
