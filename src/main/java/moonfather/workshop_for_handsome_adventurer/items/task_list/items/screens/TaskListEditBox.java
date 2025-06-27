package moonfather.workshop_for_handsome_adventurer.items.task_list.items.screens;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class TaskListEditBox extends EditBox
{
    public TaskListEditBox(Font font, int width, int height, Component message)
    {
        super(font, width, height, message);
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
}
