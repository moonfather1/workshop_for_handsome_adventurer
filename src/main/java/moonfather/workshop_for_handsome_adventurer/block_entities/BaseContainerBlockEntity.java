package moonfather.workshop_for_handsome_adventurer.block_entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class BaseContainerBlockEntity extends BlockEntity
{
    public BaseContainerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState)
    {
        super(blockEntityType, blockPos, blockState);
    }

    ///////////////////////////////////

    private final List<ItemStack> items = new ArrayList<ItemStack>(9);
    protected int capacity = 9;

    protected void VerifyCapacity()
    {
        for (int i = this.items.size(); i < this.capacity; i++) { this.items.add(ItemStack.EMPTY); }
    }

    ///////////////////////////////////

    @Override
    public void load(CompoundTag compoundTag)
    {
        super.load(compoundTag);
        this.VerifyCapacity();
        for (int i = 0; i < this.capacity; i++)
        {
            CompoundTag tag = compoundTag.getCompound("item" + i);
            if (tag.contains("id"))
            {
                this.items.set(i, ItemStack.of(tag));
            }
            else
            {
                this.items.set(i, ItemStack.EMPTY);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag)
    {
        super.saveAdditional(compoundTag);
        this.saveInternal(compoundTag);
    }

    public CompoundTag saveInternal(CompoundTag compoundTag)
    {
        this.VerifyCapacity();
        for (int i = 0; i < this.capacity; i++) { compoundTag.put("item" + i, this.items.get(i).save(new CompoundTag())); }
        return compoundTag;
    }

    ////////////////////////////////////////////////////////////

    @Override
    public void handleUpdateTag(CompoundTag tag)
    {
        this.load(tag); // update client
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        return this.saveInternal(new CompoundTag()); //send to client
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // Will get tag from #getUpdateTag
        return ClientboundBlockEntityDataPacket.create(this);
    }

    ////////////////////////////////////////////////////////////

    public void DropAll()
    {
        this.VerifyCapacity();
        for (int i = 0; i < this.capacity; i++)
        {
            Block.popResource(this.level, this.getBlockPos(), this.items.get(i));
            this.ClearItem(i);
        }
    }

    public ItemStack GetItem(int slot)
    {
        this.VerifyCapacity();
        return this.items.get(slot);
    }

    public void DepositItem(int slot, ItemStack itemStack)
    {
        this.VerifyCapacity();
        this.items.set(slot, itemStack);
        this.setChanged();
    }

    public void ClearItem(int slot)
    {
        this.VerifyCapacity();
        this.items.set(slot, ItemStack.EMPTY);
        this.setChanged();
    }
}
