package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers.*;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class OurServerPack extends BaseResourcePack
{
    public OurServerPack()
    {
        super(PackType.SERVER_DATA, Constants.DYN_PACK_SERVER_FORMAT);
    }

    @Override
    protected void buildResources(Map<ResourceLocation, String> cache)
    {
        CompletableFuture<HolderLookup.Provider> holderProvider = CompletableFuture.supplyAsync(
                VanillaRegistries::createLookup,
                Util.backgroundExecutor()
        );

        RecipeWriter.writeFiles(cache);
        LootTableWriter.writeFiles(cache);
    }



    @Override
    protected boolean isNotOurRecipe(String namespace)
    {
        return ! namespace.equals(Constants.MODID) && ! namespace.equals("tetra_tables");
    }



    @Override
    public String packId() { return "Workshop - auto-generated recipes and loot tables"; }
}
