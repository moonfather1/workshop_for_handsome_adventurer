package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers.*;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class OurServerPack2  extends BaseResourcePack
{
    private final PackLocationInfo locationInfo;

    public OurServerPack2(PackLocationInfo locationInfo)
    {
        super(PackType.SERVER_DATA, SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA));
        this.locationInfo = locationInfo;
    }


    @Override
    protected void buildResources(Map<ResourceLocation, String> cache)
    {
        // CompletableFuture<HolderLookup.Provider> holderProvider = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
        // removed because of traverse/terrestria issue.

        BlockTagWriter1.writeFiles(cache);
        BlockTagWriter2.writeFiles(cache);
        BlockTagWriter3.writeFiles(cache);
        BlockTagWriter4.writeFiles(cache);
        BlockTagWriter5.writeFiles(cache);
        ItemTagWriter1.writeFiles(cache);
    }



    @Override
    protected boolean isNotOurNamespace(String namespace)
    {
        return ! namespaces.contains(namespace);
    }

    @Override
    protected boolean isNotOurThing(String path) { return ! path.startsWith("tags"); }

    @Override
    public Set<String> getNamespaces(PackType type)
    {
        if (type != PackType.SERVER_DATA) return Set.of();
        return namespaces;
    }

    private static final Set<String> namespaces = Set.of(Constants.MODID, "c", "minecraft", "packingtape", "inventorytabs");



    @Override
    public PackLocationInfo location()
    {
        return this.locationInfo;
    }

    @Override
    public String packId() { return "Workshop - auto-generated tags"; }
}
