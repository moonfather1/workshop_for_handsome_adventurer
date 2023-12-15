package moonfather.workshop_for_handsome_adventurer.initialization;

import moonfather.workshop_for_handsome_adventurer.block_entities.messaging.PacketSender;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonSetup
{
	public static void init(FMLCommonSetupEvent event)
	{
		PacketSender.registerMessages();


		if (ModList.get().isLoaded("craftingtweaks")) {
			try {
				Class.forName("moonfather.workshop_for_handsome_adventurer.integration.CraftingTweaksProvider").getConstructor().newInstance();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
