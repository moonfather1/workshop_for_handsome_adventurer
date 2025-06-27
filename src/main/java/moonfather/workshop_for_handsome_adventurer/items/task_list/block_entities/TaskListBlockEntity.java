package moonfather.workshop_for_handsome_adventurer.items.task_list.block_entities;

import moonfather.workshop_for_handsome_adventurer.items.task_list.RegistrationForTaskList;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.TaskListItem;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListComponent;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data.TaskListMessaging;
import moonfather.workshop_for_handsome_adventurer.items.task_list.items.screens.TaskListClientInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        this.isModified = tag.getBoolean("isModified");
        if (tag.contains("original"))
        {
            Optional<ItemStack> op = ItemStack.parse(registries, tag.get("original"));
            if (op.isPresent())
            {
                this.setItem(op.get());
                this.setCurrentPage(tag.getInt("currentPage"));
                return;
            }
        }
        this.setItem(TaskListItem.Utility.createInstance());
        this.setCurrentPage(1);
        if (tag.contains("checkmarks"))
        {
            int[] checkmarks = tag.getIntArray("checkmarks");
            for (int i = 0; i < TaskListComponent.MAX_PAGE_COUNT; i++)
            {
                for (int j = 0; j < TaskListMessaging.ITEMS_PER_PAGE; j++)
                {
                    if (this.data.getAllPages().get(i).items().get(j).status().codePointAt(0) != checkmarks[i*TaskListMessaging.ITEMS_PER_PAGE+j])
                    {
                        this.data.getAllPages().get(i).items().set(j, new TaskListMessaging.TaskItemDTO(Character.toString(checkmarks[i*TaskListMessaging.ITEMS_PER_PAGE+j]), this.data.getAllPages().get(i).items().get(j).line1(), this.data.getAllPages().get(i).items().get(j).line2()));
                    }
                }
            }
        }
    }

    @Override
    protected CompoundTag saveInternal(CompoundTag compoundTag, HolderLookup.Provider lookupProvider)
    {
        compoundTag.putBoolean("isModified", this.isModified);
        compoundTag.putInt("currentPage", this.getCurrentPage()); // first one;  need to return changed page back.
        compoundTag.put("original", this.item.save(lookupProvider));
        ArrayList<Integer> checkmarks = new ArrayList<>(TaskListMessaging.ITEMS_PER_PAGE * TaskListComponent.MAX_PAGE_COUNT);
        for (int i = 0; i < TaskListComponent.MAX_PAGE_COUNT; i++)
        {
            for (int j = 0; j < TaskListMessaging.ITEMS_PER_PAGE; j++)
            {
                checkmarks.add(this.data.getAllPages().get(i).items().get(j).status().codePointAt(0));
            }
        }
        compoundTag.putIntArray("checkmarks", checkmarks);
        return compoundTag;
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
        }
    }
    public void onClientArrowPrev()  // client only method
    {
        if (this.getCurrentPage() > 1)
        {
            this.onArrowPrev();
            this.footer = null;
            TaskListMessaging.sendBlockClickPageLeftToServer(this.getBlockPos());
        }
    }

    public void checkmarkClickedOnBlock(int index)  // client thing
    {
        this.updateCheckmark(index, this.getCurrentPage()); // try calling a server method here. will be called on the server in a few moments too.
        TaskListMessaging.sendBlockClickCheckmarkServer(this.getBlockPos(), index, this.getCurrentPage());
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
        TaskListClientInvoker.invokeScreen(this.data.getAllPages(), this.data.getPageCount(), extra);
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

    public ItemStack getItemForDrop()
    {
        ItemStack result = this.item.copy();
        // should check isModified here. unused.
        this.data.setLastPage(this.currentPage);
        result.set(TaskListItem.Utility.ourComponent(), this.data);
        TaskListItem.Utility.setTitle(result, this.title);
        return result;
    }
}
