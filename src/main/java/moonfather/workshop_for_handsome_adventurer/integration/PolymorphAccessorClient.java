package moonfather.workshop_for_handsome_adventurer.integration;

import com.illusivesoulworks.polymorph.api.PolymorphApi;
import com.illusivesoulworks.polymorph.api.client.PolymorphWidgets;
import com.illusivesoulworks.polymorph.api.client.widgets.PlayerRecipesWidget;
import com.illusivesoulworks.polymorph.client.RecipesWidget;
import moonfather.workshop_for_handsome_adventurer.block_entities.SimpleTableMenu;
import moonfather.workshop_for_handsome_adventurer.block_entities.messaging.PacketSender;
import moonfather.workshop_for_handsome_adventurer.block_entities.screens.SimpleTableCraftingScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;

public class PolymorphAccessorClient
{
    public static void setTargetSlot(Slot slot)
    {
        RecipesWidget.get().ifPresent(
                (widget) ->
                {
                    if (widget instanceof PolymorphAccessorClient.OurPlayerRecipesWidget ourWidget)
                    {
                        ourWidget.setDestinationSlot(slot);
                    }
                }
        );
    }

    public static void updatePosition()
    {
        RecipesWidget.get().ifPresent(
                (widget) ->
                {
                    if (widget instanceof PolymorphAccessorClient.OurPlayerRecipesWidget ourWidget)
                    {
                        ourWidget.updatePosition();
                    }
                }
        );
    }



    public static void register()
    {
        PolymorphWidgets.getInstance().registerWidget(
                containerScreen ->
                {
                    if (containerScreen instanceof SimpleTableCraftingScreen)
                    {
                        return new OurPlayerRecipesWidget(containerScreen, containerScreen.getMenu().slots.get(SimpleTableMenu.RESULT_SLOT));
                    }
                    return null;
                }
        );
    }

    //////////////////////////

    private static class OurPlayerRecipesWidget extends PlayerRecipesWidget
    {
        public OurPlayerRecipesWidget(AbstractContainerScreen<?> containerScreen, Slot outputSlot)
        {
            super(containerScreen, outputSlot);
            this.output = outputSlot;
        }

        public void setDestinationSlot(Slot newOutput)
        {
            this.output = newOutput;
            this.resetWidgetOffsets();
        }
        private Slot output;

        @Override
        public Slot getOutputSlot()
        {
            return this.output;
        }

        public void updatePosition()
        {
            this.resetWidgetOffsets();
            this.openButton.setPosition(this.getXPos(), this.getYPos());
        }

        ////////

//        @Override
//        public void selectRecipe(Identifier resourceLocation)
//        {
//            super.selectRecipe(resourceLocation);
//            PacketSender.sendCraftingResultUpdateRequestToServer(this.getOutputSlot().index);
//        }
    }
}
