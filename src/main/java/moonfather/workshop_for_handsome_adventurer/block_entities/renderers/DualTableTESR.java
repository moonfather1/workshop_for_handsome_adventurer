package moonfather.workshop_for_handsome_adventurer.block_entities.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import moonfather.workshop_for_handsome_adventurer.ClientConfig;
import moonfather.workshop_for_handsome_adventurer.block_entities.DualTableBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class DualTableTESR implements BlockEntityRenderer<DualTableBlockEntity, SimpleTableTESR.ItemHoldingBlockRenderState>
{
    @Override
    public SimpleTableTESR.ItemHoldingBlockRenderState createRenderState()
    {
        return new SimpleTableTESR.ItemHoldingBlockRenderState();
    }

    @Override
    public void extractRenderState(DualTableBlockEntity blockEntity, SimpleTableTESR.ItemHoldingBlockRenderState renderState, float partialTick, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress)
    {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        renderState.direction = blockEntity.getDirection();
        SimpleTableTESR.fillRenderState(blockEntity, renderState, this.itemModelResolver);
    }

    @Override
    public void submit(SimpleTableTESR.ItemHoldingBlockRenderState tableRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState)
    {
        Direction direction = tableRenderState.direction; // no rotation in case of dual tables.
        SimpleTableTESR.submit3x3(tableRenderState, poseStack, direction, submitNodeCollector, 0, false);
        SimpleTableTESR.submit3x3(tableRenderState, poseStack, direction, submitNodeCollector, 3*3+4, true);
    }


    public DualTableTESR(BlockEntityRendererProvider.Context context)
    {
        this.itemModelResolver = context.itemModelResolver();
    }
    private final ItemModelResolver itemModelResolver;

    ////////////////////////////////////

    @Override
    public boolean shouldRender(DualTableBlockEntity blockEntity, Vec3 location)
    {
        if (blockEntity.hasLevel() && blockEntity.getLevel().getLevelData().getGameTime() % 40 == 7)
        {
            this.shouldRender = ClientConfig.renderItemsOnTable;
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
