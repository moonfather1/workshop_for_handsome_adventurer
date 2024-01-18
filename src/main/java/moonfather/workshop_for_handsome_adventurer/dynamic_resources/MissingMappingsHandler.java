package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import com.google.common.collect.ImmutableList;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers.BlockTagWriter2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class MissingMappingsHandler
{
    public static void mappingEvent(RegistryEvent.MissingMappings<Block> event)
    {
        ImmutableList<RegistryEvent.MissingMappings.Mapping<Block>> blockList = event.getMappings(Constants.MODID);
        for (RegistryEvent.MissingMappings.Mapping<Block> mapping : blockList)
        {
            for (String prefix : BlockTagWriter2.files)
            {
                if (mapping.key.getPath().startsWith(prefix))      // our blocks for removed woods
                {
                    mapping.remap(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(Constants.MODID, prefix + "oak")));
                    break;
                }
            }
        }
        blockList = event.getMappings("everycomp");
        for (RegistryEvent.MissingMappings.Mapping<Block> mapping : blockList)
        {
            for (String prefix : BlockTagWriter2.files)
            {
                if (mapping.key.getPath().startsWith("wfha") && mapping.key.getPath().contains(prefix)) // every compat
                {
                    mapping.remap(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(Constants.MODID, prefix + "oak")));
                    break;
                }
            }
        }
    }
}