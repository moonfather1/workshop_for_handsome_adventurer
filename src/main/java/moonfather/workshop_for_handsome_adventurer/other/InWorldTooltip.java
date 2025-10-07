package moonfather.workshop_for_handsome_adventurer.other;

import moonfather.workshop_for_handsome_adventurer.block_entities.BaseContainerBlockEntity;
import moonfather.workshop_for_handsome_adventurer.block_entities.ToolRackBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.BookShelf;
import moonfather.workshop_for_handsome_adventurer.blocks.PotionShelf;
import moonfather.workshop_for_handsome_adventurer.blocks.ToolRack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.gui.GuiLayer;
import org.jetbrains.annotations.NotNull;

//public class InWorldTooltip implements LayeredDraw.Layer
public class InWorldTooltip implements GuiLayer
{
    private static final InWorldTooltip instance = new InWorldTooltip();
    public static InWorldTooltip getInstance() { return instance; }



    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker)
    {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null || Minecraft.getInstance().screen != null) { return; }
        if (Minecraft.getInstance().hitResult instanceof BlockHitResult hitResult)
        {
            if (hitResult.getType().equals(HitResult.Type.BLOCK)) // no longer needed?
            {
                BlockState blockState = Minecraft.getInstance().level.getBlockState(hitResult.getBlockPos());
                //guiGraphics.drawString(Minecraft.getInstance().font, "posi %f, %f, %f".formatted(hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z), 100, 200, 0xffee55);
                //guiGraphics.drawString(Minecraft.getInstance().font, "win wi" + Minecraft.getInstance().getWindow().getWidth() + ", swi " + Minecraft.getInstance().getWindow().getScreenWidth() + ", sca " + Minecraft.getInstance().getWindow().getGuiScaledWidth() , 100, 182, 0xcc66ff);
                int slot = -5;  boolean above = false;
                if (blockState.getBlock() instanceof PotionShelf)
                {
                    if (! blockState.getValue(PotionShelf.FACING).equals(hitResult.getDirection().getOpposite())) { return; }
                    slot = PotionShelf.getPotionShelfSlot(hitResult);
                    //guiGraphics.drawString(Minecraft.getInstance().font, "pshelf slot " + slot, 100, 164, 0xccff55);
                }
                else if (blockState.getBlock() instanceof BookShelf bookShelf)
                {
                    if (! blockState.getValue(BookShelf.FACING).equals(hitResult.getDirection().getOpposite())) { return; }
                    slot = BookShelf.getBookShelfSlot(bookShelf, hitResult);
                    //guiGraphics.drawString(Minecraft.getInstance().font, "bshelf slot " + slot, 100, 164, 0xccff55);
                }
                else if (blockState.getBlock() instanceof ToolRack toolRack)
                {
                    if (! blockState.getValue(BookShelf.FACING).equals(hitResult.getDirection().getOpposite())) { return; }
                    if (Minecraft.getInstance().level.getBlockEntity(hitResult.getBlockPos()) != null)
                    {
                        slot = ToolRack.getToolRackSlot(toolRack, hitResult);
                    }
                    else
                    {
                        above = true;
                        slot = ToolRack.getToolRackSlot(toolRack, new BlockHitResult(hitResult.getLocation(), hitResult.getDirection(), hitResult.getBlockPos().above(), false));
                    }
                    //guiGraphics.drawString(Minecraft.getInstance().font, "tr slot " + slot, 100, 164, 0xccff55);
                }
                if (slot >= 0)
                {
                    BlockPos pos = above ? hitResult.getBlockPos().above() : hitResult.getBlockPos();
                    if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof  BaseContainerBlockEntity blockEntity)
                    {
                        if (! blockEntity.GetItem(slot).isEmpty())
                        {
                            guiGraphics.renderTooltip(Minecraft.getInstance().font, blockEntity.GetItem(slot), Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - 30, Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2 + 20, DefaultTooltipPositioner.INSTANCE, null);
                        }
                    }
                }
            }
        }
    }
}
