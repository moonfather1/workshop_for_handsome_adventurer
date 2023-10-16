package moonfather.workshop_for_handsome_adventurer.block_entities;

import net.minecraft.world.inventory.DataSlot;

import java.util.function.Consumer;

public class SimpleTableDataSlots
{
    public static final int DATA_SLOT_TABS_NEED_UPDATE = 0; // flag // tabs needing update on client. yeah this used to work but i won't bother debugging, i'll do it correctly here.
    public static final int DATA_SLOT_LOWER_ACCESS_NEEDS_UPDATE = 1;  // flag // lower half of double chest needing update on client.
    public static final int DATA_SLOT_UPPER_CONTAINER_TRUE_SIZE = 2; // this one isn't a flag, it's a value. // used for excess slots for small containers.
    public static final int DATA_SLOT_SLOTS_00_TO_26_EXCESS = 3; // this one isn't a flag, it's a value.
    public static final int DATA_SLOT_SLOTS_27_TO_53_EXCESS = 4; // this one isn't a flag, it's a value.
    public static final int DATA_SLOT_JEI_RECIPE_TARGET = 5; // two values but not a flag; dual table only but added to both.
    private static final int COUNT = 6;

    /////////////  instance  //////////////////////////

    public SimpleTableDataSlots(SimpleTableMenu menu)
    {
        this.menu = menu;
        this.slots = new DualTableMenu.DataSlotWithNotification[COUNT];
        for (int i = 0; i < COUNT; i++)
        {
            this.slots[i] = new DualTableMenu.DataSlotWithNotification(0);
        }
    }

    private final SimpleTableMenu menu;

    /////////////  events  /////////////////////////

    public void registerClientHandlerForDataSlot(int dataSlot, Consumer<Integer> event)
    {
        DualTableMenu.DataSlotWithNotification slot = null;
        slot = this.slots[dataSlot];  // NPE is fine on not found
        slot.setEvent(event);
    }

    /////////// flags //////////////////////////////////////

    public void resetDataSlotFlagForClientFlag(int dataSlot)
    {
        DataSlot slot = null;
        if (this.isFlagSlot(dataSlot))
        {
            slot = this.slots[dataSlot];
        }
        int old = slot.get();  // NPE is fine on not found
        int newValue = old % 2 == 1 ? old + 1 : old + 2;
        slot.set(newValue);
        this.menu.synchronizeDataSlotToRemote(dataSlot, newValue); // even == reset
    }

    private boolean isFlagSlot(int dataSlot)
    {
        return dataSlot == DATA_SLOT_TABS_NEED_UPDATE || dataSlot == DATA_SLOT_LOWER_ACCESS_NEEDS_UPDATE;
    }

    public void raiseDataSlotFlagForClientFlag(int dataSlot)
    {
        DataSlot slot = null;
        if (this.isFlagSlot(dataSlot))
        {
            slot = this.slots[dataSlot];
        }
        int old = slot.get(); // NPE is fine on not found
        int newValue = old % 2 == 1 ? old + 2 : old + 1;
        slot.set(newValue);
        //this.sendAllDataToRemote();  // this causes us to "lose" carried item
        this.menu.synchronizeDataSlotToRemote(dataSlot, newValue);  // odd == flag up
    }

    ///////////////// values /////////////////////////

    public int getSlotValue(int index)
    {
        return this.slots[index].get();
    }

    public void setSlotValue(int index, int value)
    {
        if (this.slots[index].get() != value)
        {
            this.slots[index].set(value);
        }
    }

    ///////////////////////////// data slots ///////////////

    public void addSlots()
    {
        for (int i = 0; i < COUNT; i++)
        {
            this.menu.addDataSlot(this.slots[i]);
        }
    }

    private final DualTableMenu.DataSlotWithNotification[] slots;
}
