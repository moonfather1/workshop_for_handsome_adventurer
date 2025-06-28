package moonfather.workshop_for_handsome_adventurer.items.task_list.items.moving_data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskListComponent
{
    public static final int MAX_PAGE_COUNT = 4;
    private int pageCount, lastPage;
    private final List<TaskListMessaging.TaskPageDTO> pages = new ArrayList<>(MAX_PAGE_COUNT);

    //////////////////////////
    public int getPageCount() { return this.pageCount; }
    public int getLastPageNumber() { return this.lastPage; }
    //////////////////////////

    public TaskListComponent(int pageCount, int lastPage, List<TaskListMessaging.TaskPageDTO> pages)
    {
        this(pageCount, lastPage, pages, true);
    }
    public TaskListComponent(int pageCount, int lastPage, List<TaskListMessaging.TaskPageDTO> pages, boolean fullCopy)
    {
        this.pageCount = pageCount;
        this.lastPage = lastPage;
        while (this.pages.size() < MAX_PAGE_COUNT) { this.pages.add(null); }
        if (pages != null)
        {
            if (fullCopy)
            {
                for (int i = 0; i < MAX_PAGE_COUNT; i++)
                {
                    this.pages.set(i, new TaskListMessaging.TaskPageDTO(pages.get(i).pageNumber(), pages.get(i).items()));
                }
            }
            else
            {
                for (int i = 0; i < MAX_PAGE_COUNT; i++)
                {
                    this.pages.set(i, pages.get(i));
                }
            }
        }
        else
        {
            for (int i = 0; i < MAX_PAGE_COUNT; i++)
            {
                this.pages.set(i, new TaskListMessaging.TaskPageDTO(i+1, null));
                for (int j = 0; j < TaskListMessaging.ITEMS_PER_PAGE; j++)
                {
                    this.pages.get(i).items().set(j, TaskListMessaging.TaskItemDTO.empty());
                }
            }
        }
    }
    @Override
    public int hashCode()
    {
        Integer base = this.pageCount * 100 + this.lastPage;
        if (pageCount == 1) return Objects.hash(base, pages.get(0));
        if (pageCount == 2) return Objects.hash(base, pages.get(0), pages.get(1));
        if (pageCount == 3) return Objects.hash(base, pages.get(0), pages.get(1), pages.get(2));
        if (pageCount == 4) return Objects.hash(base, pages.get(0), pages.get(1), pages.get(2), pages.get(3));
        if (pageCount == 5) return Objects.hash(base, pages.get(0), pages.get(1), pages.get(2), pages.get(3), pages.get(3));
        if (pageCount == 6) return Objects.hash(base, pages.get(0), pages.get(1), pages.get(2), pages.get(3), pages.get(3), pages.get(5));
        return -1;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj instanceof TaskListComponent comp2)
        {
            if (this.lastPage != comp2.lastPage)
            {
                return false; // done to trigger updates when only page number changes
            }
            if (this.pageCount == comp2.pageCount)
            {
                for (int i = 0; i < this.pageCount; i++)
                {
                    if (! this.pages.get(i).equals(comp2.pages.get(i)))
                    {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    // ------------------------------------

    public TaskListComponent makeCopy(int skipPage, int lastPageFromScreen)
    {
        TaskListComponent copy = new TaskListComponent(this.pageCount, lastPageFromScreen,null);
        copy.pageCount = this.pageCount;
        copy.lastPage = this.lastPage;
        for (int i = 0; i < MAX_PAGE_COUNT; i++)
        {
            if (i+1 != skipPage)
            {
                copy.pages.set(i, new TaskListMessaging.TaskPageDTO(this.pages.get(i).pageNumber(), this.pages.get(i).items()));
            }
            else
            {
                copy.pages.set(i, new TaskListMessaging.TaskPageDTO(this.pages.get(i).pageNumber(), null));
            }
        }
        return  copy;
    }

    public void updatePage(int pageNumber, TaskListMessaging.TaskPageDTO value)
    {
        this.pages.set(pageNumber-1, new TaskListMessaging.TaskPageDTO(pageNumber, value.items()));
    }

    public TaskListComponent addPages(int pagesToAdd)
    {
        return new TaskListComponent(this.pageCount + pagesToAdd, this.lastPage, this.pages);
    }

    public List<TaskListMessaging.TaskPageDTO> getAllPages()
    {
        return this.pages;
    }

    public void setLastPage(int pageNumber) { this.lastPage = pageNumber; }
}
