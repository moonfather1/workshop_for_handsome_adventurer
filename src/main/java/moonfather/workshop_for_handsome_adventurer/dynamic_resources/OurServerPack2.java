package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers.*;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class OurServerPack2  extends BaseResourcePack
{
    public OurServerPack2()
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

        BlockTagWriter1.writeFiles(cache);
        BlockTagWriter2.writeFiles(cache);
        BlockTagWriter3.writeFiles(cache);
        BlockTagWriter4.writeFiles(cache);
        ItemTagWriter1.writeFiles(cache);
    }



    @Override
    public Set<String> getNamespaces(PackType type)
    {
        if (type != PackType.SERVER_DATA) return Set.of();
        return namespaces;
    }
    private static final Set<String> namespaces = Set.of(Constants.MODID, "forge", "minecraft", "packingtape", "inventorytabs");



    @Override
    public String packId() { return "Workshop - auto-generated tags"; }
}
