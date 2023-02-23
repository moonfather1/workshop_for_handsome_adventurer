package moonfather.workshop_for_handsome_adventurer.block_entities;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
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

    private int xOffset;
    private final List<TabButton> tabButtons = Lists.newArrayList();
    private TabButton selectedTab;
    private EditBox renameBox;
    private boolean visible;
    private int tickCount = 0;
    private boolean widthTooNarrow2 = false;
    private SimpleTableCraftingScreen parent;
    private boolean tabsInitialized = false;

    public void init(SimpleTableCraftingScreen parent, boolean widthTooNarrow)
    {
        this.parent = parent;
        this.widthTooNarrow2 = widthTooNarrow;
        this.parent.getMinecraft().player.containerMenu = this.parent.getMenu();
        //this.timesInventoryChanged = minecraft.player.getInventory().getTimesChanged();
        this.visible = this.isVisibleAccordingToMenuData();
        if (this.visible)
        {
            this.initVisuals();
        }
        this.parent.getMinecraft().keyboardHandler.setSendRepeatsToGui(true);
    }

    public void initVisuals()
    {
        this.xOffset = (this.parent.width - this.parent.getImageWidth() - PANEL_WIDTH) / 2;
        //int i = (this.width - 147) / 2 - this.xOffset;
        //int j = (this.height - 166) / 2;
        //String s = this.searchBox != null ? this.searchBox.getValue() : "";
        //this.searchBox = new EditBox(this.minecraft.font, i + 25, j + 14, 80, 9 + 5, new TranslatableComponent("itemGroup.search"));
        //this.searchBox.setMaxLength(50);
        //this.searchBox.setBordered(false);
        //this.searchBox.setVisible(true);
        //this.searchBox.setTextColor(16777215);
        //this.searchBox.setValue(s);
        //this.filterButton = new StateSwitchingButton(i + 110, j + 12, 26, 16, this.book.isFiltering(this.menu));

        this.tabButtons.clear();
        for (int i = SimpleTableMenu.TABS_SLOT_START; i < SimpleTableMenu.TABS_SLOT_END; i+=2)
        {
            ItemStack stack = this.parent.getMenu().slots.get(i).getItem();
            if (stack.isEmpty())
            {
                break;
            }
            TabButton button = new TabButton();
            button.setMessage(stack.getHoverName());
            button.itemMain = stack;
            button.itemSub = this.parent.getMenu().slots.get(i+1).getItem();
            button.chestIndex = (i - SimpleTableMenu.TABS_SLOT_START) / 2;
            button.parent = this;
            //button.setClickHandler( this::tabChanged ); //doesn't work
            this.tabButtons.add(button);
        }
        this.tabsInitialized = true;

        if (this.selectedTab == null && this.tabButtons.size() > 0) { this.selectedTab = this.tabButtons.get(0); }
        if (this.selectedTab != null) { this.selectedTab.setStateTriggered(true); }
        this.updateTabs();
    }

    private Boolean tabChanged(TabButton button)
    {
        if (this.selectedTab != null && this.selectedTab != button)
        {
            this.selectedTab.setStateTriggered(false);
            //int slot = this.selectedTab.chestIndex * 2 + SimpleTableMenu.TABS_SLOT_START;
            //ItemStack chest = this.parent.getMenu().slots.get(slot).getItem();
            //chest.setCount(1);
            //this.parent.getMenu().slots.get(slot).set(chest);         DOBRA IDEJA ALI NE RADI U OVOM SMERU
        }
        button.setStateTriggered(true);
        this.selectedTab = button;
        System.out.println("~~tabchanged to " + button.chestIndex);
        //int slot = button.chestIndex * 2 + SimpleTableMenu.TABS_SLOT_START;
        //ItemStack chest = this.parent.getMenu().slots.get(slot).getItem();
        //chest.setCount(2);
        //this.parent.getMenu().slots.get(slot).set(chest);     DOBRA IDEJA ALI NE RADI U OVOM SMERU
        return false;
    }

    public int preferredScreenPositionX(int totalWidth, int dialogWidth)
    {
        int x;
        if (this.isVisibleAccordingToMenuData() && ! this.widthTooNarrow2 && (! this.tabsInitialized || this.tabButtons.size() > 0))
        {
            x = (totalWidth - dialogWidth - PANEL_WIDTH) / 2 + PANEL_WIDTH + 2;
        } else
        {
            x = (totalWidth - dialogWidth) / 2;
        }
        return x;
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
            RenderSystem.setShaderTexture(0, BG_CHEST_LOCATION_3_ROWS);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int x = (this.parent.width - PANEL_WIDTH - this.parent.getXSize()) / 2;
            int y = (this.parent.height - PANEL_HEIGHT_WITH_TABS) / 2;
            this.blit(poseStack, x, y, 0, 0, PANEL_WIDTH, PANEL_HEIGHT_WITH_TABS);
            //if (!this.searchBox.isFocused() && this.searchBox.getValue().isEmpty()) {
            //    drawString(poseStack, this.minecraft.font, SEARCH_HINT, i + 25, j + 14, -1);
            //} else {
            //    this.searchBox.render(poseStack, p_100320_, p_100321_, p_100322_);
            //}

            for(StateSwitchingButton tabButton : this.tabButtons)
            {
                tabButton.render(poseStack, p_100320_, p_100321_, p_100322_);
            }

            //this.filterButton.render(p_100319_, p_100320_, p_100321_, p_100322_);
            //this.recipeBookPage.render(p_100319_, i, j, p_100320_, p_100321_, p_100322_);
            poseStack.popPose();
        }
    }

    public void renderTooltip(PoseStack poseStack, int p_100363_, int p_100364_, int p_100365_, int p_100366_)
    {
        if (this.isVisibleTotal()) {
            for(StateSwitchingButton tabButton : this.tabButtons)
            {
                if (tabButton.isHoveredOrFocused())
                {
                    if (this.parent.getMinecraft().screen != null) {
                        this.parent.getMinecraft().screen.renderTooltip(poseStack, tabButton.getMessage(), p_100365_, p_100366_);
                    }
                    break;
                }
            }
        }
    }

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
    public boolean isVisibleTotal() {
        return this.isVisible() && (! this.tabsInitialized || this.tabButtons.size() > 0) && ! this.widthTooNarrow2;
    }
    private boolean isVisibleAccordingToMenuData() { return this.parent.getMenu().showInventoryAccess(); }

    protected void setVisible(boolean value)
    {
        if (value) { this.initVisuals(); }
        this.visible = value;
        //if (!value) { this.something.setInvisible(); }
        //this.sendUpdateSettings();
    }

    public void tick()
    {
        this.tickCount++;
        if (this.tickCount < 5 || this.tickCount % 20 == 6)
        {
            boolean flag = this.isVisibleAccordingToMenuData();
            if (this.visible != flag) {
                this.setVisible(flag);
                this.parent.setLeft(this.preferredScreenPositionX(this.parent.width, this.parent.getImageWidth()));
            }
        }
        if (this.isVisibleTotal())
        {
            if (this.renameBox != null) //todo:remove later
            this.renameBox.tick();
        }
    }

    private void updateTabs()
    {
        int startx = this.xOffset + 3;//(this.width - 147) / 2 - this.xOffset - 30;
        int starty = (this.parent.height - PANEL_HEIGHT_WITH_TABS) / 2; //(this.height - 166) / 2 + 3;
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
        System.out.println("~~~ slot clicked~ " + (slot == null ? "NULL" : (slot.index)));
    }

    public boolean hasClickedOutside(double mouseX, double mouseY, int leftPos, int topPos, int dialogWidth, int dialogHeight, int whoKnows)
    {
        if (!this.isVisible()) {
            return true;
        } else {
            boolean flag = mouseX < (double)leftPos || mouseY < (double)topPos || mouseX >= (double)(leftPos + dialogWidth) || mouseY >= (double)(topPos + dialogHeight);
            boolean flag1 = (double)(leftPos - PANEL_WIDTH) < mouseX && mouseX < (double)leftPos && (double)topPos < mouseY && mouseY < (double)(topPos + dialogHeight);
            return flag && !flag1 && !this.selectedTab.isHoveredOrFocused();
        }
    }

    public boolean mouseClicked(double v1, double v2, int mouseButton) {
        for(TabButton tabButton : this.tabButtons)
        {
            if (tabButton.isMouseOver(v1,v2))
            {
                this.tabChanged(tabButton);
                return true;
            }
        }
        System.out.println("~~~mousecl  " + v1 + "   " + v2 + "    " + mouseButton);
        return false;
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

        private void renderIcon(ItemRenderer itemRenderer)
        {
            itemRenderer.renderAndDecorateFakeItem(itemMain, this.x + 1, this.y + 2);

            int x = (this.parent.parent.width - PANEL_WIDTH - this.parent.parent.getXSize()) / 2;
            int y = (this.parent.parent.height - PANEL_HEIGHT_WITH_TABS) / 2;
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
