package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraftforge.fml.loading.FMLConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class DynamicAssetConfig
{
    public static boolean separateCreativeTab()
    {
        return SecondCreativeTab.items_table1.size() >= getClient().minimum_number_of_sets_for_separate_creative_tab;
    }

    public static boolean masterLeverOn()
    {
        return getCommon().generate_blocks_for_mod_added_woods;
    }

    ///////////////////

    private static InstantConfigServer getCommon()
    {
        if (common == null)
        {
            Path configPath = Path.of(FMLConfig.defaultConfigPath(), "../config", Constants.MODID + "-special-common.json");
            if (configPath.toFile().exists())
            {
                try
                {
                    Gson gson = new Gson();
                    common = gson.fromJson(Files.readString(configPath), InstantConfigServer.class);
                }
                catch (IOException ignored)
                {
                }
            }
            if (common == null)
            {
                common = new InstantConfigServer();
            }
            if (! configPath.toFile().exists())
            {
                try
                {
                    Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
                    String text = gson.toJson(common, InstantConfigServer.class);
                    Files.writeString(configPath, text, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                }
                catch (IOException ignored)
                {
                }
            }
        }
        return common;
    }
    private static InstantConfigServer common = null;

    private static InstantConfigClient getClient()
    {
        if (client == null)
        {
            Path configPath = Path.of(FMLConfig.defaultConfigPath(), "..", "config", Constants.MODID + "-special-client.json");
            if (configPath.toFile().exists())
            {
                try
                {
                    Gson gson = new Gson();
                    client = gson.fromJson(Files.readString(configPath), InstantConfigClient.class);
                }
                catch (IOException ignored)
                {
                }
            }
            if (client == null)
            {
                client = new InstantConfigClient();
            }
            if (! configPath.toFile().exists())
            {
                try
                {
                    Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
                    String text = gson.toJson(client, InstantConfigClient.class);
                    Files.writeString(configPath, text, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                }
                catch (IOException ignored)
                {
                }
            }
        }
        return client;
    }
    private static InstantConfigClient client = null;

    ////////////////////////////

    private static class InstantConfigServer // because the other kind is unavailable early
    {
        public String comment1 = "Unicorn-magic-powered system that automatically adds tables/racks/shelves for all wood types in the game (yes, modded ones too, that is the point of this system). If you turn this off (does not work? please report!), workshop blocks will only be added in vanilla wood types.";
        public String comment2 = "Option requires game restart. Will synchronize it in future versions, for now users need to be careful.";
        public boolean generate_blocks_for_mod_added_woods = true;
    }

    private static class InstantConfigClient // because the other kind is unavailable early
    {
        public String comment1 = "How many wood sets are needed for this mod to use a second tab in creative mode for dynamically created blocks. Set to a high number to disable second tab and shove everything into first.";
        public int minimum_number_of_sets_for_separate_creative_tab = 4;
    }
}
