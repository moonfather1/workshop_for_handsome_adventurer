package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;

public class FinderEvents
{
    public static void addClientPack(AddPackFindersEvent event)
    {
        if (DynamicAssetConfig.masterLeverOn() && event.getPackType() == PackType.CLIENT_RESOURCES)
        {
            event.addRepositorySource((packConsumer, packConstructor) ->
            {
                @SuppressWarnings("resource")
                PackResources pack = new OurClientPack();

                packConsumer.accept(Pack.create(
                        Constants.MODID + "_client",
                        true,
                        () -> pack,
                        packConstructor,
                        Pack.Position.BOTTOM,
                        PackSource.DEFAULT
                ));
            });
        }
    }

    public static void addServerPack(AddPackFindersEvent event)
    {
        if (DynamicAssetConfig.masterLeverOn() && event.getPackType() == PackType.SERVER_DATA)
        {
            event.addRepositorySource((packConsumer, packConstructor) ->
            {
                @SuppressWarnings("resource")
                PackResources pack = new OurServerPack();

                packConsumer.accept(Pack.create(
                        Constants.MODID + "_server",
                        true,
                        () -> pack,
                        packConstructor,
                        Pack.Position.BOTTOM,
                        PackSource.DEFAULT
                ));
            });
            event.addRepositorySource((packConsumer, packConstructor) ->
            {
                @SuppressWarnings("resource")
                PackResources pack = new OurServerPack2();

                packConsumer.accept(Pack.create(
                        Constants.MODID + "_server2",
                        true,
                        () -> pack,
                        packConstructor,
                        Pack.Position.BOTTOM,
                        PackSource.DEFAULT
                ));
            });
        }
    }
}
