package moonfather.workshop_for_handsome_adventurer.items.task_list.items.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

public class TaskListEditBox extends EditBox
{
    public TaskListEditBox(Font font, int width, int height, Component message)
    {
        super(font, 0, 0, width, height, message);
    }

    // we do only one thing here - skip +, - and *  because of special handling in screen class.
    @Override
    public boolean charTyped(char codePoint, int modifiers)
    {
        if ((codePoint == '*' || codePoint == '+') && modifiers == 0)
        {
            return false;
        }
        if ((codePoint == '-') && modifiers == 0)
        {
            return false; // a little problematic
        }
        if (codePoint == '_')
        {
            return super.charTyped('-', 0); // allow dashes
        }
        return super.charTyped(codePoint, modifiers);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    // now to remove the stupid shadow

      public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial)
      {
          if (this.ourGraphics == null)
          {
              this.ourGraphics = new GuiGraphicsWithoutTextShadows(Minecraft.getInstance(), guiGraphics.bufferSource());
          }
          this.ourGraphics.pose = guiGraphics.pose();
          this.ourGraphics.bufferSource = guiGraphics.bufferSource();
          super.renderWidget(this.ourGraphics, mouseX, mouseY, partial);
      }

    private GuiGraphics ourGraphics = null;

    private class GuiGraphicsWithoutTextShadows extends GuiGraphics
    {
        public GuiGraphicsWithoutTextShadows(Minecraft minecraft, MultiBufferSource.BufferSource bufferSource) { super(minecraft, bufferSource); }

        public int drawString(Font p_283019_, FormattedCharSequence p_283376_, int p_283379_, int p_283346_, int p_282119_) {
            return this.drawString(p_283019_, p_283376_, p_283379_, p_283346_, p_282119_, false);
        }

        public int drawString(Font p_282003_, @Nullable String p_281403_, int p_282714_, int p_282041_, int p_281908_) {
            return this.drawString(p_282003_, p_281403_, p_282714_, p_282041_, p_281908_, false);
        }
    }
}
