package moonfather.workshop_for_handsome_adventurer.integration.mixins;

import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import invtweaks.util.Sorting;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(Sorting.class)
public class InventoryTweaks
{
    @Inject(method = "executeSort", at = @At("HEAD"), cancellable = true, remap = false)
    private static void check(Player player, boolean isPlayerSort, String screenClass, CallbackInfo ci)
    {
        if (! isPlayerSort && player != null && player.containerMenu instanceof SimpleTableMenu)
        {
            ci.cancel();
        }
    }
}