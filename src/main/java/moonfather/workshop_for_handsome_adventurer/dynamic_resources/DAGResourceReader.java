package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import dev.lukebemish.dynamicassetgenerator.impl.OldResourceGenerationContext;
import dev.lukebemish.dynamicassetgenerator.impl.OldServerGenerationContext;
import dev.lukebemish.dynamicassetgenerator.impl.client.OldClientGenerationContext;
import net.minecraft.resources.ResourceLocation;
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

    private OldResourceGenerationContext context = null;

    private void initContext()
    {
        if (this.context == null)
        {
            if (packType.equals(PackType.SERVER_DATA))
            {
                this.context = new OldServerGenerationContext(new ResourceLocation(namespace, "data"));
            }
            else
            {
                this.context = new OldClientGenerationContext(new ResourceLocation(namespace, "assets"));
            }
        }
    }

    @Override
    public InputStream getStream(ResourceLocation location)
    {
        this.initContext();
        IoSupplier<InputStream> sup =  this.context.getResource(location);
        if (sup != null)
        {
            try
            {
                return sup.get();
            }
            catch (IOException e)
            {
                return null;
            }
        }
        return null;
    }
}
