package moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities;

import moonfather.workshop_for_handsome_adventurer.items.task_list.RegistrationForTaskList;
import moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities.renderers.RenderStateManagement;
import moonfather.workshop_for_handsome_adventurer.items.task_list.blocks.TaskListPanel;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.TaskListItem;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListComponent;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListMessaging;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.screens.TaskListClientInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TaskListBlockEntity extends BasicBlockEntity implements Nameable
{
    public TaskListBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) { super(type, pos, blockState); }
    public TaskListBlockEntity(BlockPos pos, BlockState blockState) { super(RegistrationForTaskList.TASK_LIST_PANEL_BE.get(), pos, blockState); }

    /////////////////////////////////////////////////////

    private int currentPage;
    private TaskListComponent data;
    private String title = "asdfg";
    private Component titleAsComponent = null;
    private boolean isFireResistant = false;
    private ItemStack item;
    private boolean isModified = false;

    public void setItem(ItemStack taskList)
    {
        this.item = taskList;
        this.data = taskList.get(TaskListItem.Utility.ourComponent());
        if (this.data == null) { this.data = new TaskListComponent(1, 1, null); }
        this.currentPage = this.data.getLastPageNumber();
        this.title = TaskListItem.Utility.getTitle(this.item);
        this.isFireResistant = TaskListItem.Utility.isFireImmune(this.item);
        this.titleAsComponent = null;
        this.setChanged();
    }

    public int getCurrentPage() { return this.currentPage; }

    public void setCurrentPage(int newPage) { this.currentPage = newPage; }
    private static final TaskListMessaging.TaskPageDTO EMPTY = new TaskListMessaging.TaskPageDTO(1, null);
    public TaskListMessaging.TaskPageDTO getPageForDisplay() { if (this.data == null) return EMPTY;  return this.data.getAllPages().get(this.currentPage - 1); }
    public int getPageCount() { if (this.data == null) return 7;  return this.data.getPageCount(); }
    public boolean isFireResistant() { return this.isFireResistant; }
    public String getTitle() { return title; }

    /////////////////////////////////////////////////////


    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        this.isModified = input.getBooleanOr("isModified", false);
        Optional<ItemStack> storedItem = input.read("original", ItemStack.CODEC);
        if (storedItem.isPresent())
        {
            this.setItem(storedItem.get());
            this.setCurrentPage(input.getIntOr("currentPage", 1));
            return;
        }
        this.setItem(TaskListItem.Utility.createInstance());
        this.setCurrentPage(1);

        Optional<int[]> checkmarks = input.getIntArray("checkmarks");
        if (checkmarks.isPresent())
        {
            for (int i = 0; i < TaskListComponent.MAX_PAGE_COUNT; i++)
            {
                for (int j = 0; j < TaskListMessaging.ITEMS_PER_PAGE; j++)
                {
                    if (this.data.getAllPages().get(i).items().get(j).status().codePointAt(0) != checkmarks.get()[i*TaskListMessaging.ITEMS_PER_PAGE+j])
                    {
                        this.data.getAllPages().get(i).items().set(j, new TaskListMessaging.TaskItemDTO(Character.toString(checkmarks.get()[i*TaskListMessaging.ITEMS_PER_PAGE+j]), this.data.getAllPages().get(i).items().get(j).line1(), this.data.getAllPages().get(i).items().get(j).line2()));
                    }
                }
            }
        }
    }

    @Override
    protected ValueOutput saveInternal(ValueOutput output)
    {
        output.putBoolean("isModified", this.isModified);
        output.putInt("currentPage", this.getCurrentPage()); // first one;  need to return changed page back.
        output.store("original", ItemStack.CODEC, this.item);
        int[] checkmarks = new int[TaskListMessaging.ITEMS_PER_PAGE * TaskListComponent.MAX_PAGE_COUNT];
        int k = 0;
        for (int i = 0; i < TaskListComponent.MAX_PAGE_COUNT; i++)
        {
            for (int j = 0; j < TaskListMessaging.ITEMS_PER_PAGE; j++)
            {
                checkmarks[k++] = this.data.getAllPages().get(i).items().get(j).status().codePointAt(0);
            }
        }
        output.putIntArray("checkmarks", checkmarks);
        return output;
    }

    public String getFooter()
    {
        if (this.data == null) return "1/8"; // sometimes rendering jumps ahead of setting data
        if (this.footer == null)
        {
            this.footer = this.getCurrentPage() + "/" + this.getPageCount();
        }
        return this.footer;
    }
    private String footer = null; // for TESR; client only.

    //-----------------------------------------

    public void onArrowPrev()  // both sides
    {
        if (this.getCurrentPage() > 1)
        {
            this.setCurrentPage(this.getCurrentPage() - 1);
            this.setChanged();
        }
    }
    public void onArrowNext()  // both sides
    {
        if (this.getCurrentPage() < this.getPageCount())
        {
            this.setCurrentPage(this.getCurrentPage() + 1);
            this.setChanged();
        }
    }
    public void onClientArrowNext()  // client only method
    {
        if (this.getCurrentPage() < this.getPageCount())
        {
            this.onArrowNext();
            this.footer = null;
            TaskListMessaging.sendBlockClickPageRightToServer(this.getBlockPos());
            this.setCachedRenderState(null);
        }
    }
    public void onClientArrowPrev()  // client only method
    {
        if (this.getCurrentPage() > 1)
        {
            this.onArrowPrev();
            this.footer = null;
            TaskListMessaging.sendBlockClickPageLeftToServer(this.getBlockPos());
            this.setCachedRenderState(null);
        }
    }

    public void checkmarkClickedOnBlock(int index)  // client thing
    {
        this.updateCheckmark(index, this.getCurrentPage()); // try calling a server method here. will be called on the server in a few moments too.
        TaskListMessaging.sendBlockClickCheckmarkServer(this.getBlockPos(), index, this.getCurrentPage());
        this.setCachedRenderState(null);
    }



    public void updateCheckmark(int index, int pageNumber)   // server only method      // 0-based and 1-based
    {
        if (this.getCurrentPage() != pageNumber) // shouldn't happen
        {
            this.setCurrentPage(pageNumber);
        }
        TaskListMessaging.TaskItemDTO old = this.data.getAllPages().get(pageNumber - 1).items().get(index);
        String newValue = switch (old.status())
        {
            case "e" -> "y";
            case "y" -> "n";
            case "n" -> "q";
            default -> "e";
        };
        this.data.getAllPages().get(pageNumber-1).items().set(index, new TaskListMessaging.TaskItemDTO(newValue, old.line1(), old.line2()));
        this.setChanged();
    }



    public void invokeGUI()
    {
        TaskListMessaging.TaskListExtraDTO extra = new TaskListMessaging.TaskListExtraDTO(this.getBlockPos(), this.data.getLastPageNumber(), this.getTitle());
        TaskListClientInvoker.invokeScreen(this.data.getAllPages(), this.data.getPageCount(), extra, this.isFireResistant);
    }


    @Nullable
    @Override
    public Component getCustomName()
    {
        if (this.titleAsComponent == null)
        {
            this.titleAsComponent = Component.literal(this.title);
        }
        return this.titleAsComponent;
    }

    @Override
    public Component getName()
    {
        if (this.titleAsComponent == null)
        {
            this.titleAsComponent = Component.literal(this.title);
        }
        return this.titleAsComponent;
    }

    public ItemStack getItem() { return this.item; }

    private ItemStack getItemForDrop()
    {
        ItemStack result = this.item.copy();
        // should check isModified here. unused.
        this.data.setLastPage(this.currentPage);
        result.set(TaskListItem.Utility.ourComponent(), this.data);
        TaskListItem.Utility.setTitle(result, this.title);
        return result;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state)
    {
        super.preRemoveSideEffects(pos, state);
        Block.popResourceFromFace(this.level, pos, state.getValue(TaskListPanel.FACING), this.getItemForDrop());
    }

    // rendering support // // //

    public RenderStateManagement.Page getCachedRenderState() { return this.cachedRenderState; }
    private RenderStateManagement.Page cachedRenderState = null;
    public void setCachedRenderState(RenderStateManagement.Page value) { this.cachedRenderState = value; }
}
