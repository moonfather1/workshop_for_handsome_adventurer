package moonfather.workshop_for_handsome_adventurer.integration;

import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

public class TOPProxyRegistration
{
    public static void enqueueIMC(final InterModEnqueueEvent event)
    {
        if (ModList.get().isLoaded("theoneprobe"))
        {
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPRegistration::instance);
        }
    }
}
