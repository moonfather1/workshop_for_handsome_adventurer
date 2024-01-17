package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers.BlockTagWriter2;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;

import java.util.List;

@Mod.EventBusSubscriber
public class MissingMappingsHandler
{
    @SubscribeEvent
    public static void mappingEvent(MissingMappingsEvent event)
    {
        List<MissingMappingsEvent.Mapping<Block>> blockList = event.getMappings(Registries.BLOCK, Constants.MODID);
        for (MissingMappingsEvent.Mapping<Block> mapping : blockList)
        {
            for (String prefix : BlockTagWriter2.files)
            {
                if (mapping.getKey().getPath().startsWith(prefix))
                {
                    mapping.remap(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(Constants.MODID, prefix + "oak")));
                    break;
                }
            }
        }
        blockList = event.getMappings(Registries.BLOCK, "every_compat");
        for (MissingMappingsEvent.Mapping<Block> mapping : blockList)
        {
            for (String prefix : BlockTagWriter2.files)
            {
                if (mapping.getKey().getPath().startsWith("wfha") && mapping.getKey().getPath().contains(prefix))
                {
                    mapping.remap(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(Constants.MODID, prefix + "oak")));
                    break;
                }
            }
        }

    }
}
