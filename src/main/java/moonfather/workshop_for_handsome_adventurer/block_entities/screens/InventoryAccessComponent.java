package moonfather.workshop_for_handsome_adventurer.block_entities.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import moonfather.workshop_for_handsome_adventurer.block_entities.messaging.PacketSender;
import moonfather.workshop_for_handsome_adventurer.block_entities.screen_components.SimpleButton;
import moonfather.workshop_for_handsome_adventurer.block_entities.screen_components.SlightlyNicerEditBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class InventoryAccessComponent extends GuiComponent implements Widget, GuiEventListener, NarratableEntry
{
    public static final int PANEL_WIDTH = 176;
    public static final int PANEL_HEIGHT_WITHOUT_TABS = 134;
    public static final int PANEL_HEIGHT_WITH_TABS = 166;
    protected static final ResourceLocation BG_CHEST_LOCATION_3_ROWS = new ResourceLocation("workshop_for_handsome_adventurer:textures/gui/left_panel_normal_chest.png");
    protected static final ResourceLocation BG_CHEST_LOCATION_6_ROWS = new ResourceLocation("workshop_for_handsome_adventurer:textures/gui/left_panel_double_chest.png");
    private final String renameTooltipKey = "message.workshop_for_handsome_adventurer.rename";

    private int xOffset;
    private final List<TabButton> tabButtons = Lists.newArrayList();
    private TabButton selectedTab;
    private EditBox renameBox;
    private SimpleButton renameButton;
    private boolean visible;
    private int tickCount = 0;
    private boolean widthTooNarrow2 = false;
    private SimpleTableCraftingScreen parent;
    private boolean tabsInitialized = false;
    private boolean slotRowsFourToSixVisible = false;

    public void init(SimpleTableCraftingScreen parent, boolean widthTooNarrow)
    {
        this.parent = parent;
        updateWidth(widthTooNarrow);
        this.parent.getMinecraft().player.containerMenu = this.parent.getMenu();
        //this.timesInventoryChanged = minecraft.player.getInventory().getTimesChanged();
        this.visible = this.isVisibleAccordingToMenuData();
        if (this.visible)
        {
            this.initVisuals();
        }
        this.parent.getMenu().registerClientHandlerForDataSlot(this.parent.getMenu().DATA_SLOT_TABS_NEED_UPDATE, this::onTabListChangedOnServer);
        this.parent.getMenu().registerClientHandlerForDataSlot(this.parent.getMenu().DATA_SLOT_UPPER_CONTAINER_TRUE_SIZE, this::onContainerSizeChangedOnServer);
        this.parent.getMinecraft().keyboardHandler.setSendRepeatsToGui(true);
    }



    public void initVisuals()
    {
        this.xOffset = (this.parent.width - this.parent.getImageWidth() - PANEL_WIDTH) / 2;
        int bottomY = (this.parent.height - parent.getYSize()) / 2 + PANEL_HEIGHT_WITH_TABS;
        if (this.renameBox == null) {
            this.renameBox = new SlightlyNicerEditBox(this.parent.getMinecraft().font, this.xOffset, bottomY - 18, 120, 9 + 5, Component.literal("Input box for new name for container"));
            this.renameBox.setMaxLength(50);
            this.renameBox.setBordered(false);  // draw bg myself because some dumbass hardcoded black as background
            this.renameBox.setVisible(true);
            this.renameBox.setTextColor(0xcccccc);
            this.renameButton = new SimpleButton(this.xOffset, bottomY - 23, 25, 18, 181, 105, 18+1, BG_CHEST_LOCATION_3_ROWS, 256, 256, p_93751_ -> this.renameButtonClicked(), Component.literal("Rename container"));
            this.renameButton.setTooltipKey(renameTooltipKey);
            this.renameButton.setTooltipInset(Component.literal(""));
            this.renameButton.active = false;
        }
        this.renameBox.x = this.xOffset + 9;
        this.renameBox.y = bottomY - 18;
        this.renameButton.x = this.xOffset + 7 + this.renameBox.getWidth() + 7;
        this.renameButton.y = bottomY - 23;

        if (! this.tabsInitialized) {
            this.tabButtons.clear();
            this.selectedTab = null;
            for (int i = SimpleTableMenu.TABS_SLOT_START; i < SimpleTableMenu.TABS_SLOT_END; i += 2) {
                ItemStack stack = this.parent.getMenu().slots.get(i).getItem();
                if (stack.isEmpty()) {
                    break;
                }
                TabButton button = new TabButton();
                button.setMessage(stack.getHoverName());
                button.itemMain = stack;
                button.itemSub = this.parent.getMenu().slots.get(i + 1).getItem();
                button.chestIndex = (i - SimpleTableMenu.TABS_SLOT_START) / 2;
                button.parent = this;
                //button.setClickHandler( this::tabChanged ); //doesn't work
                this.tabButtons.add(button);
                if (button.chestIndex == this.parent.getMenu().selectedTab) {
                    this.tabChanged(button, true);
                }
            }
        }
        this.tabsInitialized = true;

        if (this.selectedTab == null && this.tabButtons.size() > 0) {
            this.tabChanged(this.tabButtons.get(0), true);
            this.parent.getMenu().updateAccessSlotsOnClient();
        }
        this.updateTabLocations();
    }



    private Boolean tabChanged(TabButton button, boolean dontSendToServer)
    {
        if (! button.equals(this.selectedTab)) {
            if (this.selectedTab != null) {
                this.selectedTab.setStateTriggered(false);
            }
            button.setStateTriggered(true);
            this.selectedTab = button;
            this.parent.getMenu().selectedTab = button.chestIndex; // we separately set this here as we need it in listener. this value change only happens on client and is only needed here.
            if (! dontSendToServer) {
                PacketSender.sendTabChangeToServer(button.chestIndex);
            }
            this.renameButton.setTooltipInset(button.itemMain.getHoverName());
            this.renameBox.setValue("");
            boolean canRenameContainer = (button.itemMain.getCount() & 4) == 0;
            this.renameBox.visible = canRenameContainer;
            this.renameButton.visible = canRenameContainer;
            this.slotRowsFourToSixVisible = (button.itemMain.getCount() & 2) == 2;
            return true;
        }
        return false;
    }

    private Boolean tabChanged(TabButton button)
    {
        return  this.tabChanged(button, false);
    }


    private void onTabListChangedOnServer(Integer flag) {
        if (flag % 2 == 0) return;
        this.tabsInitialized = false;
        this.parent.getMenu().selectedTab = -1;
        this.initVisuals();
        if (this.tabButtons.size() > 0) {
            this.tabChanged(this.tabButtons.get(0));
        }
    }



    private void onContainerSizeChangedOnServer(Integer value) {  }

    public boolean areSlotRowsFourToSixVisible() { return this.slotRowsFourToSixVisible; }

    public int getWidth()
    {
        if (this.isVisibleAccordingToMenuData() && ! this.widthTooNarrow2 && (! this.tabsInitialized || this.tabButtons.size() > 0))
        {
            return PANEL_WIDTH;
        }
        else
        {
            return 0;
        }
    }

    /////////////////////////////////////////////////////////////////////

    @Override
    public void render(PoseStack poseStack, int p_100320_, int p_100321_, float p_100322_)
    {
        if (this.isVisibleTotal())
        {
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.0D, 100.0D);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, this.getBackground());
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int x = this.parent.getGuiLeft();
            int y = (this.parent.height - parent.getYSize()) / 2;
            this.blit(poseStack, x, y, 0, 0, PANEL_WIDTH, PANEL_HEIGHT_WITH_TABS);

            this.renameBox.render(poseStack, p_100320_, p_100321_, p_100322_);
            this.renameButton.render(poseStack, p_100320_, p_100321_, p_100322_);

            for (StateSwitchingButton tabButton : this.tabButtons)
            {
                tabButton.render(poseStack, p_100320_, p_100321_, p_100322_);
            }
            poseStack.popPose();
        }
    }



    private ResourceLocation getBackground() {
        if (this.selectedTab != null && this.slotRowsFourToSixVisible) {
            return BG_CHEST_LOCATION_6_ROWS;
        }
        return BG_CHEST_LOCATION_3_ROWS;
    }

    public void renderTooltip(PoseStack poseStack, int mouseX, int mouseY)
    {
        if (this.isVisibleTotal()) {
            this.renameButton.renderTooltipsSeparately(poseStack, mouseX, mouseY);
            for(StateSwitchingButton tabButton : this.tabButtons)
            {
                if (tabButton.isHoveredOrFocused())
                {
                    if (this.parent.getMinecraft().screen != null) {
                        this.parent.getMinecraft().screen.renderTooltip(poseStack, tabButton.getMessage(), mouseX, mouseY);
                    }
                    break;
                }
            }
        }
    }

    /////////////////////////////////////////////

    @Override
    public NarrationPriority narrationPriority()
    {
        return this.visible ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
        List<NarratableEntry> list = Lists.newArrayList();
        list.addAll(this.tabButtons);
        Screen.NarratableSearchResult screen$narratablesearchresult = Screen.findNarratableWidget(list, (NarratableEntry) null);
        if (screen$narratablesearchresult != null) {
            screen$narratablesearchresult.entry.updateNarration(output.nest());
        }
    }

    public boolean isMouseOver(double p_100353_, double p_100354_) {
        return false;
    }
    public boolean changeFocus(boolean p_100372_) {
        return false;
    }
    public void removed() {
        this.parent.getMinecraft().keyboardHandler.setSendRepeatsToGui(false);
    }
    public void toggleVisibility() {
        this.setVisible(!this.isVisible());
    }
    public boolean isVisible() {
        return this.visible && this.tabButtons.size() > 0;
    }
    public boolean isVisibleTotal() { return this.isVisible() && (! this.tabsInitialized || this.tabButtons.size() > 0) && ! this.widthTooNarrow2;  }
    private boolean isVisibleAccordingToMenuData() { return this.parent.getMenu().showInventoryAccess(); }



    protected void setVisible(boolean value)
    {
        boolean changeTab = value && ! this.visible;
        if (value) { this.initVisuals(); }
        this.visible = value;
        this.updateSlotPositions();
        if (changeTab) {
            this.tabChanged(this.tabButtons.get(0), true); // just to update visuals
        }
    }

    public void tick()
    {
        this.tickCount++;
        if (this.tickCount < 5 || this.tickCount % 10 == 6)
        {
            boolean flag = this.isVisibleAccordingToMenuData();
            if (this.visible != flag) {
                this.setVisible(flag);
                this.parent.setPositionsX();
            }
        }
        if (this.isVisibleTotal() && this.renameBox != null)
        {
            this.renameBox.tick();
            if (this.tickCount % 10 == 5) {
                this.suppressRenameButton = false;
                this.renameButton.active = !this.renameBox.getValue().isEmpty() && (this.parent.getMinecraft().player.experienceLevel > 0 || this.parent.getMinecraft().player.isCreative());
            }
        }
    }

    ///////////////////////////////////////////

    @Override
    public boolean keyPressed(int p_94745_, int p_94746_, int p_94747_) {
        if (this.renameBox.isFocused()) {
            if (this.renameBox.keyPressed(p_94745_, p_94746_, p_94747_) || this.renameBox.canConsumeInput()) {
                return true;
            }
        }

        return GuiEventListener.super.keyPressed(p_94745_, p_94746_, p_94747_);
    }

    @Override
    public boolean charTyped(char p_94732_, int p_94733_) {
        if (this.renameBox.isFocused()) {
            if (this.renameBox.charTyped(p_94732_, p_94733_)) {
                return true;
            }
        }
        return GuiEventListener.super.charTyped(p_94732_, p_94733_);
    }

    ////////////////////////////////////////

    private int lastInventoryAccessRange = 0;

    private void updateWidth(boolean widthTooNarrow) {
        if (this.widthTooNarrow2 != widthTooNarrow) {
            this.widthTooNarrow2 = widthTooNarrow;
            this.updateSlotPositions();
        }
    }

    private void updateSlotPositions() {
        if (this.isVisibleTotal() && ! this.slotsMoved) {
            for (int k = 0; k < this.parent.getMenu().slots.size(); k++) {
                this.parent.getMenu().slots.get(k).x += PANEL_WIDTH + 2;
            }
            this.slotsMoved = true;
            for (var c : this.parent.children())  // CraftingTweaks support
            {
                if (c instanceof Button b)
                {
                    b.x += PANEL_WIDTH + 2;
                }
            }
        }
        else if (! this.isVisibleTotal() && this.slotsMoved) {
            for (int k = 0; k < this.parent.getMenu().slots.size(); k++) {
                this.parent.getMenu().slots.get(k).x -= PANEL_WIDTH + 2;
            }
            this.slotsMoved = false;
            for (var c : this.parent.children())   // CraftingTweaks support
            {
                if (c instanceof Button b)
                {
                    b.x -= PANEL_WIDTH + 2;
                }
            }
        }
    }
    private boolean slotsMoved = false;



    private void updateTabLocations()
    {
        int startx = this.xOffset + 3;
        int starty = (this.parent.height - this.parent.getYSize()) / 2;
        int counter = 0;
        for(StateSwitchingButton tabButton : this.tabButtons)
        {
            tabButton.setPosition(startx + (TabButton.WIDTH - 1 /*overlap 1px*/) * counter, starty);
            counter++;
            tabButton.visible = true;
        }
    }

    public void slotClicked(@Nullable Slot slot)
    {
        //System.out.println("~~~ slot clicked~ " + (slot == null ? "NULL" : (slot.index)));
    }

    public boolean hasClickedOutside(double mouseX, double mouseY, int leftPos, int topPos, int width, int height, int mouseButton)
    {
        if (! this.isVisibleTotal()) {
            return true;
        } else {
            return mouseX < leftPos
                || mouseX > leftPos + PANEL_WIDTH
                || mouseY < topPos
                || mouseY > topPos + height;
        }
    }

    public boolean mouseClicked(double v1, double v2, int mouseButton) {
        if (this.renameBox != null) {
            if (this.renameBox.isMouseOver(v1, v2)) {
                //System.out.println("~~~mousecl E  " + this.renameBox.isFocused() + "/" + this.renameBox.isHoveredOrFocused());
                this.renameBox.setFocus(true);
                return true;
            }
            else {
                this.renameBox.setFocus(false);
            }
        }
        for(TabButton tabButton : this.tabButtons)
        {
            if (tabButton.isMouseOver(v1, v2))
            {
                this.tabChanged(tabButton);
                return true;
            }
        }
        if (this.renameButton != null && this.renameButton.active && ! this.suppressRenameButton && this.renameButton.isMouseOver(v1, v2)) {
            this.suppressRenameButton = true;
            this.renameButton.mouseClicked(v1, v2, mouseButton);
            return true;
        }
        //System.out.println("~~~mousecl  " + v1 + "   " + v2 + "    " + mouseButton + "/" + this.renameBox.isFocused());
        return false;
    }
    private boolean suppressRenameButton = false;

    private void renameButtonClicked() {
        PacketSender.sendRenameRequestToServer(this.renameBox.getValue());
        this.selectedTab.setMessage(Component.literal(this.renameBox.getValue())); // fake it
        this.renameBox.setValue("");
    }

    ///////////////////////////////////////////////////////

    private class TabButton extends StateSwitchingButton
    {
        public static final int WIDTH = 22;
        public static final int HEIGHT = 26;
        public InventoryAccessComponent parent;
        private ItemStack itemMain = ItemStack.EMPTY, itemSub = ItemStack.EMPTY;
        private int chestIndex;
        public TabButton()
        {
            super(0, 0, WIDTH, HEIGHT, false);
            this.initTextureValues(0, 168, 54, 0, BG_CHEST_LOCATION_3_ROWS);
        }


        public void renderButton(PoseStack poseStack, int p_100458_, int p_100459_, float p_100460_)
        {
            Minecraft minecraft = Minecraft.getInstance();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, this.resourceLocation);
            RenderSystem.disableDepthTest();
            int texX = this.xTexStart;
            int texY = this.yTexStart;
            if (this.isStateTriggered) { texX += this.xDiffTex; }
            if (this.isHoveredOrFocused()) { texY += this.yDiffTex; } //not a thing
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.blit(poseStack, this.x, this.y, texX, texY, this.width, this.height);
            RenderSystem.enableDepthTest();
            this.renderIcon(minecraft.getItemRenderer());
        }

        boolean checkedForSpecialScaling = false, doSpecialScaling = false;
        private void renderIcon(ItemRenderer itemRenderer)
        {
            if (! this.checkedForSpecialScaling) {
                this.doSpecialScaling = ! (itemMain.getItem() instanceof BlockItem);
                this.checkedForSpecialScaling = true;
            }
            if (! this.doSpecialScaling) {
                // main image - block   (chests, barrels)
                itemRenderer.renderAndDecorateFakeItem(itemMain, this.x + 1, this.y + 2);
            }
            else {
                // main image - item    (belt, backpack...)
                int x = (this.parent.parent.width - this.parent.parent.getXSize()) / 2;
                int y = (this.parent.parent.height - this.parent.parent.getYSize()) / 2;
                PoseStack posestack = RenderSystem.getModelViewStack();
                posestack.pushPose();
                posestack.scale(0.667F, 0.667F, 0.667F);
                posestack.translate(0, 0, +100.0D);
                RenderSystem.applyModelViewMatrix();
                itemRenderer.renderAndDecorateFakeItem(itemMain, (int)((x + this.chestIndex * (WIDTH-1) + 7) * 1.5d), (int)((y+6)*1.5d));
                posestack.popPose();
                RenderSystem.applyModelViewMatrix();
            }
            // sub image
            int x = (this.parent.parent.width - this.parent.parent.getXSize()) / 2;
            int y = (this.parent.parent.height - this.parent.parent.getYSize()) / 2;
            PoseStack posestack = RenderSystem.getModelViewStack();
            posestack.pushPose();
            posestack.scale(0.667F, 0.667F, 0.667F);
            posestack.translate(0, 0, +100.0D);
            RenderSystem.applyModelViewMatrix();
            itemRenderer.renderAndDecorateFakeItem(itemSub, (int)((x + this.chestIndex * (WIDTH-1) + 13) * 1.5d), (int)((y+12)*1.5d));
            posestack.popPose();
            RenderSystem.applyModelViewMatrix();
        }

        private Function<TabButton, Boolean> handler = null;
        public void setClickHandler(Function<TabButton, Boolean> handler)
        {
            this.handler = handler;
        }

        @Override
        public boolean mouseClicked(double p_93641_, double p_93642_, int p_93643_)
        {
            if (this.handler != null)
            {
                return this.handler.apply(this);
            }
            return super.mouseClicked(p_93641_, p_93642_, p_93643_);
        }
    }
}
