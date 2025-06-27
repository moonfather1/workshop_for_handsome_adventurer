package moonfather.workshop_for_handsome_adventurer.items.task_list.items.screens;

import moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListMessaging;
import net.minecraft.client.Minecraft;

import java.util.List;


// just so that we don't touch client stuff on server side
public class TaskListClientInvoker
{
    public static void invokeScreen(List<TaskListMessaging.TaskPageDTO> pagesToDisplay, int pageCount, TaskListMessaging.TaskListExtraDTO extra)
    {
        Minecraft.getInstance().setScreen(new TaskListScreen(pagesToDisplay, pageCount, extra));
    }
}
