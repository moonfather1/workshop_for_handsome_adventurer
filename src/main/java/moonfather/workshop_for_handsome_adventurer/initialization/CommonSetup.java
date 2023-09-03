package moonfather.workshop_for_handsome_adventurer.initialization;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.messaging.PacketSender;
import moonfather.workshop_for_handsome_adventurer.other.OptionalRecipeCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonSetup
{
	public static void init(FMLCommonSetupEvent event)
	{
		PacketSender.registerMessage();
		CraftingHelper.register(new OptionalRecipeCondition.Serializer(new ResourceLocation(Constants.MODID, "optional")));

		if (ModList.get().isLoaded("craftingtweaks")) {
			try {
				Class.forName("moonfather.workshop_for_handsome_adventurer.integration.CraftingTweaksProvider").getConstructor().newInstance();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
