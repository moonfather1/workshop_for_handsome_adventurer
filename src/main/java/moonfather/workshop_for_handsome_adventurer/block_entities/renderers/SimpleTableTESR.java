package moonfather.workshop_for_handsome_adventurer.block_entities.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import moonfather.workshop_for_handsome_adventurer.OptionsHolder;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class SimpleTableTESR implements BlockEntityRenderer<SimpleTableBlockEntity>
{
    public SimpleTableTESR(BlockEntityRendererProvider.Context context)
    {
    }


    @Override
    public void render(SimpleTableBlockEntity table, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay)
    {
        Direction direction = null;
        double playerDX = table.getBlockPos().getX() + 0.5d - Minecraft.getInstance().player.position().x;
        double playerDZ = table.getBlockPos().getZ() + 0.5d - Minecraft.getInstance().player.position().z;
        if ((Math.abs(playerDZ) > 1e-4 && Math.abs(Math.abs(playerDX / playerDZ) - 1) < 0.2) // player is diagonally positioned compared to the table
                || (table.getBlockPos().getX() == Minecraft.getInstance().player.blockPosition().getX() && table.getBlockPos().getZ() == Minecraft.getInstance().player.blockPosition().getZ())) // player is right on top of the table
        {
            // special option: direction is dependent on where the player looks
            direction = Direction.fromYRot(Minecraft.getInstance().player.yHeadRot + 180);
        }
        else
        {
            // normal option: direction is dependent only on player position
            direction = Direction.getNearest(-1 * playerDX, 0, -1 * playerDZ);
        } // ok we have direction. now to draw...
        render3x3(poseStack, direction, bufferSource, combinedLight, combinedOverlay, table, 0, false, false);
    }

    public static void render3x3(PoseStack poseStack, Direction direction, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, SimpleTableBlockEntity table, int tableInventoryOffset, boolean dual, boolean secondary)
    {
        for (int j = 0; j < 3 * 3; ++j)
        {
            ItemStack itemstack = ToolRackTESR.RemoveEnchantments(table.GetItem(tableInventoryOffset + j));
            if (itemstack != ItemStack.EMPTY)
            {
                poseStack.pushPose();
                poseStack.translate(0, 1.01f, 0);   // on top
                int tableOffsetX = secondary ? direction.getStepX() : 0;
                int tableOffsetZ = secondary ? direction.getStepZ() : 0;
                poseStack.translate(0.5D + tableOffsetZ, 0, 0.5D - tableOffsetX); // center
                poseStack.mulPose(getYRotation(direction, direction.getStepZ() == 0));  // rotate towards player
                double positionScale = 0.63f;
                poseStack.translate(j % 3 * 0.3D * positionScale, 0, j / 3 * 0.3D * positionScale); // spread into grid
                poseStack.translate(-0.19D, 0, -0.19D); // center the grid
                poseStack.mulPose(XPlus90); // lay items horizontal
                poseStack.mulPose(ZPlus180); // lay items horizontal
                float itemScale = 0.15f;
                poseStack.scale(itemScale, itemScale, itemScale / 3); // last part flattens them a little. i don't know how else to deal with blocks
                Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, j);
                poseStack.popPose();
            }
        }
    }

    private static final Quaternion XPlus90 = Vector3f.XP.rotationDegrees(90.0F);
    private static final Quaternion ZPlus180 = Vector3f.ZP.rotationDegrees(180.0F);
    private static final Map<Integer, Quaternion> YRotCache = new HashMap();


    private static Quaternion getYRotation(Direction directionToPlayer, boolean invert)
    {
        int one = invert ? -1 : 1;
        int y = (int) directionToPlayer.toYRot() * one;
        Quaternion result = YRotCache.getOrDefault(y, null);
        if (result == null)
        {
            result = Vector3f.YP.rotationDegrees(y);
            YRotCache.put(y, result);
        }
        return result;
    }


    @Override
    public boolean shouldRender(SimpleTableBlockEntity blockEntity, Vec3 location)
    {
        if (blockEntity.hasLevel() && blockEntity.getLevel().getLevelData().getGameTime() % 40 == 7)
        {
            this.shouldRender = OptionsHolder.CLIENT.RenderItemsOnTable.get();
        }
        return this.shouldRender;
    }

    private boolean shouldRender = true;
}
