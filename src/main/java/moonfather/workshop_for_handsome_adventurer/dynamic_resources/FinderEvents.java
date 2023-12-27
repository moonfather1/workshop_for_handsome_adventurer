package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraftforge.event.AddPackFindersEvent;

public class FinderEvents
{
    public static void addClientPack(AddPackFindersEvent event)
    {
        if (DynamicAssetConfig.masterLeverOn() && event.getPackType() == PackType.CLIENT_RESOURCES)
        {
            event.addRepositorySource((packConsumer) ->
            {
                @SuppressWarnings("resource")
                PackResources pack = new OurClientPack();

                packConsumer.accept(Pack.create(
                        Constants.MODID + "_client",
                        Component.literal("Workshop - client assets"),
                        true,
                        s -> pack,
                        new Pack.Info(
                                Component.literal(pack.packId()),
                                Constants.DYN_PACK_SERVER_FORMAT,
                                Constants.DYN_PACK_RESOURCE_FORMAT,
                                FeatureFlagSet.of(),
                                true
                        ),
                        PackType.CLIENT_RESOURCES,
                        Pack.Position.BOTTOM,
                        true,
                        PackSource.DEFAULT
                ));
            });
        }
    }

    public static void addServerPack(AddPackFindersEvent event)
    {
        if (DynamicAssetConfig.masterLeverOn() && event.getPackType() == PackType.SERVER_DATA)
        {
            event.addRepositorySource((packConsumer) ->
            {
                @SuppressWarnings("resource")
                PackResources pack = new OurServerPack();

                packConsumer.accept(Pack.create(
                        Constants.MODID + "_server",
                        Component.literal("Workshop - server-side generated assets"),
                        true,
                        s -> pack,
                        new Pack.Info(
                                Component.literal(pack.packId()),
                                Constants.DYN_PACK_SERVER_FORMAT,
                                Constants.DYN_PACK_RESOURCE_FORMAT,
                                FeatureFlagSet.of(),
                                true
                        ),
                        PackType.SERVER_DATA,
                        Pack.Position.BOTTOM,
                        true,
                        PackSource.DEFAULT
                ));
            });
        }
    }
}
