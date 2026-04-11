package moonfather.workshop_for_handsome_adventurer.block_entities.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;

public class DiscShelfTESR extends ToolRackTESR
{
    public DiscShelfTESR(BlockEntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public void submit(RackRenderState rackRenderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState)
    {
        Direction direction = rackRenderState.direction;
        Direction itemDirection = rackRenderState.direction.getCounterClockWise();
        int itemsPerRow = rackRenderState.numberOfItemsInOneRow;
        int rows = rackRenderState.numberOfItems / rackRenderState.numberOfItemsInOneRow;
        poseStack.pushPose();
        poseStack.translate(0.5 - direction.getStepX() * 0.42, 0.7, 0.5 - direction.getStepZ() * 0.42);
        poseStack.scale(0.29f, 0.34f, 0.29f);
        double vertical = 2/16d;  // for row 0;
        for (int row = 0; row < rows; row++)
        {
            if (row >= 1) { vertical += 120/160d;}
            for (int i = 0; i < itemsPerRow; i++)
            {
                ToolRackItemRenderState itemToRender = rackRenderState.items[row * itemsPerRow + i];
                if (itemToRender != null)
                {
                    poseStack.pushPose();
                    double antiZFighting = row * 0.003d + i * 0.001d;
                    poseStack.translate(itemDirection.getStepX() * (i - 1d) + antiZFighting, 0 - vertical, itemDirection.getStepZ() * (i - 1d) + antiZFighting);
                    poseStack.mulPose(direction.getRotation());
                    poseStack.scale(1f, 1.4f, 1f);  // thicker
                    double rowExtrude = +0.225d * row - 0.1d;   if (row == 2) rowExtrude += 0.025;
                    poseStack.translate(0, rowExtrude, 0);
                    doSpecialPoseStackAdjustments(itemToRender, poseStack);
                    itemToRender.submit(poseStack, nodeCollector, rackRenderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                    poseStack.popPose();
                }
            }
        }
        poseStack.popPose();
    }



    @Override
    protected void doSpecialPoseStackAdjustments(ToolRackItemRenderState state, PoseStack matrixStack)
    {
        matrixStack.mulPose(XMinus90);  // 1.19.4   Vector3f.XP.rotationDegrees(-90.0F)
        matrixStack.mulPose(YPlus180);  // 1.19.4   Vector3f.YP.rotationDegrees(180.0F)

        if (state.larger150) // first this. apply the rest on top of this.
        {
            matrixStack.scale(1.50f, 1.10f, 1.50f); // seems to be noo need for matrixStack.translate
        }
        else if (state.larger125)
        {
            matrixStack.scale(1.25f, 1.10f, 1.25f);  // seems to be noo need for matrixStack.translate
        }
        matrixStack.translate(0, 0.1, 0);
    }
}
