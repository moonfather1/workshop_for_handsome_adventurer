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
import java.util.HashMap;
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
        String json;
        for (String spruceFile: files)
        {
            json = AssetReader.getInstance(PackType.CLIENT_RESOURCES, Constants.MODID).getText(new ResourceLocation(Constants.MODID, spruceFile));
            if (json != null)
            {
                for (String wood: WoodTypeLister.getWoodIds())
                {
                    String replaced = json
                        .replace(SPRUCE_PLANKS, getPlanks(wood))
                        .replace(SPRUCE_LOG, getStrippedLog(wood))
                        .replace(SPRUCE, wood);
                    if (WoodTypeLister.isUsingDarkerWorkstation(wood))
                    {
                        replaced = replaced.replace("/stripped_dark_oak_log", "/stripped_spruce_log");
                    }
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
                            //used to be a simple   builder.append(line.replace(SPRUCE, wood));     ...here...
                            String replacement;
                            if (! wood.contains("_"))
                            {
                                replacement = line.replace(SPRUCE, wood);
                            }
                            else
                            {
                                String[] temp = line.split(":", 2);  // translation is difficult. this replace call is a trivial version.
                                replacement = temp[0].replace(SPRUCE, wood) + ':' + temp[1].replace(SPRUCE, wood.replace('_', ' '));
                            }
                            builder.append(replacement);
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



    private String getPlanks(String wood)
    {
        String template = WoodTypeLister.getTexture1Template(wood);
        if (template == null)
        {
            return TEMPLATE_PLANKS.formatted(WoodTypeLister.getHostMod(wood), wood);
        }
        return template.formatted(WoodTypeLister.getHostMod(wood), wood);
    }

    private String getStrippedLog(String wood)
    {
        if (strippedLogCache.containsKey(wood))
        {
            return strippedLogCache.get(wood);
        }
        String result;
        String sub = WoodTypeLister.getLogRecipeSubstitute(wood);
        if (sub == null)
        {
            String template = WoodTypeLister.getTexture2Template(wood);
            if (template != null)
            {
                result = template.formatted(WoodTypeLister.getHostMod(wood), wood);
            }
            else
            {
                result = TEMPLATE_LOG.formatted(WoodTypeLister.getHostMod(wood), wood);
            }
        }
        else
        {
            ResourceLocation rl = new ResourceLocation(sub);
            String namespace = rl.getNamespace(), path = rl.getPath();
            String sub2 = WoodTypeLister.getLogTextureSubstitute(wood);
            if (sub2 != null)
            {
                if (sub2.contains(":"))
                {
                    ResourceLocation rl2 = new ResourceLocation(sub2);
                    namespace = rl2.getNamespace();
                    path = rl2.getPath();
                }
                else
                {
                    namespace = WoodTypeLister.getHostMod(wood);
                    path = sub2;
                }
            }
            String template = WoodTypeLister.getTexture2TemplateForMod(rl.getNamespace());
            if (template == null)
            {
                template = TEMPLATE_ANY_BLOCK;
            }
            result = template.formatted(namespace, path);
        }
        strippedLogCache.put(wood, result);
        return result;
    }
    private final Map<String, String> strippedLogCache = new HashMap<>(); // will be remade on reload
    private static final String TEMPLATE_PLANKS = "%s:block/%s_planks";
    private static final String TEMPLATE_LOG = "%s:block/stripped_%s_log";
    private static final String TEMPLATE_ANY_BLOCK = "%s:block/%s";



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
        if (deserializer == LanguageMetadataSection.TYPE)
        {
            return (T) new LanguageMetadataSection(Map.of( "en_us", new LanguageInfo("US", "English", false)));
        }
        return super.getMetadataSection(deserializer);
    }

    @Override
    public String packId() { return "Workshop - auto-generated client assets"; }
}
