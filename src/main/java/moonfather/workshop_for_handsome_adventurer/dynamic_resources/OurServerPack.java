package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.Map;

public class OurServerPack extends BaseResourcePack
{
    public OurServerPack()
    {
        super(PackType.SERVER_DATA, Constants.DYN_PACK_SERVER_FORMAT);
    }

    @Override
    protected void buildResources(Map<ResourceLocation, String> cache)
    {
        BlockTagWriter1.writeFiles(cache);
        BlockTagWriter2.writeFiles(cache);
        BlockTagWriter3.writeFiles(cache);
        BlockTagWriter4.writeFiles(cache);
        ItemTagWriter1.writeFiles(cache);
        RecipeWriter.writeFiles(cache);
        LootTableWriter.writeFiles(cache);
    }



    @Override
    public String getName() { return "Workshop - auto-generated recipes and loot tables"; }
}
