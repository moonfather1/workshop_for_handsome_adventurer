package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.dynamic_resources.helpers.BlockTagWriter2;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;

public class MissingMappingsHandler
{
    // handles the missing mappings
    public static void prepareMappings()
    {
        for (String earlier : earlierTypes)
        {
            if (! WoodTypeLister.getWoodIds().contains(earlier))
            {
                for (String prefix : BlockTagWriter2.files)
                {
                    Identifier missing = Identifier.fromNamespaceAndPath(Constants.MODID, prefix + earlier);
                    Identifier fallback = Identifier.fromNamespaceAndPath(Constants.MODID, prefix + "oak");
                    BuiltInRegistries.BLOCK.addAlias(missing, fallback);
                }
            }
        }
    }



    // reads all previously existing wood types
    public static void read()
    {
        String raw = "";
        try
        {
            Path path = Path.of("config", "workshop", "mappings.dat");
            raw = Files.readString(path);
        }
        catch (IOException ignored) {       }
        Arrays.stream(raw.split(",\\s*")).forEach(earlierTypes::add);
        earlierTypes.remove("");
    }
    private static final HashSet<String> earlierTypes = new HashSet<>();



    public static void storeForNextTime()
    {
        WoodTypeLister.getWoodIds().forEach(earlierTypes::add);
        String raw = String.join(", ", earlierTypes);
        try
        {
            Path path = Path.of("config", "workshop", "mappings.dat");;
            Files.writeString(path, raw, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (IOException e)
        {
            LogManager.getLogger().error("WFHA error 507 (" + e.getClass().getName() + "):  " + e.getMessage() + "  ~~~");
        }
    }
}
