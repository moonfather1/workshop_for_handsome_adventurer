package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.fml.ModList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public abstract class AssetReader
{
    private static AssetReader serverReader = null, clientReader = null;

    public static AssetReader getInstance(PackType packType, String namespace)
    {
        if (packType.equals(PackType.SERVER_DATA))
        {
            if (serverReader == null)
            {
                if (ModList.get().isLoaded("dynamic_asset_generator"))
                {
                    serverReader = new DAGResourceReader(packType, namespace);
                }
                else
                {
                    serverReader = new RawResourceReader(packType, namespace);
                }
            }
            return serverReader;
        }
        if (packType.equals(PackType.CLIENT_RESOURCES))
        {
            if (clientReader == null)
            {
                if (ModList.get().isLoaded("dynamic_asset_generator"))
                {
                    clientReader = new DAGResourceReader(packType, namespace);
                }
                else
                {
                    clientReader = new RawResourceReader(packType, namespace);
                }
            }
            return clientReader;
        }
        return null;
    }

    public abstract InputStream getStream(ResourceLocation location);
	
	public String getText(ResourceLocation location)
	{
		InputStream is = this.getStream(location);
		if (is == null) 
		{
			return null;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
        return in.lines().collect(Collectors.joining("\n"));
	}

    //////////////////////////////////////////////////

    private static class RawResourceReader extends AssetReader
    {
        public RawResourceReader(PackType packType, String namespace)
        {
            super();
            this.prefix = packType.equals(PackType.SERVER_DATA) ? ("/data/" + namespace + "/") : ("/assets/" + namespace + "/");
        }
        private final String prefix;

        @Override
        public InputStream getStream(ResourceLocation location)
        {
            String path = this.prefix + location.getPath();
            return this.getClass().getResourceAsStream(path);
        }
    }
}
