package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackMetadataResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;

import java.util.stream.Stream;

public class SimpleResourceSupplier implements Pack.ResourcesSupplier
{
    public SimpleResourceSupplier(PackResources actualPack)
    {
        this.packContents = actualPack;
    }
    private final PackResources packContents;

    @Override
    public PackMetadataResources openMetadata(PackLocationInfo location)
    {
        if (location.equals(this.packContents.location()))
        {
            return this.packContents;
        }
        return null;
    }

    @Override
    public Stream<PackResources> openResources(PackLocationInfo location, Pack.Metadata metadata)
    {
        if (location.equals(this.packContents.location()))
        {
            return Stream.of(this.packContents);
        }
        return Stream.empty();
    }
}
