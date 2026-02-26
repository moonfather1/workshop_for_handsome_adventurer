package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import com.google.common.base.Stopwatch;
import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.InclusiveRange;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class BaseResourcePack implements PackResources
{
    private final Map<Identifier, String> dataCache = new ConcurrentHashMap<>();
    private final PackType type;
    private final PackMetadataSection packMetadata;
    private Set<String> namespaces = null;

    protected BaseResourcePack(PackType type, int packFormat)
    {
        this.type = type;
        this.packMetadata = new PackMetadataSection(Component.literal(this.packId()), SharedConstants.getCurrentVersion().packVersion(type).minorRange());
    }

    ////////////////////////////////////

    protected abstract void buildResources(Map<Identifier, String> cache);
    protected abstract boolean isNotOurNamespace(String namespace);
    protected abstract boolean isNotOurThing(String path);

    ////////////////////////////////////////////////////////////

    private void buildOnDemand()
    {
        if (this.namespaces == null)
        {
            Stopwatch stopwatch = Stopwatch.createStarted();
            this.buildResources(this.dataCache);
            this.namespaces = this.dataCache.keySet()
                                            .stream()
                                            .map(Identifier::getNamespace)
                                            .collect(Collectors.toSet());
            stopwatch.stop();
            //LogUtils.getLogger().info("~~~Generated dynamic {} in {} ms.", this.type.toString(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... fileName)
    {
        this.buildOnDemand();
        return null;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType type, Identifier location)
    {
        if (this.isNotOurNamespace(location.getNamespace())) { return null; }
        if (this.isNotOurThing(location.getPath())) { return null; }
        if (this.namespaces == null && ! (location.getNamespace().equals(Constants.MODID) && (location.getPath().contains("spruce") || location.getPath().contains("en_us"))))
        {
            this.buildOnDemand(); // so normally we do, but we allow this one to pass.
        }
        if (type == this.type && this.dataCache.containsKey(location))
        {
            return supplierForPath(location);
        }
        return null;
    }



    @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput output)
    {
        if (this.isNotOurNamespace(namespace)) { return; }
        if (this.isNotOurThing(path)) { return; }
        if (type == this.type)
        {
            this.buildOnDemand();
            this.dataCache.keySet()
                     .stream()
                     .filter(loc -> loc.getNamespace().equals(namespace))
                     .filter(loc -> loc.getPath().startsWith(path))
                     .forEach(loc -> output.accept(loc, supplierForPath(loc)));
        }
    }



    private IoSupplier<InputStream> supplierForPath(Identifier loc)
    {
        return () -> new ByteArrayInputStream(this.dataCache.get(loc).getBytes(StandardCharsets.UTF_8));
    }



    @Override
    public Set<String> getNamespaces(PackType type)
    {
        if (type != this.type) return Set.of();
        if (this.namespaces == null) return fixedNamespaces;
        return this.namespaces;  //i'm an idiot. this optimization killed tags when portinh to 1.19.2 and i wasted two days hitting my head on the table to figure out why.
    }
    private final Set<String> fixedNamespaces = Set.of(Constants.MODID, "tetra_tables");

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionType<T> metadataSectionType) throws IOException
    {
        if (metadataSectionType.equals(this.type))  // why was this PackMetadataSection.TYPE?
        {
            return (T) this.packMetadata;
        }
        return null;
    }

    @Override
    public String packId()
    {
        return "test_dag:" + this.getClass().getSimpleName();
    }

    @Override
    public void close()
    {
    }
}
