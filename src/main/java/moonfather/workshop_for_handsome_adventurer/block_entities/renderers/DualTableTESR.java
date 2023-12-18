package moonfather.workshop_for_handsome_adventurer.block_entities.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import moonfather.workshop_for_handsome_adventurer.ClientConfig;
import moonfather.workshop_for_handsome_adventurer.block_entities.DualTableBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DualTableTESR implements BlockEntityRenderer<DualTableBlockEntity>
{
    public DualTableTESR(BlockEntityRendererProvider.Context context) { }


    @Override
    public void render(DualTableBlockEntity table, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay)
    {
        Direction direction = table.getDirection(); // no rotation in case of dual tables.
        SimpleTableTESR.render3x3(poseStack, direction, bufferSource, combinedLight, combinedOverlay, table, 0, true, false);
        SimpleTableTESR.render3x3(poseStack, direction, bufferSource, combinedLight, combinedOverlay, table, 3*3+4, true, true);
    }



    @Override
    public boolean shouldRender(DualTableBlockEntity blockEntity, Vec3 location)
    {
        if (blockEntity.hasLevel() && blockEntity.getLevel().getLevelData().getGameTime() % 40 == 7)
        {
            this.shouldRender = ClientConfig.RenderItemsOnTable.get();
        }
        return this.shouldRender;
    }
    private boolean shouldRender = true;


    // run TESR even if main block isn't visible.
    @Override
    public AABB getRenderBoundingBox(DualTableBlockEntity blockEntity)
    {
        return blockEntity.getRenderBoundingBox();
    }
}
