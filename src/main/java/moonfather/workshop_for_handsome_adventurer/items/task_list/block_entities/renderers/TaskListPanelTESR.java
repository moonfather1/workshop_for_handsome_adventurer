package moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import moonfather.workshop_for_handsome_adventurer.ClientConfig;
import moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities.TaskListBlockEntity;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListMessaging;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import javax.annotation.ParametersAreNonnullByDefault;
@ParametersAreNonnullByDefault
public class TaskListPanelTESR implements BlockEntityRenderer<TaskListBlockEntity>
{
    public TaskListPanelTESR(BlockEntityRendererProvider.Context context)
    {
    }

    @Override
    public void render(TaskListBlockEntity tile, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight, int combinedOverlay, Vec3 camera)
    {
        if (! ClientConfig.taskListItemsAreDrawnOnWall)
        {
            return;
        }
        // positioning first
        Direction direction = tile.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
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
        combinedLight = reduceLightBasedOnDirection(combinedLight, direction);
        normalTextLight = reduceLightBasedOnDirection(normalTextLight, direction);
        finishedTextLight = reduceLightBasedOnDirection(finishedTextLight, direction);
        // items
        final float hpixel = 17f, vpixel = 13f; // pixel size for fine scaling
        final float itemTextStart = 4.5f;
        TaskListMessaging.TaskPageDTO page = tile.getPageForDisplay();
        for (int i = 0; i < TaskListMessaging.ITEMS_PER_PAGE; i++)
        {
            int color = textColorMain;
            int light = normalTextLight;
            if (ClientConfig.taskListColoringForFinishedItems && (page.items().get(i).status().equals("y") || page.items().get(i).status().equals("n")))
            {
                color = textColorGrayed;
                light = finishedTextLight;
            }
            Minecraft.getInstance().font.drawInBatch(page.items().get(i).line1(), itemTextStart * hpixel,  (3 + 2*i) * vpixel, color, false, poseStack.last().pose(), multiBufferSource, Font.DisplayMode.NORMAL, 0, light);
            Minecraft.getInstance().font.drawInBatch(page.items().get(i).line2(), itemTextStart * hpixel,  (3 + 2*i+1) * vpixel, color, false, poseStack.last().pose(), multiBufferSource, Font.DisplayMode.NORMAL, 0, light);
        }
        // footer
        Minecraft.getInstance().font.drawInBatch("<<", 3*hpixel, 15*vpixel, textColorMain, false, poseStack.last().pose(), multiBufferSource, Font.DisplayMode.NORMAL, 0, normalTextLight);
        Minecraft.getInstance().font.drawInBatch(tile.getFooter(), 8*hpixel, 15*vpixel, textColorMain, false, poseStack.last().pose(), multiBufferSource, Font.DisplayMode.NORMAL, 0, normalTextLight); // used to be 0x510051 in these three
        Minecraft.getInstance().font.drawInBatch(">>", 13.5f*hpixel, 15*vpixel, textColorMain, false, poseStack.last().pose(), multiBufferSource, Font.DisplayMode.NORMAL, 0, normalTextLight);
        // checkboxes
        int lightColor = tile.getLevel() != null ? LevelRenderer.getLightColor(tile.getLevel(), tile.getBlockPos()) : -1;
        for (int i = 0; i < TaskListMessaging.ITEMS_PER_PAGE; i++)
        {
            //Minecraft.getInstance().font.drawInBatch(page.items().get(i).status(), 3 * hpixel,  (3 + 2*i + 0.5f) * vpixel, color, false, poseStack.last().pose(), multiBufferSource, Font.DisplayMode.NORMAL, 0, light);
            SpecialFirstEverRenderer.render(page.items().get(i).status(), 3f, 3 + 2*i + 0.5f, poseStack, multiBufferSource, combinedLight, combinedOverlay, lightColor);
        }
        // hor lines
        float scaleX = 32;
        poseStack.scale(scaleX, 1, 0.25f);
        float intendedStart = 3.5f; // in pixels
        float start = (0) * intendedStart + 0.115f;
        for (int i = 0; i < 5; i++)
        {
            Minecraft.getInstance().font.drawInBatch("_", start * hpixel,  (4 + 2*i + 0.2f) * vpixel, textColorDividers, false, poseStack.last().pose(), multiBufferSource, Font.DisplayMode.NORMAL, 0, 0x220022);
        }
        // and done
        poseStack.popPose();
    }



    private static final Quaternionf XPlus180 = new Quaternionf().fromAxisAngleDeg(1, 0, 0, 180);
    private static final Quaternionf YPlus180 = new Quaternionf().fromAxisAngleDeg(0, 1, 0, 180);
    private static final Quaternionf YPlus090 = new Quaternionf().fromAxisAngleDeg(0, 1, 0,  90);
    private static final Quaternionf YPlus270 = new Quaternionf().fromAxisAngleDeg(0, 1, 0, 270);
    private static final Quaternionf ZPlus180 = new Quaternionf().fromAxisAngleDeg(0, 0, 1, 180);
    private static final int textColorMain = 0xFF444455 ; // was ok with 0x111122, 0x444433  0xee00ee
    private static final int textColorGrayed = 0xFF858585;
    private static final int textColorDividers = 0xFF9a9a95;



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
}
