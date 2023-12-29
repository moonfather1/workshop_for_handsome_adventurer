package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.metadata.language.LanguageMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class OurClientPack extends BaseResourcePack
{
    public OurClientPack()
    {
        super(PackType.CLIENT_RESOURCES, Constants.DYN_PACK_RESOURCE_FORMAT);
    }

    @Override
    protected void buildResources(Map<ResourceLocation, String> cache)
    {
        final String SPRUCE_PLANKS = "minecraft:block/spruce_planks";
        final String SPRUCE_LOG = "minecraft:block/stripped_spruce_log";
        final String SPRUCE = "spruce";
        final String TEMPLATE_PLANKS = "%s:block/%s_planks";
        final String TEMPLATE_LOG = "%s:block/stripped_%s_log";
        String json;
        for (String spruceFile: files)
        {
            json = AssetReader.getInstance(PackType.CLIENT_RESOURCES, Constants.MODID).getText(new ResourceLocation(Constants.MODID, spruceFile));
            if (json != null)
            {
                for (String wood: WoodTypeLister.getWoodIds())
                {
                    String replaced = json
                        .replace(SPRUCE_PLANKS, TEMPLATE_PLANKS.formatted(WoodTypeLister.getHostMod(wood), wood))
                        .replace(SPRUCE_LOG, TEMPLATE_LOG.formatted(WoodTypeLister.getHostMod(wood), wood))
                        .replace(SPRUCE, wood);
                    String namespace = spruceFile.contains("tetra") ? "tetra_tables" : Constants.MODID;
                    cache.put(new ResourceLocation(namespace, spruceFile.replace(SPRUCE, wood)), replaced);
                }
            }
        }
        // that was easy, now the language file:
        InputStream originalLang = AssetReader.getInstance(PackType.CLIENT_RESOURCES, Constants.MODID).getStream(new ResourceLocation(Constants.MODID, "lang/en_us.json"));
        if (originalLang != null)
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(originalLang));
            StringBuilder builder = new StringBuilder();
            builder.append("{\n");
            String line; boolean first = true;
            try
            {
                while ((line = reader.readLine()) != null)
                {
                    if (line.contains(SPRUCE))
                    {
                        if (line.endsWith(","))
                        {
                            line = line.substring(0, line.length() - 1);
                        }
                        for (String wood: WoodTypeLister.getWoodIds())
                        {
                            if (! first)
                            {
                                builder.append(",\n");
                            }
                            else
                            {
                                first = false;
                            }
                            builder.append(line.replace(SPRUCE, wood));
                        }
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            builder.append("\n}\n");
            cache.put(new ResourceLocation(Constants.MODID, "lang/en_us.json"), builder.toString());
        }
        // and now a totally unneeded emi aliases file
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("  \"aliases\": [\n");
        builder.append("    {\n");
        builder.append("      \"stacks\": [\n");
        String line = "\"item:workshop_for_handsome_adventurer:simple_table_";
        boolean first = true;
        for (String wood: WoodTypeLister.getWoodIds())
        {
            if (! first)
            {
                builder.append(",\n");
            }
            else
            {
                first = false;
            }
            builder.append(line + wood + '"');
        }
        builder.append("      ],\n");
        builder.append("      \"text\": [\n");
        builder.append("        \"alias.emi.workbench\"\n");
        builder.append("      ]\n");
        builder.append("    }\n  ]\n}\n");
        cache.put(new ResourceLocation("emi", "aliases/list2.json"), builder.toString());
    }

    private static final String[] files =
            {
                    "blockstates/book_shelf_double_spruce.json",
                    "blockstates/book_shelf_minimal_spruce.json",
                    "blockstates/book_shelf_open_double_spruce.json",
                    "blockstates/book_shelf_open_minimal_spruce.json",
                    "blockstates/book_shelf_with_lanterns_spruce.json",
                    "blockstates/dual_table_bottom_left_spruce.json",
                    "blockstates/dual_table_bottom_right_spruce.json",
                    "blockstates/dual_table_top_left_spruce.json",
                    "blockstates/dual_table_top_right_spruce.json",
                    "blockstates/potion_shelf_spruce.json",
                    "blockstates/simple_table_spruce.json",
                    "blockstates/tool_rack_double_spruce.json",
                    "blockstates/tool_rack_framed_spruce.json",
                    "blockstates/tool_rack_pframed_spruce.json",
                    "blockstates/tool_rack_single_spruce.json",
                    "models/block/book_shelf_double_spruce.json",
                    "models/block/book_shelf_minimal_spruce.json",
                    "models/block/book_shelf_open_double_spruce.json",
                    "models/block/book_shelf_open_minimal_spruce.json",
                    "models/block/book_shelf_with_lit_lanterns_spruce.json",
                    "models/block/book_shelf_with_unlit_lanterns_spruce.json",
                    "models/block/dual_rack_bottom_spruce.json",
                    "models/block/dual_rack_top_spruce.json",
                    "models/block/dual_table_part_bottom_left_spruce.json",
                    "models/block/dual_table_part_bottom_right_spruce.json",
                    "models/block/dual_table_part_top_left_spruce.json",
                    "models/block/dual_table_part_top_left2_spruce.json",
                    "models/block/dual_table_part_top_left3_spruce.json",
                    "models/block/dual_table_part_top_right_spruce.json",
                    "models/block/dual_table_part_top_right2_spruce.json",
                    "models/block/dual_table_part_top_right3_spruce.json",
                    "models/block/framed_rack_full_bottom_spruce.json",
                    "models/block/framed_rack_full_top_spruce.json",
                    "models/block/framed_rack_hollow_bottom_spruce.json",
                    "models/block/framed_rack_hollow_top_spruce.json",
                    "models/block/mini_rack_spruce.json",
                    "models/block/potion_shelf_spruce.json",
                    "models/block/simple_table_spruce.json",
                    "models/block/simple_table_with_drawer_spruce.json",
                    "models/item/book_shelf_double_spruce.json",
                    "models/item/book_shelf_minimal_spruce.json",
                    "models/item/book_shelf_open_double_spruce.json",
                    "models/item/book_shelf_open_minimal_spruce.json",
                    "models/item/book_shelf_with_lanterns_spruce.json",
                    "models/item/potion_shelf_spruce.json",
                    "models/item/simple_table_spruce.json",
                    "models/item/tool_rack_double_spruce.json",
                    "models/item/tool_rack_framed_spruce.json",
                    "models/item/tool_rack_pframed_spruce.json",
                    "models/item/tool_rack_single_spruce.json",
                    "models/item/workstation_placer_spruce.json",
                    "blockstates/tetra_table_spruce.json",
                    "models/block/tetra_table_spruce.json",
                    "models/item/tetra_table_spruce.json"
            };


    @Override
    @SuppressWarnings("unchecked")
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException
    {
        if (deserializer == LanguageMetadataSection.SERIALIZER)
        {
            return (T) new LanguageMetadataSection(List.of(new LanguageInfo("en_us", "US", "English", false)));
        }
        return super.getMetadataSection(deserializer);
    }

    @Override
    public String getName() { return "Workshop - auto-generated client assets"; }
}
