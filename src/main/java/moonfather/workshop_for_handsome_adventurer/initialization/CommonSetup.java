package moonfather.workshop_for_handsome_adventurer.initialization;

import moonfather.workshop_for_handsome_adventurer.block_entities.messaging.PacketSender;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonSetup
{
	public static void init(FMLCommonSetupEvent event)
	{
		PacketSender.registerMessage();
	}
}
