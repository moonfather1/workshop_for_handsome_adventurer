package moonfather.workshop_for_handsome_adventurer.integration;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.PotionShelfBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.StreamServerDataProvider;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class JadePotionDataProvider implements StreamServerDataProvider<BlockAccessor, List<Integer>>
{
    private static final JadePotionDataProvider INSTANCE = new JadePotionDataProvider();
    private static final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "jade_wfha_ps");

    public static JadePotionDataProvider getInstance() { return INSTANCE; }



    @Override
    public @Nullable List<Integer> streamData(BlockAccessor accessor)
    {
        PotionShelfBlockEntity shelf = (PotionShelfBlockEntity) accessor.getBlockEntity();
        List<Integer> result = new ArrayList<>(shelf.getCapacity());
        for (int i = 0; i < shelf.getCapacity(); i++)
        {
            result.add(shelf.GetRemainingItems(i));
        }
        return result;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, List<Integer>> streamCodec()
    {
        return ByteBufCodecs.INT.apply(ByteBufCodecs.list(12)).cast();  // will use 6
    }

    @Override
    public ResourceLocation getUid()
    {
        return id;
    }
}


