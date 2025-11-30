package moonfather.workshop_for_handsome_adventurer.block_entities.screens;

import com.google.common.collect.Lists;
import moonfather.workshop_for_handsome_adventurer.CommonConfig;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableDataSlots;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import moonfather.workshop_for_handsome_adventurer.block_entities.messaging.PacketSender;
import moonfather.workshop_for_handsome_adventurer.block_entities.screen_components.SimpleButton;
import moonfather.workshop_for_handsome_adventurer.block_entities.screen_components.SlightlyNicerEditBox;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import moonfather.workshop_for_handsome_adventurer.integration.PolymorphAccessorClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Function;

@ParametersAreNonnullByDefault
public class InventoryAccessComponent implements Renderable, GuiEventListener, NarratableEntry
{
    public static final int PANEL_WIDTH = 176;
    public static final int PANEL_HEIGHT_WITHOUT_TABS = 134;
    public static final int PANEL_HEIGHT_WITH_TABS = 166;
    protected static final ResourceLocation BG_CHEST_LOCATION_3_ROWS = ResourceLocation.parse("workshop_for_handsome_adventurer:textures/gui/left_panel_normal_chest.png");
    protected static final ResourceLocation BG_CHEST_LOCATION_6_ROWS = ResourceLocation.parse("workshop_for_handsome_adventurer:textures/gui/left_panel_double_chest.png");
    private static final String RENAME_BUTTON_LOCATION = "workshop_for_handsome_adventurer:textures/gui/rename_%s.png";
    private static final String renameTooltipKey = "message.workshop_for_handsome_adventurer.rename";
    private static final String renameTooltip0Key = "message.workshop_for_handsome_adventurer.rename0";

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
            this.hideIfScreenTooNarrow();
        }
        this.parent.getMenu().registerClientHandlerForDataSlot(SimpleTableDataSlots.DATA_SLOT_TABS_NEED_UPDATE, this::onTabListChangedOnServer);
        this.parent.getMenu().registerClientHandlerForDataSlot(SimpleTableDataSlots.DATA_SLOT_UPPER_CONTAINER_TRUE_SIZE, this::onContainerSizeChangedOnServer);
    }



    private void hideIfScreenTooNarrow()
    {
        // sane condition is   this.isVisibleAccordingToMenuData() != this.isVisibleTotal()
        // ...and that will work on resize but not on first start; both are false first time.
        // ...instead of accounting for that, i'll just go unconditionally.
        this.parent.getMenu().setClientFlagScreenTooNarrow(this.widthTooNarrow2);
        this.parent.getMenu().updateAccessSlotsOnClient();
    }

    public void initVisuals()
    {
        this.xOffset = (this.parent.width - this.parent.getImageWidth() - PANEL_WIDTH) / 2;
        int bottomY = (this.parent.height - parent.getYSize()) / 2 + PANEL_HEIGHT_WITH_TABS;
        if (this.renameBox == null)
        {
            this.renameBox = new SlightlyNicerEditBox(this.parent.getMinecraft().font, this.xOffset, bottomY - 18, 120, 9 + 5, Component.literal("Input box for new name for container"));
            this.renameBox.setMaxLength(50);
            this.renameBox.setBordered(false);  // draw bg myself because some dumbass hardcoded black as background
            this.renameBox.setVisible(true);
            this.renameBox.setTextColor(0xffcccccc);
            this.renameBox.setTextShadow(false);
            this.renameButton = new SimpleButton(this.xOffset, bottomY - 23, 25, 18, RENAME_BUTTON_LOCATION, "normal", "hovered", "disabled",  32, 32, p_93751_ -> this.renameButtonClicked(), Component.literal("Rename container"));
            this.renameButton.setTooltipKey(CommonConfig.RenameChestsForFree.get() ? renameTooltip0Key : renameTooltipKey);
            this.renameButton.setTooltipInset(Component.literal(""));
            this.renameButton.active = false;
        }
        this.renameBox.setX(this.xOffset + 9);
        this.renameBox.setY(bottomY - 18);
        this.renameButton.setX(this.xOffset + 7 + this.renameBox.getWidth() + 7);
        this.renameButton.setY(bottomY - 23);

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
            Integer flagsBoxed = button.itemMain.get(Registration.TAB_FLAGS.get());
            int flags = flagsBoxed != null ? flagsBoxed : 1;
            boolean canRenameContainer = (flags & 4) == 0;
            this.renameBox.visible = canRenameContainer;
            this.renameButton.visible = canRenameContainer;
            this.slotRowsFourToSixVisible = (flags & 2) == 2;
            return true;
        }
        return false;
    }

    private Boolean tabChanged(TabButton button)
    {
        return  this.tabChanged(button, false);
    }


    private void onTabListChangedOnServer(Integer flag)
    {
        if (flag % 2 == 0) return;
        int previousTabCount = this.tabButtons.size(); //! N
        int newTabCount = 0; //! N
        for (int i = SimpleTableMenu.TABS_SLOT_START; i < SimpleTableMenu.TABS_SLOT_END; i += 2)         //!! N
        {
            ItemStack stack = this.parent.getMenu().slots.get(i).getItem();
            if (stack.isEmpty())
            {
                break;
            }
            newTabCount += 1;
        } //!! N
        if (previousTabCount != newTabCount)
        {
            this.parent.getMenu().selectedTab = -1;
        }

        this.tabsInitialized = false;
//!!        this.parent.getMenu().selectedTab = -1;
        this.initVisuals();
        if (previousTabCount == 0 && newTabCount > 0)   //! N
        {
//!!            if (this.tabButtons.size() > 0)
//!!            {
//????????????            this.tabChanged(this.tabButtons.get(0));
//!!            }
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
    public void render(GuiGraphics graphics, int p_100320_, int p_100321_, float p_100322_)
    {
        if (this.isVisibleTotal())
        {
            int x = this.parent.getGuiLeft();
            int y = (this.parent.height - parent.getYSize()) / 2;
            graphics.blit(RenderPipelines.GUI_TEXTURED, this.getBackground(), x, y,0, 0, PANEL_WIDTH, PANEL_HEIGHT_WITH_TABS, 256, 256);

            this.renameBox.render(graphics, p_100320_, p_100321_, p_100322_);
            this.renameButton.render(graphics, p_100320_, p_100321_, p_100322_);

            for (StateSwitchingButton tabButton : this.tabButtons)
            {
                tabButton.render(graphics, p_100320_, p_100321_, p_100322_);
            }
        }
    }



    private ResourceLocation getBackground() {
        if (this.selectedTab != null && this.slotRowsFourToSixVisible) {
            return BG_CHEST_LOCATION_6_ROWS;
        }
        return BG_CHEST_LOCATION_3_ROWS;
    }

    public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        if (this.isVisibleTotal()) {
            this.renameButton.renderTooltipsSeparately(graphics, this.parent.getFont(), mouseX, mouseY);
            for(TabButton tabButton : this.tabButtons)
            {
                if (tabButton.isHoveredOrFocused())
                {
                    if (this.parent.getMinecraft().screen != null)
                    {
                        graphics.renderTooltip(this.parent.getFont(), tabButton.getMessageForTooltip(), mouseX+2, mouseY+12, DefaultTooltipPositioner.INSTANCE, null);
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

    @Override
    public void setFocused(boolean p_265728_) {
    }

    @Override
    public boolean isFocused() {
        return this.renameBox.isFocused();
    }

    public void removed() { }
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
        this.hideIfScreenTooNarrow();
        this.updatePolymorphButton();
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
            if (this.tickCount % 10 == 5) {
                this.suppressRenameButton = false;
                boolean hasEnoughXP = this.parent.getMinecraft().player.isCreative() || CommonConfig.RenameChestsForFree.get() || this.parent.getMinecraft().player.experienceLevel > 0;
                this.renameButton.active = ! this.renameBox.getValue().isEmpty() && hasEnoughXP;
            }
        }
    }

    ///////////////////////////////////////////

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (this.renameBox.isFocused())
        {
            if (scanCode == 23) // tab
            {
                this.renameBox.setFocused(false);
                return true;
            }
            // if (keyCode == 69) /* E */ { return true; }
            // if (this.renameBox.keyPressed(keyCode, scanCode, modifiers)) { return true; }
            this.renameBox.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        return GuiEventListener.super.keyPressed(keyCode, scanCode, modifiers);
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
                    b.setX(b.getX() + (PANEL_WIDTH + 2));
                }
            }
        }
        else if (! this.isVisibleTotal() && this.slotsMoved) {
            for (int k = 0; k < this.parent.getMenu().slots.size(); k++) {
                this.parent.getMenu().slots.get(k).x -= PANEL_WIDTH + 2;
            }
            this.slotsMoved = false;
            for (var c : this.parent.children())  // CraftingTweaks support
            {
                if (c instanceof Button b)
                {
                    b.setX(b.getX() - (PANEL_WIDTH + 2));
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
        boolean topRow = true;
        for (StateSwitchingButton tabButton : this.tabButtons)
        {
            if (counter >= TabButton.TAB_ROW_COUNT)
            {
                topRow = false;
                counter -= TabButton.TAB_ROW_COUNT;
                starty += 163;
            }
            tabButton.setPosition(startx + (TabButton.WIDTH - 1 /*overlap 1px*/) * counter, starty);
            counter++;
            tabButton.visible = true;
        }
    }

    private void updatePolymorphButton()
    {
        // client-side handler for polymorph: we move the button, hopefully.
        if (ModList.get().isLoaded("polymorph"))
        {
            PolymorphAccessorClient.updatePosition();
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

    @Override
    public boolean mouseClicked(double v1, double v2, int mouseButton) {
        if (this.renameBox != null) {
            if (this.renameBox.isMouseOver(v1, v2)) {
                //System.out.println("~~~mousecl E  " + this.renameBox.isFocused() + "/" + this.renameBox.isHoveredOrFocused());
                this.renameBox.setFocused(true);
                return true;
            }
            else {
                this.renameBox.setFocused(false);
            }
        }
        for (TabButton tabButton : this.tabButtons)
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

    private static class TabButton extends StateSwitchingButton
    {
        private static final int TAB_ROW_COUNT = 8;
        public static final int WIDTH = 22;
        public static final int HEIGHT = 26;
        public InventoryAccessComponent parent;
        private ItemStack itemMain = ItemStack.EMPTY, itemSub = ItemStack.EMPTY;
        private int chestIndex;

        private static final ResourceLocation IMAGE_ACTIVE_TAB = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "textures/gui/tab_top_active.png"); // no need for WidgetSprites class
        private static final ResourceLocation IMAGE_INACTIVE_TAB = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "textures/gui/tab_top_inactive.png");
        private static final ResourceLocation IMAGE_ACTIVE_BOTTOM_TAB = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "textures/gui/tab_bottom_active.png"); // no need for WidgetSprites class
        private static final ResourceLocation IMAGE_INACTIVE_BOTTOM_TAB = ResourceLocation.fromNamespaceAndPath(Constants.MODID, "textures/gui/tab_bottom_inactive.png");
        private ResourceLocation imageActiveTab, imageInactiveTab;

        public TabButton()
        {
            super(0, 0, WIDTH, HEIGHT, false);
            // not calling initTextureValues. will do things manually it requires atlas for this control.
        }


        @Override
        public void renderWidget(GuiGraphics graphics, int p_100458_, int p_100459_, float p_100460_)
        {
            int texX = 2;  // ignoring isHoveredOrFocused()
            int texY = this.chestIndex < TAB_ROW_COUNT ? 2 : 4;
            if (imageActiveTab == null) {
                imageActiveTab = this.chestIndex < TAB_ROW_COUNT ? IMAGE_ACTIVE_TAB : IMAGE_ACTIVE_BOTTOM_TAB;
                imageInactiveTab = this.chestIndex < TAB_ROW_COUNT ? IMAGE_INACTIVE_TAB : IMAGE_INACTIVE_BOTTOM_TAB;
            }
            graphics.blit(RenderPipelines.GUI_TEXTURED, this.isStateTriggered ? imageActiveTab : imageInactiveTab, this.getX(), this.getY(), texX, texY, this.width, this.height, 32, 32);
            this.renderIcon(graphics);
        }

        boolean checkedForSpecialScaling = false, doSpecialScaling = false;
        private void renderIcon(GuiGraphics graphics)
        {
            int x = (this.parent.parent.width - this.parent.parent.getXSize()) / 2;
            int y = (this.parent.parent.height - this.parent.parent.getYSize()) / 2;
            int tabIndexInRow = this.chestIndex;
            int textureYAdjustment = 0; // 0 for top
            int textureYAdjustment2 = 0; // 0 for top
            if (this.chestIndex >= TAB_ROW_COUNT) {
                tabIndexInRow = this.chestIndex - TAB_ROW_COUNT;
                textureYAdjustment = 3;
                y = y + this.parent.parent.getYSize() - HEIGHT - 39 - textureYAdjustment;
                textureYAdjustment2 = 1;
            }

            if (! this.checkedForSpecialScaling) {
                this.doSpecialScaling = ! (itemMain.getItem() instanceof BlockItem);
                this.checkedForSpecialScaling = true;
            }
            if (! this.doSpecialScaling) {
                // main image - block   (chests, barrels)
                graphics.renderFakeItem(itemMain, this.getX() + 1, this.getY() + 3 + textureYAdjustment2);
                // not using x and y prepared above; still moved above because it is used for first item and for backpack/belt icons.
            }
            else {
                // main image - item    (belt, backpack...)
                graphics.pose().pushMatrix();
                graphics.pose().scale(2/3f, 2/3f); // why did i downsize? looks bad but i probably had a reason.
                graphics.renderFakeItem(itemMain, (int)((x + tabIndexInRow * (WIDTH-1) + 7) * 1.5d), (int)((y+5)*1.5d));
                graphics.pose().popMatrix();
            }
            // sub image
            graphics.pose().pushMatrix();
            graphics.pose().scale(2/3f, 2/3f);
            //graphics.renderFakeItem(itemSub, (int)((x + tabIndexInRow * (WIDTH-1) + 13) * 1.5d), (int)((y+12)*1.5d));
            graphics.renderFakeItem(itemSub, (int)((x + tabIndexInRow * (WIDTH-1) + 13) * 1.5d), (int)((this.getY() + textureYAdjustment2+12)*1.5d));
            graphics.pose().popMatrix();
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

        public List<ClientTooltipComponent> getMessageForTooltip()
        {
            if (this.tooltip == null)
            {
                this.tooltip = List.of(ClientTooltipComponent.create(this.getMessage().getVisualOrderText()));
            }
            return this.tooltip;
        }
        private List<ClientTooltipComponent> tooltip = null;
    }
}
