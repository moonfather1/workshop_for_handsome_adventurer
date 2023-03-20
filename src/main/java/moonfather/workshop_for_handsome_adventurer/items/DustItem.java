package moonfather.workshop_for_handsome_adventurer.items;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DustItem extends Item
{
    public DustItem() {
        super(new Properties());
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int index, boolean isSelected) {
        if (entity instanceof Player p)
        {
            p.getInventory().removeItemNoUpdate(index);
        }
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, Level level) {
        return 5; // ticks. 5 * 1/20sec
    }
}
