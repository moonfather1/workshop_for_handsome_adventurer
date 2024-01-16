package moonfather.workshop_for_handsome_adventurer;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class Constants
{ 
    public static final String MODID = "workshop_for_handsome_adventurer";

    public static final int DYN_PACK_SERVER_FORMAT = 9;
    public static final int DYN_PACK_RESOURCE_FORMAT = 8;

    public static class Tags
    {
        public static final TagKey<Item> NOT_ALLOWED_ON_TOOLRACK = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Constants.MODID, "dont_allow_onto_toolrack"));
        public static final TagKey<Item> ALLOWED_ON_POTION_SHELF = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Constants.MODID, "allowed_on_potion_shelf"));
    }
} 
