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
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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

    private TranslatableComponent tooltip = null;
    private Component tooltipInset = null;
    List<Component> tooltipLines = null;

    public SimpleButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation texture, int textureWidth, int textureHeight, OnPress onPress, Component message) {
        super(x, y, width, height, message, onPress);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.yDiffTex = yDiffTex;
        this.resourceLocation = texture;
    }


    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float p_94285_) {
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
        blit(poseStack, this.x, this.y, (float)this.xTexStart, (float)yOffset, this.width, this.height, this.textureWidth, this.textureHeight);
        if (this.isHovered) {
            if (this.tooltipLines == null) {
                this.tooltipLines = new ArrayList<>(2);
                Arrays.stream(Language.getInstance().getOrDefault(this.tooltip.getKey())
                                .split("\n"))
                        .forEach(text -> {
                            int pos = text.indexOf("%s");
                            if (pos == -1) {
                                this.tooltipLines.add(new TextComponent(text).withStyle(ChatFormatting.GRAY));
                            }
                            else {
                                this.tooltipLines.add(new TextComponent(text.substring(0, pos)).withStyle(ChatFormatting.GRAY)
                                        .append(this.tooltipInset.copy().withStyle(Style.EMPTY.withColor(0xeeaa77)))
                                        .append(new TextComponent(text.substring(pos+2)).withStyle(ChatFormatting.GRAY))
                                );
                            }
                        });
            }
            Minecraft.getInstance().screen.renderTooltip(poseStack, this.tooltipLines, Optional.empty(), mouseX, mouseY-8);
        }
    }

    public void setTooltipBase(TranslatableComponent newTooltip) {
        this.tooltip = newTooltip;
        this.tooltipLines = null;
    }

    public void setTooltipInset(Component newTooltip) {
        this.tooltipInset = newTooltip;
        this.tooltipLines = null;
    }
}
