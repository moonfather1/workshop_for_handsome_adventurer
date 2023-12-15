package moonfather.workshop_for_handsome_adventurer.block_entities.screen_components;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleButton extends Button
{
    private final ResourceLocation resourceLocationNormal, resourceLocationHovered, resourceLocationDisabled;
    private final int textureWidth;
    private final int textureHeight;

    private String tooltipKey = null;
    private Component tooltipInset = null;
    List<Component> tooltipLines = null;

    public SimpleButton(int x, int y, int width, int height, String texture, String suffixNormal, String suffixHovered, String suffixDisabled, int textureWidth, int textureHeight, OnPress onPress, Component message)
    {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.resourceLocationNormal = new ResourceLocation(texture.formatted(suffixNormal));
        this.resourceLocationHovered = new ResourceLocation(texture.formatted(suffixHovered));
        this.resourceLocationDisabled = new ResourceLocation(texture.formatted(suffixDisabled));
    }


    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float p_94285_)
    {
        ResourceLocation image = this.resourceLocationNormal;
        if (this.isActive() && this.isHoveredOrFocused())
        {
            image = this.resourceLocationHovered;
        }
        else if (! this.isActive())
        {
            image = this.resourceLocationDisabled;
        }
        graphics.blit(image, this.getX(), this.getY(), 0, 0, this.width, this.height, this.textureWidth, this.textureHeight);
    }

    public void renderTooltipsSeparately(GuiGraphics graphics, Font font, int mouseX, int mouseY)
    {
        if (this.isHovered)
        {
            if (this.tooltipLines == null)
            {
                this.tooltipLines = new ArrayList<>(2);
                Arrays.stream(Language.getInstance().getOrDefault(this.tooltipKey)
                                      .split("\n"))
                      .forEach(text -> {
                          int pos = text.indexOf("%s");
                          if (pos == -1)
                          {
                              this.tooltipLines.add(Component.literal(text).withStyle(ChatFormatting.GRAY));
                          }
                          else
                          {
                              this.tooltipLines.add(Component.literal(text.substring(0, pos)).withStyle(ChatFormatting.GRAY)
                                                             .append(this.tooltipInset.copy().withStyle(Style.EMPTY.withColor(0xeeaa77)))
                                                             .append(Component.literal(text.substring(pos + 2)).withStyle(ChatFormatting.GRAY))
                              );
                          }
                      });
            }
            graphics.renderComponentTooltip(font, this.tooltipLines, mouseX, mouseY - 8);
        }
    }

    public void setTooltipKey(String newKey)
    {
        this.tooltipKey = newKey;
        this.tooltipLines = null;
    }

    public void setTooltipInset(Component newTooltip)
    {
        this.tooltipInset = newTooltip;
        this.tooltipLines = null;
    }
}
