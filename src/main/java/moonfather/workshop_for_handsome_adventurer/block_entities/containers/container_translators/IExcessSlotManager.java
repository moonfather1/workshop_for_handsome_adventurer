package moonfather.workshop_for_handsome_adventurer.block_entities.containers.container_translators;

public interface IExcessSlotManager
{
    default boolean isSlotSpecificallyDisabled(int slotIndex) { return false; }
}
