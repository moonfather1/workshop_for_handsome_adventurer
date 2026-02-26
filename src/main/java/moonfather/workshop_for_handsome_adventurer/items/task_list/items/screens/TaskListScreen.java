package moonfather.workshop_for_handsome_adventurer.items.task_list.items.screens;

import moonfather.workshop_for_handsome_adventurer.ClientConfig;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListComponent;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListMessaging;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

import java.util.*;

public class TaskListScreen extends Screen
{
    public TaskListScreen(List<TaskListMessaging.TaskPageDTO> pagesFromItem, int pageCount, TaskListMessaging.TaskListExtraDTO extraInfo, boolean isFireImmune)
    {
        super(Component.literal("task list"));
        this.extraOriginal = extraInfo;
        this.page = extraInfo.lastPage();
        this.pageCount = pageCount;
        this.initialContent = pagesFromItem.get(extraInfo.lastPage()-1);
        this.pagesFromItem = pagesFromItem;
        this.itemName = extraInfo.itemName();
        this.isFireImmune = isFireImmune;
    }

    private static final Identifier BG_LOCATION = Identifier.fromNamespaceAndPath(Constants.MODID, "textures/gui/gui_task_list_bg.png");
    private final int imageWidth = 179;
    private final int imageHeight = 209;
    protected int leftPos;
    protected int topPos;
    private int page;
    private String footer = null;
    private int topMarginMain = 24;
    private TaskListMessaging.TaskPageDTO initialContent;
    private final boolean isFireImmune;
    private final int pageCount;
    private final TaskListMessaging.TaskListExtraDTO extraOriginal;
    private final String itemName;
    private final List<TaskListMessaging.TaskPageDTO> pagesFromItem;
    private final List<EditBox> editBoxes = new ArrayList<>();
    private EditBox header;
    private final List<String> checkBoxValues = new ArrayList<>(6);
    private final HashMap<String, Identifier> checkBoxImages = new HashMap<>(4);
    private ImageWidget arrowPrev1, arrowPrev2, arrowNext1, arrowNext2;
    private static final Identifier BTN_LEFT_NORMAL = Identifier.withDefaultNamespace("widget/page_backward");
    private static final Identifier BTN_LEFT_ACTIVE = Identifier.withDefaultNamespace("widget/page_backward_highlighted");   // withDefaultNamespace("assets/minecraft/textures/gui/sprites/widget/page_backward.png"); // full path
    private static final Identifier BTN_RIGHT_NORMAL = Identifier.withDefaultNamespace("widget/page_forward");
    private static final Identifier BTN_RIGHT_ACTIVE = Identifier.withDefaultNamespace("widget/page_forward_highlighted");
    //private static final Identifier BTN_RIGHT_ACTIVE = Identifier.withDefaultNamespace("textures/gui/sprites/widget/page_forward_highlighted.png"); // this works with texture() call instead of sprioe()
    private static final Identifier CHECKBOX_EMPTY = Identifier.fromNamespaceAndPath(Constants.MODID,"textures/gui/task_list_check1.png");
    private static final Identifier CHECKBOX_DONE = Identifier.fromNamespaceAndPath(Constants.MODID,"textures/gui/task_list_check2.png");
    private static final Identifier CHECKBOX_MOPE = Identifier.fromNamespaceAndPath(Constants.MODID,"textures/gui/task_list_check3.png");
    private static final Identifier CHECKBOX_QMARK = Identifier.fromNamespaceAndPath(Constants.MODID,"textures/gui/task_list_check4.png");
    private static final int NORMAL_TEXT_COLOR = 0xff886666;
    private static final int DIM_TEXT_COLOR = 0xffbbaaaa;

    @Override
    protected void init()
    {
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = 0;  // normally middle but i'm thinking top here  (this.height - this.imageHeight) / 2;
        if (this.editBoxes.size() == 0)
        {
            this.topMarginMain = 42; // can do 44 if we need more room above
            int y = topMarginMain;
            int height = 12;
            for (int i = 0; i < 12; i++)
            {
                EditBox eb1 = new TaskListEditBox(Minecraft.getInstance().font, 140, height, Component.empty());
                eb1.setMaxLength(22);
                eb1.setBordered(false);
                eb1.setVisible(true);
                eb1.setTextColor(NORMAL_TEXT_COLOR);
                eb1.setTextShadow(false);

                eb1.setX(this.leftPos + 26);
                eb1.setY(this.topPos + y);
                y += (height - 1);

                this.editBoxes.add(eb1);
                this.renderables.add(eb1);

                if (i % 2 ==1) { y += 2; } // for hor line
            }
            // name edit box; maybe a help button on top
            this.header = new EditBox(Minecraft.getInstance().font, 150, height, Component.literal("Item name"));
            this.header.setMaxLength(32);
            this.header.setBordered(false);
            this.header.setVisible(true);
            this.header.setTextColor(0xFF555088);
            this.header.setTextShadow(false);
            this.header.setX(this.leftPos + 16);
            this.header.setY(this.topPos + 16);
            this.renderables.add(this.header);
            this.header.setValue(this.itemName);

            // edit boxes done, now paging arrows
            int arrowHeight = 13, arrowWidth = 23;
            int hmargin = 10, vmargin = 10;
            this.arrowPrev1 = ImageWidget.sprite(arrowWidth, arrowHeight, BTN_LEFT_NORMAL);
            this.arrowPrev1.setPosition(this.leftPos + hmargin, this.topPos + this.imageHeight - arrowHeight - vmargin);
            this.arrowPrev2 = ImageWidget.sprite(arrowWidth, arrowHeight, BTN_LEFT_ACTIVE);
            this.arrowPrev2.setPosition(this.arrowPrev1.getX(), this.arrowPrev1.getY());
            this.arrowNext1 = ImageWidget.sprite(arrowWidth, arrowHeight, BTN_RIGHT_NORMAL);
            this.arrowNext1.setPosition(this.leftPos + this.imageWidth - arrowWidth - hmargin, this.topPos + this.imageHeight - arrowHeight - vmargin);
            this.arrowNext2 = ImageWidget.sprite(arrowWidth, arrowHeight, BTN_RIGHT_ACTIVE);
            this.arrowNext2.setPosition(this.arrowNext1.getX(), this.arrowNext1.getY());
            this.addRenderableOnly(this.arrowPrev1);
            this.addRenderableOnly(this.arrowPrev2);
            this.addRenderableOnly(this.arrowNext1);
            this.addRenderableOnly(this.arrowNext2);
            this.setArrowVisibilityInitial();
            // paging arrows done, now checkboxes
            this.checkBoxImages.put("e", CHECKBOX_EMPTY);
            this.checkBoxImages.put("y", CHECKBOX_DONE);
            this.checkBoxImages.put("n", CHECKBOX_MOPE);
            this.checkBoxImages.put("q", CHECKBOX_QMARK);
        }
        // this repeats for every page shown (after paging)
        for (int i = 0; i < this.editBoxes.size(); i++)
        {
            this.editBoxes.get(i).setValue(i % 2 == 0 ? this.initialContent.items().get(i/2).line1() : this.initialContent.items().get(i/2).line2());
        }
        this.checkBoxValues.clear();
        for (int i = 0; i < this.editBoxes.size() / 2; i++)
        {
            this.checkBoxValues.add(this.initialContent.items().get(i).status());
        }
        this.greyOutDoneAndAbandoned();
    }

    private void greyOutDoneAndAbandoned()
    {
        this.greyOutDoneAndAbandoned(false, 15);
    }
    private void greyOutDoneAndAbandoned(boolean oneItemOnly, int index)
    {
        if (ClientConfig.taskListColoringForFinishedItems)
        {
            int startIndex, loopEnd;
            if (! oneItemOnly)
            {
                startIndex = 0; loopEnd = this.editBoxes.size();
            }
            else
            {
                startIndex = index * 2; loopEnd = startIndex + 2;
            }
            String status;
            for (int i = startIndex; i < loopEnd; i++)
            {
                status = this.checkBoxValues.get(i/2);
                if (status.equals("y") || status.equals("n"))
                {
                    this.editBoxes.get(i).setTextColor(DIM_TEXT_COLOR);
                }
                else
                {
                    this.editBoxes.get(i).setTextColor(NORMAL_TEXT_COLOR);
                }
            }
        }
    }

    private void setArrowVisibilityInitial()
    {
        this.arrowPrev1.visible = true;
        this.arrowNext1.visible = true;
        this.arrowPrev2.visible = false;
        this.arrowNext2.visible = false;
    }

    private void setArrowVisibilityOnHover(int mouseX, int mouseY)
    {
        boolean hover = mouseX >= this.arrowPrev1.getX() && mouseX <= this.arrowPrev1.getX() + this.arrowPrev1.getWidth()
                && mouseY >= this.arrowPrev1.getY() && mouseY <= this.arrowPrev1.getY() + this.arrowPrev1.getHeight();
        this.arrowPrev2.visible = this.page > 1 && hover;
        this.arrowPrev1.visible = ! this.arrowPrev2.visible;
        hover = mouseX >= this.arrowNext1.getX() && mouseX <= this.arrowNext1.getX() + this.arrowNext1.getWidth()
                && mouseY >= this.arrowNext1.getY() && mouseY <= this.arrowNext1.getY() + this.arrowNext1.getHeight();
        this.arrowNext2.visible = this.page < this.pageCount && hover;
        this.arrowNext1.visible = ! this.arrowNext2.visible;
    }

    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY)
    {
        // if right button, say craft with paper
        boolean hover = mouseX >= this.arrowNext1.getX() && mouseX <= this.arrowNext1.getX() + this.arrowNext1.getWidth()
                && mouseY >= this.arrowNext1.getY() && mouseY <= this.arrowNext1.getY() + this.arrowNext1.getHeight();
        if (! hover)
        {
            this.tooltipTicks = 5 * 20 * 4;
            this.currentTooltip = null;
            return;
        }
        if (this.tooltipTicks < 0)
        {
            return;
        }
        this.tooltipTicks -= 1;

        if (this.currentTooltip == null)
        {
            if (C_DUAL_TOOLTIP.size() == 0)
            {
                C_DUAL_TOOLTIP.add(ClientTooltipComponent.create(PAPER_TOOLTIP.getVisualOrderText()));
                C_DUAL_TOOLTIP.add(ClientTooltipComponent.create(Component.literal(" ").getVisualOrderText()));
                C_DUAL_TOOLTIP.add(ClientTooltipComponent.create(CREAM_TOOLTIP.getVisualOrderText()));
            }
            if (C_KEYBOARD_TOOLTIP.size() == 0)
            {
                boolean firstRow = true;
                for (String s : KEYBOARD_TOOLTIP_RAW.getString().split("<br>"))
                {
                    if (! firstRow)
                    {
                        C_KEYBOARD_TOOLTIP.add(ClientTooltipComponent.create(Component.literal(s).getVisualOrderText()));
                    }
                    else
                    {
                        C_KEYBOARD_TOOLTIP.add(ClientTooltipComponent.create(Component.literal(s).withColor(0x95b5ff).getVisualOrderText()));
                        firstRow = false;
                    }
                }
            }
            if (C_PAPER_ONLY_TOOLTIP.size() == 0)
            {
                C_PAPER_ONLY_TOOLTIP.add(ClientTooltipComponent.create(PAPER_TOOLTIP.getVisualOrderText()));
            }
            if (C_CREAM_ONLY_TOOLTIP.size() == 0)
            {
                C_CREAM_ONLY_TOOLTIP.add(ClientTooltipComponent.create(CREAM_TOOLTIP.getVisualOrderText()));
            }
            int random = this.randomProvider.nextInt(5);
            if (this.page == this.pageCount && this.pageCount < TaskListComponent.MAX_PAGE_COUNT)
            {
                // guiGraphics.renderTooltip(Minecraft.getInstance().font, PAPER_TOOLTIP, mouseX, mouseY);
                if (random == 0 && ! this.isFireImmune) this.currentTooltip = C_DUAL_TOOLTIP;
                else if (random == 1) this.currentTooltip = C_KEYBOARD_TOOLTIP;
                else this.currentTooltip = C_PAPER_ONLY_TOOLTIP;
            }
            if (this.page == this.pageCount && this.pageCount == TaskListComponent.MAX_PAGE_COUNT)
            {
                if (random < 2 && ! this.isFireImmune) this.currentTooltip = C_CREAM_ONLY_TOOLTIP;
                else this.currentTooltip = C_KEYBOARD_TOOLTIP;
            }
        }
        if (this.currentTooltip == null)
        {
            return;
        }
        guiGraphics.renderTooltip(Minecraft.getInstance().font, this.currentTooltip, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
    }
    private static final Component PAPER = Component.translatable(Items.PAPER.getDescriptionId()).withColor(0xffddaa);
    private static final Component PAPER_TOOLTIP = Component.translatable("message.workshop_for_handsome_adventurer.task_list_ex", PAPER);
    private static final Component CREAM = Component.translatable(Items.MAGMA_CREAM.getDescriptionId()).withColor(0xddbb77);
    private static final Component CREAM_TOOLTIP = Component.translatable("message.workshop_for_handsome_adventurer.task_list_cream", CREAM);
    private static final Component KEYBOARD_TOOLTIP_RAW = Component.translatable("message.workshop_for_handsome_adventurer.keyboard");

    private static final List<ClientTooltipComponent> C_DUAL_TOOLTIP = new ArrayList<>(3);
    private static final List<ClientTooltipComponent> C_KEYBOARD_TOOLTIP = new ArrayList<>(6);
    private static final List<ClientTooltipComponent> C_PAPER_ONLY_TOOLTIP = new ArrayList<>(1);
    private static final List<ClientTooltipComponent> C_CREAM_ONLY_TOOLTIP = new ArrayList<>(1);
    private List<ClientTooltipComponent> currentTooltip = null;

    private int tooltipTicks = -1;
    private final Random randomProvider = new Random();

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick)
    {
        // focusing
        boolean foundEditBox = false;
        for (int i = 0; i < this.editBoxes.size(); i++)
        {
            if (this.editBoxes.get(i).mouseClicked(event, isDoubleClick))
            {
                this.setFocused(this.editBoxes.get(i));
                foundEditBox = true;
                break;
            }
        }
        if (! foundEditBox && this.header.mouseClicked(event, isDoubleClick))
        {
            this.setFocused(this.header);
            foundEditBox = true;
        }
//        if (button == 0) {
//            this.setDragging(true);
//        }
        if (foundEditBox) { return true; }

        // checking checkboxes
        int y = this.topPos + this.topMarginMain + 3;
        int x = this.leftPos + 10;
        int size = 13;
        if (event.x() >= x - 1 && event.x() <= x + size + 1)
        {
            for (int i = 0; i < this.checkBoxValues.size(); i++)
            {
                if (event.y() >= y - 1 && event.y() <= y + size + 1)
                {
                    String current = this.checkBoxValues.get(i);
                    if (current.equals("e"))
                    { this.checkBoxValues.set(i, "y"); }
                    else if (current.equals("y"))
                    { this.checkBoxValues.set(i, "n"); }
                    else if (current.equals("n"))
                    { this.checkBoxValues.set(i, "q"); }
                    else if (current.equals("q"))
                    { this.checkBoxValues.set(i, "e"); }
                    this.greyOutDoneAndAbandoned(true, i);
                    break;
                }
                y = y + 12 + 12;
            }
        }
        // paging
        if (event.y() >= this.arrowNext1.getY() && event.y() <= this.arrowNext1.getY() + this.arrowNext1.getHeight())
        {
            int offset = 0;
            if (event.x() >= this.arrowNext1.getX() && event.x() <= this.arrowNext1.getX() + this.arrowNext1.getWidth() && this.page < this.pageCount)
            {
                offset = +1;
            }
            if (event.x() >= this.arrowPrev1.getX() && event.x() <= this.arrowPrev1.getX() + this.arrowPrev1.getWidth() && this.page > 1)
            {
                offset = -1;
            }
            if (offset != 0)
            {
                this.changePage(offset);
            }
        }
        return true;
    }

    private void changePage(int offset)
    {
        assert offset * offset == 1;
        TaskListMessaging.TaskPageDTO finishedPage = this.makeDTO();
        TaskListMessaging.sendPageToServer(finishedPage, this.makeExtra());
        this.pagesFromItem.set(this.page-1, finishedPage);
        this.footer = null;
        this.page += offset;
        this.initialContent = this.pagesFromItem.get(this.page-1);
        this.init();
        this.tooltipTicks = -1;
    }

    private void checkByKeyboard(int keyCode)
    {
        for (int i = 0; i < this.editBoxes.size(); i++)
        {
            if (this.editBoxes.get(i).isFocused())
            {
                int index = i / 2;
                String current = this.checkBoxValues.get(index);
                String newValue = "e";
                if (keyCode == K_PLU)
                {
                    newValue = current.equals("y") ? "e" : "y";
                }
                else if (keyCode == K_MIN)
                {
                    newValue = current.equals("n") ? "e" : "n";
                }
                else if (keyCode == K_STR)
                {
                    newValue = current.equals("q") ? "e" : "q";
                }
                if (! newValue.equals(current))
                {
                    this.checkBoxValues.set(index, newValue);
                    this.greyOutDoneAndAbandoned(true, index);
                }
            }
        }
    }

    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton) {
        return mouseX < (double)guiLeft || mouseY < (double)guiTop || mouseX >= (double)(guiLeft + this.imageWidth) || mouseY >= (double)(guiTop + this.imageHeight);
    }

    @Override
    public final void tick()
    {
        super.tick();
        if (! this.minecraft.player.isAlive() || this.minecraft.player.isRemoved()) {
            this.onClose();
        }
    }
    @Override
    public void onClose()
    {
        TaskListMessaging.sendPageToServer(this.makeDTO(), this.makeExtra());
        this.minecraft.player.closeContainer();
        super.onClose();
    }

    private TaskListMessaging.TaskPageDTO makeDTO()
    {
        TaskListMessaging.TaskPageDTO storedPage = new TaskListMessaging.TaskPageDTO(this.page, null);
        for (int i = 0; i < TaskListMessaging.ITEMS_PER_PAGE; i++)
        {
            if (2*i < this.editBoxes.size())
            {
                storedPage.items().set(i, new TaskListMessaging.TaskItemDTO(this.checkBoxValues.get(i), this.editBoxes.get(2 * i).getValue(), this.editBoxes.get(2 * i + 1).getValue()));
            }
            else
            {
                // we don't have 12 text boxes yet
                storedPage.items().set(i, TaskListMessaging.TaskItemDTO.empty());
            }
        }
        return storedPage;
    }

    private TaskListMessaging.TaskListExtraDTO makeExtra()
    {
        // main hand and block position in extra record are there to find item on server
        return new TaskListMessaging.TaskListExtraDTO(this.extraOriginal, this.page, this.header.getValue());
    }

    ////////////////////

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.untapsToSkip == 0)
        {
            this.setArrowVisibilityOnHover(mouseX, mouseY);
            this.untapsToSkip = 5;
        }
        this.untapsToSkip--;
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        for (Renderable renderable : this.renderables)
        {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }
        // hor lines
        int height = 12; // 14 if borders
        for (int i = 0; i < 5; i++)
        {
            guiGraphics.hLine(this.leftPos + 14, this.leftPos + this.imageWidth - 16, this.topPos + this.topMarginMain - 4 + (i+1)*(height+height-1+1), 0xff776666);
        }
        // page num
        if (this.footer == null)
        {
            this.footer = String.format("%d/%d", this.page, this.pageCount);
        }
        guiGraphics.drawString(Minecraft.getInstance().font, this.footer, this.leftPos + this.imageWidth / 2 - 12, this.topPos + this.imageHeight - 18, 0xff776666, false);
        // checkboxes
        int y = this.topPos + this.topMarginMain + 3;
        int x = this.leftPos + 10;
        int size = 13;
        for (int i = 0; i < this.checkBoxValues.size(); i++)
        {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.checkBoxImages.get(this.checkBoxValues.get(i)), x, y, 0.0F, 0.0F, size, size, 16, 16, 16, 16);
            y = y + 12 + 12;
        }
        // tooltip
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
    private int untapsToSkip = 5;

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BG_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight, 256, 256);
    }

    ///////////////////////////////


    private static final int K_ESC = 256, K_DN = 264, K_UP = 265, K_ENT = 257, K_ENT_NP = 335;
    private static final int K_PLU = 334, K_MIN = 333, K_STR = 332;
    private static final int K_PG_DN = 267, K_PG_UP = 266;

    @Override
    public boolean keyPressed(KeyEvent event)
    {
        if (event.key() == K_ESC && this.shouldCloseOnEsc())
        {
            this.onClose();
            return true;
        }
        if ((event.key() == K_DN || event.key() == K_UP)  && (event.modifiers() & 1) == 1)
        {
            // special focus
            if (this.header.equals(this.getFocused())) { return true; }
            this.setFocused(this.header);
            return true;
        }
        if (event.key() == K_DN || (event.key() == K_ENT && (event.modifiers() & 1) == 0) || (event.key() == K_ENT_NP && (event.modifiers() & 1) == 0))
        {
            // focus down
            for (int i = 0; i < this.editBoxes.size(); i++)
            {
                if (this.editBoxes.get(i).equals(this.getFocused()))
                {
                    if (i < this.editBoxes.size() - 1)
                    {
                        this.setFocused(this.editBoxes.get(i+1));
                    }
                    else
                    {
                        this.setFocused(this.editBoxes.get(0));
                    }
                    return true;
                }
            }
            this.setFocused(this.editBoxes.get(0));
            return true;
        }
        if (event.key() == K_UP || (event.key() == K_ENT && (event.modifiers() & 1) == 1) || (event.key() == K_ENT_NP && (event.modifiers() & 1) == 1))
        {
            // focus up
            for (int i = 0; i < this.editBoxes.size(); i++)
            {
                if (this.editBoxes.get(i).equals(this.getFocused()))
                {
                    if (i > 0)
                    {
                        this.setFocused(this.editBoxes.get(i-1));
                    }
                    else
                    {
                        this.setFocused(this.editBoxes.get(this.editBoxes.size()-1));
                    }
                    return true;
                }
            }
            this.setFocused(this.editBoxes.get(0)); // first focus
            return true;
        }
        if ((event.key() == K_PLU || event.key() == K_MIN || event.key() == K_STR) && ((event.modifiers() & 1) == 0))
        {
            this.checkByKeyboard(event.key());
            return true;
        }
        if (event.key() == K_PG_DN && this.page < this.pageCount)
        {
            // paging
            this.changePage(1);
            return true;
        }
        if (event.key() == K_PG_UP && this.page > 1)
        {
            // paging
            this.changePage(-1);
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean isPauseScreen()
    {
        return ClientConfig.taskListPausesSingleplayer;
    }
}
