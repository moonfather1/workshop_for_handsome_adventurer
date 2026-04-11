package moonfather.workshop_for_handsome_adventurer.items.task_list.items.screens;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.network.chat.Component;

public class TaskListEditBox extends EditBox
{
    public TaskListEditBox(Font font, int width, int height, Component message)
    {
        super(font, width, height, message);
    }

    // we do only one thing here - skip +, - and *  because of special handling in screen class.
    @Override
    public boolean charTyped(CharacterEvent event)
    {
        if ((event.codepoint() == '*' || event.codepoint() == '+') /*&& event.modifiers() == 0*/)
        {
            return false;
        }
        if ((event.codepoint() == '-') /*&& event.modifiers() == 0*/)
        {
            return false; // a little problematic
        }
        if (event.codepoint() == '_')
        {
            return super.charTyped(new CharacterEvent('-')); // allow dashes
        }
        return super.charTyped(event);
    }
}
