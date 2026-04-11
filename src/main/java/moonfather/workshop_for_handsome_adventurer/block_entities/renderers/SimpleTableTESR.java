package moonfather.workshop_for_handsome_adventurer.block_entities.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import moonfather.workshop_for_handsome_adventurer.ClientConfig;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;

public class SimpleTableTESR implements BlockEntityRenderer<SimpleTableBlockEntity, SimpleTableTESR.ItemHoldingBlockRenderState>
{
    private static final Quaternionf XPlus90 = new Quaternionf().fromAxisAngleDeg(1, 0, 0, 90);
    private static final Quaternionf ZPlus180 = new Quaternionf().fromAxisAngleDeg(0, 0, 1, 180);
    private static final Map<Integer, Quaternionf> YRotCache = new HashMap<>();



    private static Quaternionf getYRotation(Direction directionToPlayer, boolean invert)
    {
        int one = invert ? -1 : 1;
        int y = (int) directionToPlayer.toYRot() * one;
        Quaternionf result = YRotCache.getOrDefault(y, null);
        if (result == null)
        {
            result = new Quaternionf().fromAxisAngleDeg(0, 1, 0, y);
            YRotCache.put(y, result);
        }
        return result;
    }



    @Override
    public boolean shouldRender(SimpleTableBlockEntity blockEntity, Vec3 location)
    {
        if (blockEntity.hasLevel() && blockEntity.getLevel().getLevelData().getGameTime() % 40 == 7)
        {
            this.shouldRender = ClientConfig.renderItemsOnTable;
        }
        return this.shouldRender;
    }
    private boolean shouldRender = true;

    //////////////////////////////////////////////

    public static class ItemHoldingBlockRenderState extends BlockEntityRenderState
    {
        public ItemStackRenderState[] items = new ItemStackRenderState[24];
        public Direction direction = Direction.NORTH;
    }

    @Override
    public ItemHoldingBlockRenderState createRenderState()
    {
        // Create the render state used to submit the block entity to the feature renderer
        return new ItemHoldingBlockRenderState();
    }

    @Override
    public void extractRenderState(SimpleTableBlockEntity blockEntity, ItemHoldingBlockRenderState renderState, float partialTick, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress)
    {
        // Extract the necessary rendering values from the block entity to the render state
        // Always call super or BlockEntityRenderState#extractBase
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        // Populate extended values
        fillRenderState(blockEntity, renderState, this.itemModelResolver);
    }

    public static void fillRenderState(SimpleTableBlockEntity blockEntity, ItemHoldingBlockRenderState renderState, ItemModelResolver itemModelResolver) {
        int seedBase = Long.valueOf(blockEntity.getBlockPos().asLong()).hashCode();
        for (int i = 0; i < blockEntity.getCapacity(); i++)
        {
            ItemStack itemstack = blockEntity.GetItem(i);
            if (! itemstack.isEmpty())
            {
                ItemStackRenderState itemstackrenderstate = new ItemStackRenderState();
                itemModelResolver.updateForTopItem(itemstackrenderstate, ToolRackTESR.removeEnchantmentsStatic(itemstack), ItemDisplayContext.FIXED, blockEntity.getLevel(), null, seedBase + i);
                renderState.items[i] = itemstackrenderstate;
            }
            else
            {
                renderState.items[i] = null;
            }
        }
    }

    @Override
    public void submit(ItemHoldingBlockRenderState tableRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState)
    {
        if (Minecraft.getInstance().player == null) { return; }
        Direction direction = null;
        double playerDX = tableRenderState.blockPos.getX() + 0.5d - Minecraft.getInstance().player.position().x;
        double playerDZ = tableRenderState.blockPos.getZ() + 0.5d - Minecraft.getInstance().player.position().z;
        if ((Math.abs(playerDZ) > 1e-4 && Math.abs(Math.abs(playerDX / playerDZ) - 1) < 0.2) // player is diagonally positioned compared to the table
                || (tableRenderState.blockPos.getX() == Minecraft.getInstance().player.blockPosition().getX() && tableRenderState.blockPos.getZ() == Minecraft.getInstance().player.blockPosition().getZ())) // player is right on top of the table
        {
            // special option: direction is dependent on where the player looks
            direction = Direction.fromYRot(Minecraft.getInstance().player.yHeadRot + 180);
        }
        else
        {
            // normal option: direction is dependent only on player position
            direction = Direction.getNearest(-1 * (int)playerDX, 0, -1 * (int)playerDZ, null);
            if (direction == null)
            {
                direction = Direction.fromYRot(Minecraft.getInstance().player.yHeadRot + 180); // i don't really care for the value, just can't have null.
            }
        }
        // ok we have direction. now to draw...
        submit3x3(tableRenderState, poseStack, direction, submitNodeCollector, 0, false);
    }

    static void submit3x3(ItemHoldingBlockRenderState tableRenderState, PoseStack poseStack, Direction direction, SubmitNodeCollector nodeCollector, int tableInventoryOffset, boolean secondary)
    {
        for (int j = 0; j < 3*3; ++j)
        {
            ItemStackRenderState itemStackRenderState = tableRenderState.items[tableInventoryOffset + j];
            if (itemStackRenderState != null)
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
                itemStackRenderState.submit(poseStack, nodeCollector, tableRenderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                poseStack.popPose();
            }
        }
    }

    public SimpleTableTESR(BlockEntityRendererProvider.Context context)
    {
        // Get anything you need from the context
        this.itemModelResolver = context.itemModelResolver();
    }
    private final ItemModelResolver itemModelResolver;
}
