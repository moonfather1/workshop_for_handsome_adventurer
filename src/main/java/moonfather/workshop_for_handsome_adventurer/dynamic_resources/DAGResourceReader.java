package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import dev.lukebemish.dynamicassetgenerator.api.DataResourceCache;
import dev.lukebemish.dynamicassetgenerator.api.ResourceCache;
import dev.lukebemish.dynamicassetgenerator.api.ResourceGenerationContext;
import dev.lukebemish.dynamicassetgenerator.api.client.AssetResourceCache;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;

import java.io.IOException;
import java.io.InputStream;

public class DAGResourceReader extends AssetReader
{
    public DAGResourceReader(PackType packType, String namespace)
    {
        this.packType = packType;
        this.namespace = namespace;
    }

    private final PackType packType;
    private final String namespace;

    ////////////////

    private ResourceCache cache = null;
    private ResourceGenerationContext context = null;

    private void initContext()
    {
        if (this.context == null)
        {
//            if (packType.equals(PackType.SERVER_DATA))
//            {
//                this.cache = ResourceCache.register(new DataResourceCache(Identifier.fromNamespaceAndPath(namespace, "data")));
//            }
//            else
//            {
//                this.cache = ResourceCache.register(new AssetResourceCache(Identifier.fromNamespaceAndPath(namespace, "assets")));
//            }
//            this.context = this.cache.makeContext(true).withResourceSource(ResourceGenerationContext.ResourceSource.filtered((s) -> true, packType));
        }
    }

    @Override
    public InputStream getStream(Identifier location)
    {
//        this.initContext();
//        IoSupplier<InputStream> sup =  this.context.getResourceSource().getResource(location);
//        if (sup != null)
//        {
//            try
//            {
//                return sup.get();
//            }
//            catch (IOException e)
//            {
//                return null;
//            }
//        }
        return null;
    }
}
