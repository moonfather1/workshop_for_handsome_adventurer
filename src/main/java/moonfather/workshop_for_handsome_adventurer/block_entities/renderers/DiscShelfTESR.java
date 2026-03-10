package moonfather.workshop_for_handsome_adventurer.block_entities.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import moonfather.workshop_for_handsome_adventurer.block_entities.ToolRackBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec3;

public class DiscShelfTESR extends ToolRackTESR
{
    public DiscShelfTESR(BlockEntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public void render(ToolRackBlockEntity tile, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        Direction direction = tile.getBlockState().getValue(HorizontalDirectionalBlock.FACING).getOpposite();
        Direction itemDirection = direction.getCounterClockWise();

        int itemsPerRow = tile.getNumberOfItemsInOneRow();
        int rows = tile.getNumberOfItems() / tile.getNumberOfItemsInOneRow();
        matrixStack.pushPose();
        matrixStack.translate(0.5 - direction.getStepX() * 0.42, 0.7, 0.5 - direction.getStepZ() * 0.42);
        matrixStack.scale(0.29f, 0.34f, 0.29f);
        double vertical = 2/16d;  // for row 0;
        for (int row = 0; row < rows; row++)
        {
            if (row >= 1) { vertical += 120/160d;}
            for (int i = 0; i < itemsPerRow; i++)
            {
                ItemStack itemStack = tile.GetItem(row * itemsPerRow + i);
                if (! itemStack.isEmpty())
                {
                    matrixStack.pushPose();
                    double antiZFighting = row * 0.003d + i * 0.001d;
                    matrixStack.translate(itemDirection.getStepX() * (i - 1d) + antiZFighting, 0 - vertical, itemDirection.getStepZ() * (i - 1d) + antiZFighting);
                    matrixStack.mulPose(direction.getRotation());
                    matrixStack.scale(1f, 1.4f, 1f);  // thicker
                    double rowExtrude = +0.225d * row - 0.1d;   if (row == 2) rowExtrude += 0.025;
                    matrixStack.translate(0, rowExtrude, 0);
                    renderItemStack(tile, itemStack, matrixStack, buffer, combinedLight, combinedOverlay);
                    matrixStack.popPose();
                }
            }
        }
        matrixStack.popPose();
    }



    private void renderItemStack(ToolRackBlockEntity tile, ItemStack itemStack, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        if (itemStack == null || itemStack.isEmpty())
        {
            return;
        }
        int renderId = (int) tile.getBlockPos().asLong();

        matrixStack.mulPose(XMinus90);  // 1.19.4   Vector3f.XP.rotationDegrees(-90.0F)
        matrixStack.mulPose(YPlus180);  // 1.19.4   Vector3f.YP.rotationDegrees(180.0F)

        if (itemStack.is(TAG_LARGER_ON_TOOLRACK_150)) // first this. apply the rest on top of this.
        {
            matrixStack.scale(1.50f, 1.10f, 1.50f); // seems to be noo need for matrixStack.translate
        }
        else if (itemStack.is(TAG_LARGER_ON_TOOLRACK_125))
        {
            matrixStack.scale(1.25f, 1.10f, 1.25f);  // seems to be noo need for matrixStack.translate
        }
        matrixStack.translate(0, 0.1, 0);
        Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, matrixStack, buffer, tile.getLevel(), renderId);
    }
}
