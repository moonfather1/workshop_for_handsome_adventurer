package moonfather.workshop_for_handsome_adventurer.items.task_list.items.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TaskListPageButton extends AbstractWidget
{
    private final ResourceLocation imageLocation;
    private final int texX, texY, fullW, fullH;

    public TaskListPageButton(int x, int y, int w, int h, Component hint, ResourceLocation imageLocation, int tx, int ty, int fullW, int fullH)
    {
        super(x, y, w, h, hint);
        this.imageLocation = imageLocation;
        this.texX = tx;
        this.texY = ty;
        this.fullW = fullW;
        this.fullH = fullH;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        guiGraphics.blit(this.imageLocation, this.getX(), this.getY(), this.texX, this.texY, this.getWidth(), this.getHeight(), this.fullW, this.fullH);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) { }
}
