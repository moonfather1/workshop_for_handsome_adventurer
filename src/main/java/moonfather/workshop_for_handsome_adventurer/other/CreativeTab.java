package moonfather.workshop_for_handsome_adventurer.other;

import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CreativeTab
{
    public static final CreativeModeTab TAB_WORKSHOP = new CreativeModeTab(Constants.MODID)
    {
        public ItemStack makeIcon()
        {
            return new ItemStack(Registration.blocks_table1.get(0).get());
        }
    };

}
