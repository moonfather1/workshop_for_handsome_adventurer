package moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import moonfather.workshop_for_handsome_adventurer.ClientConfig;
import moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities.TaskListBlockEntity;
import moonfather.workshop_for_handsome_adventurer.items.task_list.blocks.TaskListPanel;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListMessaging;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import javax.annotation.ParametersAreNonnullByDefault;
@ParametersAreNonnullByDefault
public class TaskListPanelTESR implements BlockEntityRenderer<TaskListBlockEntity, BlockEntityRenderState>
{
    public TaskListPanelTESR(BlockEntityRendererProvider.Context context)
    {
    }



    private static final Quaternionf XPlus180 = new Quaternionf().fromAxisAngleDeg(1, 0, 0, 180);
    private static final Quaternionf YPlus180 = new Quaternionf().fromAxisAngleDeg(0, 1, 0, 180);
    private static final Quaternionf YPlus090 = new Quaternionf().fromAxisAngleDeg(0, 1, 0,  90);
    private static final Quaternionf YPlus270 = new Quaternionf().fromAxisAngleDeg(0, 1, 0, 270);
    private static final Quaternionf ZPlus180 = new Quaternionf().fromAxisAngleDeg(0, 0, 1, 180);
    private static final int textColorMain = 0xFF444455 ; // was ok with 0x111122, 0x444433  0xee00ee
    private static final int textColorGrayed = 0xFF858585;
    private static final int textColorDividers = 0xFF9a9a95;
    private static final FormattedCharSequence NAVI_NEXT = FormattedCharSequence.forward(">>", Style.EMPTY);
    private static final FormattedCharSequence NAVI_PREV = FormattedCharSequence.forward("<<", Style.EMPTY);



    private int reduceLightBasedOnDirection(int originalLight, Direction direction)
    {
        int mul;
        if (direction.equals(Direction.NORTH) || direction.equals(Direction.SOUTH))
        {
            mul = 80; // i could just return here to leave it at 100/100, but it looks better at 80/100
        }
        else
        {
            mul = 65; // fighting mojang's stupidity.
        }
        int byteValue = (originalLight >> 16) & 0xFF;
        byteValue = byteValue * mul / 100;
        int result = byteValue;
        byteValue = (originalLight >> 8) & 0xFF;
        byteValue = byteValue * mul / 100;
        result = (result << 8) + byteValue;
        byteValue = (originalLight) & 0xFF;
        byteValue = byteValue * mul / 100;
        result = (result << 8) + byteValue;
        result = result | (originalLight & 0xFF000000);     // opacity needed as of 1.21.8
        return result;
    }

    /////////////////////////////


    @Override
    public void extractRenderState(TaskListBlockEntity blockEntity, BlockEntityRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress)
    {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        TaskPageRenderState state = (TaskPageRenderState) renderState;
        state.direction = blockEntity.getBlockState().getValue(TaskListPanel.FACING);
        state.page = RenderStateManagement.extract(blockEntity);
        state.lightColor = blockEntity.getLevel() != null ? LightCoordsUtil.getLightCoords(blockEntity.getLevel(), blockEntity.getBlockPos()) : -1;
    }

    @Override
    public BlockEntityRenderState createRenderState()
    {
        return new TaskPageRenderState();
    }

    @Override
    public void submit(BlockEntityRenderState blockEntityRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState)
    {
        if (! ClientConfig.taskListItemsAreDrawnOnWall)
        {
            return;
        }
        TaskPageRenderState renderState = (TaskPageRenderState) blockEntityRenderState;
        // positioning first
        Direction direction = renderState.direction;
        poseStack.pushPose();
        if (direction.equals(Direction.NORTH)) {
            poseStack.mulPose(ZPlus180);
            poseStack.translate(-1f, -1f, +148/160f);
        }
        if (direction.equals(Direction.EAST))
        {
            poseStack.mulPose(XPlus180);
            poseStack.mulPose(YPlus270);
            poseStack.translate(-1f, -1f, +-1+149/160f);
        }
        if (direction.equals(Direction.SOUTH))
        {
            poseStack.mulPose(ZPlus180);
            poseStack.mulPose(YPlus180);
            poseStack.translate(0f, -1f, -1 + 148/160f);
        }
        if (direction.equals(Direction.WEST))
        {
            poseStack.mulPose(XPlus180);
            poseStack.mulPose(YPlus090);
            poseStack.translate(0f, -1f, +0+149/160f);
        }
        poseStack.scale(0.0035f, 0.0045f, 0.0050f);
        // adjust lighting based on direction
        int normalTextLight = 0x410041,  finishedTextLight = 0x410041; // packed light coordinates
        int combinedLight = reduceLightBasedOnDirection(renderState.lightColor, direction);
        normalTextLight = reduceLightBasedOnDirection(normalTextLight, direction);
        finishedTextLight = reduceLightBasedOnDirection(finishedTextLight, direction);
        // items
        final float hpixel = 17f, vpixel = 13f; // pixel size for fine scaling
        final float itemTextStart = 4.9f;
        for (int i = 0; i < TaskListMessaging.ITEMS_PER_PAGE; i++)
        {
            int color = textColorMain;
            int light = normalTextLight;
            if (ClientConfig.taskListColoringForFinishedItems && (renderState.page.items()[i].status().equals("y") || renderState.page.items()[i].status().equals("n")))
            {
                color = textColorGrayed;
                light = finishedTextLight;
            }
            submitNodeCollector.submitText(poseStack, itemTextStart * hpixel, (3 + 2*i) * vpixel, renderState.page.items()[i].line1(), false, Font.DisplayMode.NORMAL, light, color, 0, 0);
            submitNodeCollector.submitText(poseStack, itemTextStart * hpixel, (3 + 2*i+1) * vpixel, renderState.page.items()[i].line2(), false, Font.DisplayMode.NORMAL, light, color, 0, 0);
            // wasted so much tie on this shitty new system...  it just calls  font.drawInBatch() anyway...
        }
        // footer
        submitNodeCollector.submitText(poseStack, 3*hpixel, 15*vpixel, NAVI_PREV, false, Font.DisplayMode.NORMAL, normalTextLight, textColorMain, 0, 0);
        submitNodeCollector.submitText(poseStack, 8*hpixel, 15*vpixel, renderState.page.footer(), false, Font.DisplayMode.NORMAL, normalTextLight, textColorMain, 0, 0);
        submitNodeCollector.submitText(poseStack, 13.5f*hpixel, 15*vpixel, NAVI_NEXT, false, Font.DisplayMode.NORMAL, normalTextLight, textColorMain, 0, 0);
        // checkboxes
        for (int i = 0; i < TaskListMessaging.ITEMS_PER_PAGE; i++)
        {
            int finalI = i;
            submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.cutoutMovingBlock(), (pose, vertexConsumer) -> SpecialFirstEverRenderer.render(renderState.page.items()[finalI].status(), 3f, 3 + 2 * finalI + 0.5f, pose, vertexConsumer, combinedLight));
        }
        // hor lines
        float scaleX = 32;
        poseStack.scale(scaleX, 1, 0.25f);
        float intendedStart = 3.5f; // in pixels
        float start = (0) * intendedStart + 0.115f;
        for (int i = 0; i < 5; i++)
        {
            submitNodeCollector.submitText(poseStack, start * hpixel, (4 + 2*i + 0.2f) * vpixel, FormattedCharSequence.forward("_", Style.EMPTY), false, Font.DisplayMode.NORMAL, 0x220022, textColorDividers, 0, 0);
        }
        // and done
        poseStack.popPose();

    }

    private static class TaskPageRenderState extends BlockEntityRenderState
    {
        private Direction direction;
        private RenderStateManagement.Page page;
        private int lightColor;
    }
}
