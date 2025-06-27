package moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moonfather.workshop_for_handsome_adventurer.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.HashMap;
import java.util.Map;

// because it appears no one ever did this. or needed this.
// even though i'd expect it to just be available.
public class SpecialFirstEverRenderer
{
    public static void render(String image, float xInPixels, float yInPixelsFromTop, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight, int combinedOverlay, int lightColor)
    {
        if (sprites.size() == 0)
        {
            sprites.put("e", Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.fromNamespaceAndPath(Constants.MODID, "gui/task_list_check1")));
            sprites.put("y", Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.fromNamespaceAndPath(Constants.MODID, "gui/task_list_check2")));
            sprites.put("n", Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.fromNamespaceAndPath(Constants.MODID, "gui/task_list_check3")));
            sprites.put("q", Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.fromNamespaceAndPath(Constants.MODID, "gui/task_list_check4")));
        }
        TextureAtlasSprite sprite = sprites.get(image);
        poseStack.pushPose();
        poseStack.scale(1.875f, 1.3125f, 1.0f);
        float yScaleSpecial = 160f; // special is a fancy way of saying i'm adjusting things by hand
        float xScaleSpecial = 160f; // special is a fancy way of saying i'm adjusting things by hand
        poseStack.translate(-4 + xInPixels * 1/16f * xScaleSpecial,  -4 + yInPixelsFromTop * 1/16f * yScaleSpecial, 0.5);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.cutoutMipped());

        int w = 13, h = 13;
        vertex(poseStack.last(), vertexConsumer,  0, 16, sprite.getU0(), sprite.getV1(), combinedLight);
        vertex(poseStack.last(), vertexConsumer, 16, 16, sprite.getU1(), sprite.getV1(), combinedLight);
        vertex(poseStack.last(), vertexConsumer, 16,  0, sprite.getU1(), sprite.getV0(), combinedLight);
        vertex(poseStack.last(), vertexConsumer,  0,  0, sprite.getU0(), sprite.getV0(), combinedLight);

        poseStack.popPose();
    }
    private static final Map<String, TextureAtlasSprite> sprites = new HashMap<>(4);

    private static void vertex(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            float x,
            float y,
            float u,
            float v,
            int packedLight
    )
    {
        vertex(pose,consumer, x, y, u, v, -0.03125F, 0, 0, -1, packedLight);
    }
    private static void vertex(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            float x,
            float y,
            float u,
            float v,
            float z,
            int normalX,
            int normalY,
            int normalZ,
            int packedLight
    ) {
        consumer.addVertex(pose, x, y, z)
                .setColor(-1)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, (float)normalX, (float)normalY, (float)normalZ);
    }
}
