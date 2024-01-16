package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import com.google.common.base.Stopwatch;
import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class BaseResourcePack implements PackResources
{
    private static final Collection<ResourceLocation> EMPTY = new ArrayList<>();
    private final Map<ResourceLocation, String> dataCache = new ConcurrentHashMap<>();
    private final PackType type;
    private final PackMetadataSection packMetadata;
    private Set<String> namespaces = null;

    protected BaseResourcePack(PackType type, int packFormat)
    {
        this.type = type;
        this.packMetadata = new PackMetadataSection(new TextComponent("Workshop resources"), packFormat);
    }

    ////////////////////////////////////

    protected abstract void buildResources(Map<ResourceLocation, String> cache);
    protected abstract boolean isNotOurFile(String namespace);

    ////////////////////////////////////////////////////////////

    private void buildOnDemand()
    {
        if (this.namespaces == null)
        {
            Stopwatch stopwatch = Stopwatch.createStarted();
            this.buildResources(this.dataCache);
            this.namespaces = this.dataCache.keySet()
                                            .stream()
                                            .map(ResourceLocation::getNamespace)
                                            .collect(Collectors.toSet());
            stopwatch.stop();
            //LogUtils.getLogger().info("~~~Generated dynamic {} in {} ms.", this.type.toString(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    @Nullable
    @Override
    public InputStream getRootResource(String fileName) throws IOException
    {
        this.buildOnDemand();
        return null;
    }

    @Nullable
    @Override
    public InputStream getResource(PackType type, ResourceLocation location) throws IOException
    {
        if (this.isNotOurFile(location.getNamespace())) { return null; }
        if (this.namespaces == null && ! (location.getNamespace().equals(Constants.MODID) && (location.getPath().contains("spruce") || location.getPath().contains("en_us"))))
        {
            this.buildOnDemand(); // so normally we do, but we allow this one to pass.
        }
        if (type == this.type && this.dataCache.containsKey(location))
        {
            return new ByteArrayInputStream(this.dataCache.get(location).getBytes(StandardCharsets.UTF_8));
        }
        return null;
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType type, String namespace, String path, int maxDepth, Predicate<String> givenFilter)
    {
        if (this.isNotOurFile(namespace)) { return EMPTY; }
        if (path.equals("dynamic_asset_generator")) { return EMPTY; }
        if (type == this.type)
        {
            this.buildOnDemand();
            return this.dataCache.keySet()
                     .stream()
                     .filter(loc -> loc.getNamespace().equals(namespace))
                     .filter(loc -> loc.getPath().startsWith(path))
                     .filter(loc -> givenFilter.test(loc.getPath()))
                     .toList();
        }
        return EMPTY;
    }



    @Override
    public boolean hasResource(PackType type, ResourceLocation location)
    {
        if (this.isNotOurFile(location.getNamespace())) { return false; }
        if (type != this.type)
        {
            return false;
        }
        this.buildOnDemand();
        return this.dataCache.containsKey(location);
    }



    @Override
    public Set<String> getNamespaces(PackType type)
    {
        if (type != this.type) return Set.of();
        if (this.namespaces == null) return fixedNamespaces;
        return this.namespaces;  //i'm an idiot. this optimization killed tags and i wasted two days hitting my head on the table to figure out why.
    }
    private final Set<String> fixedNamespaces = Set.of(Constants.MODID, "tetra_tables");

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException
    {
        if (deserializer == PackMetadataSection.SERIALIZER)
        {
            return (T) this.packMetadata;
        }
        return null;
    }

    @Override
    public void close() { }

    @Override
    public boolean isHidden()
    {
        return true;
    }
}
