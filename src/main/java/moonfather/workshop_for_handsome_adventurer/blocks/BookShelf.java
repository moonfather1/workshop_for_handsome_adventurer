package moonfather.workshop_for_handsome_adventurer.blocks;

import moonfather.workshop_for_handsome_adventurer.block_entities.BookShelfBlockEntity;
import moonfather.workshop_for_handsome_adventurer.initialization.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class BookShelf extends ToolRack
{
    public BookShelf(String subType)
    {
        super(INITIAL_BOOK_COUNT, "book_shelf", subType);
        this.registedDefaultState();
    }

    public BookShelf(String subType, Properties properties)
    {
        super(INITIAL_BOOK_COUNT, "book_shelf", subType, properties);
        this.registedDefaultState();
    }

    private static final int INITIAL_BOOK_COUNT = 12; // so that we don't have 8 hardcoded anywhere
    protected int numberOfBooksInARow() { return 4; }
    protected int numberOfRows() { return 2; }
    protected boolean singleRowIsTop() { return true; }

    ////////// main part///////////////////////

    public static int getBookShelfSlot(BookShelf block, BlockHitResult blockHitResult)
    {  // for the one probe
        return block.getTargetedSlot(blockHitResult);
    }

    @Override
    protected int getTargetedSlot(BlockHitResult blockHitResult)
    {
        int aboveThisRow;
        double frac = blockHitResult.getLocation().y - blockHitResult.getBlockPos().getY();
        if (this.numberOfRows() == 2)
        {
            if (frac >= 8 / 16d) { aboveThisRow = 0; /* row1*/ } else { aboveThisRow = this.numberOfBooksInARow(); /* row2*/ }
        }
        else
        {
            aboveThisRow = 0;
            if (frac > 8/16d && ! this.singleRowIsTop() || frac < 8/16d && this.singleRowIsTop())
            {
                return -1;
            }
        }

        int integral;
        integral = (int) blockHitResult.getLocation().z;
        frac = (blockHitResult.getLocation().z - integral) * blockHitResult.getDirection().getStepX();
        integral = (int) blockHitResult.getLocation().x;
        frac -= (blockHitResult.getLocation().x - integral) * blockHitResult.getDirection().getStepZ();
        while (frac < 0) frac += 1.0;
        while (frac > 1) frac -= 1.0;
        int horizontal = this.numberOfBooksInARow() - 1 - (int) Math.floor(frac * this.numberOfBooksInARow()); // 3-Ax4 ---- x4 turns quarters of a block into slots, 3 minus slot turns indices around because i did the above math backwards

        return aboveThisRow + horizontal;
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult)
    {
        if (hand == InteractionHand.OFF_HAND)
        {
            return InteractionResult.PASS;
        }
        if (level.isClientSide)
        {
            return InteractionResult.SUCCESS;
        }

        int slot = this.getTargetedSlot(blockHitResult);
        if (slot >= this.itemCount)
        {
            slot -= this.itemCount;
        }
        if (slot == -1)
        {
            return InteractionResult.CONSUME;
        }

        BookShelfBlockEntity BE = ((BookShelfBlockEntity) level.getBlockEntity(pos));
        ItemStack existing = BE.GetItem(slot);
        ItemStack itemInMainHand = player.getMainHandItem();
        ItemStack itemInOffHand = player.getOffhandItem();

        if (existing.isEmpty())
        {
            if (this.canDepositItem(itemInMainHand))
            {
                doPlaceBookOntoShelf(player, itemInMainHand, slot, BE, level, pos, blockState);
            }
            else if (this.canDepositItem(itemInOffHand))
            {
                doPlaceBookOntoShelf(player, itemInOffHand, slot, BE, level, pos, blockState);
            }
            else if (! itemInMainHand.isEmpty() || ! itemInOffHand.isEmpty())
            {
                player.displayClientMessage(NotABookMessage, true);
            }
        }
        else if (! existing.isEmpty() && itemInMainHand.isEmpty())
        {
            doPickBookFromShelf(player, InteractionHand.MAIN_HAND, existing, slot, BE, level, pos, blockState);
        }
        else if (! existing.isEmpty() && ! itemInMainHand.isEmpty() && itemInOffHand.isEmpty())
        {
            doPickBookFromShelf(player, InteractionHand.OFF_HAND, existing, slot, BE, level, pos, blockState);
        }
        return InteractionResult.CONSUME;
    }
    private final MutableComponent NotABookMessage = Component.translatable("message.workshop_for_handsome_adventurer.invalid_item_for_book_shelf");

    private static void doPickBookFromShelf(Player player, InteractionHand hand, ItemStack bookInShelfSlot, int slot, BookShelfBlockEntity BE, Level level, BlockPos pos, BlockState blockState)
    {
        player.setItemInHand(hand, bookInShelfSlot); // normal
        BE.ClearItem(slot);
        player.playSound(SoundEvents.ITEM_PICKUP, 0.5f, 1);
        level.setBlockAndUpdate(pos, blockState.setValue(getSlotProperty(slot), false));
    }

    private static void doPlaceBookOntoShelf(Player player, ItemStack itemToStore, int slot, BookShelfBlockEntity BE, Level level, BlockPos pos, BlockState blockState)
    {
        ItemStack toStore = itemToStore.copy();
        toStore.setCount(1);
        BE.DepositItem(slot, toStore);
        itemToStore.shrink(1);
        player.playSound(SoundEvents.BOOK_PUT, 0.5f, 0.7f);
        level.setBlockAndUpdate(pos, blockState.setValue(getSlotProperty(slot), true));
    }

    @Override
    protected boolean canDepositItem(ItemStack mainHandItem)
    {
        return mainHandItem.is(ItemTags.BOOKSHELF_BOOKS);
    }

    //////////////// block entity /////////////

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState)
    {
        BookShelfBlockEntity BE = Registration.BOOK_SHELF_BE.get().create(pos, blockState);
        BE.setCapacity(this.getBookCapacity());
        return BE;
    }

    private int getBookCount() { return this.numberOfRows() * this.numberOfBooksInARow(); }
    private int getBookCapacity() { return Math.max(this.numberOfRows() * this.numberOfBooksInARow(), 8); }

    //////////////// flammability ///////////////////

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) { return 5; }
    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) { return 20; }

    //////////////// states ///////////////////

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        if (SLOT_OCCUPIED[0] == null)
        {
            int slot_count = this.getBookCapacity();
            for (int i = 0; i < slot_count; i++)
            {
                SLOT_OCCUPIED[i] = BooleanProperty.create("slot_" + i + "_occupied");
            }
        }
        for (int i = 0; i < this.getBookCount(); i++)
        {
            builder.add(SLOT_OCCUPIED[i]);
        }
    }
    public static final BooleanProperty[] SLOT_OCCUPIED = new BooleanProperty[20];
    private static BooleanProperty getSlotProperty(int slot) { return SLOT_OCCUPIED[slot]; }



    private void registedDefaultState()
    {
        BlockState state = this.defaultBlockState().setValue(FACING, Direction.NORTH);
        for (int i = 0; i < this.getBookCount(); i++)
        {
            state = state.setValue(getSlotProperty(i), false);
        }
        this.registerDefaultState(state);
    }



    ////////////// actual variants ///////////

    public static class Dual extends BookShelf
    {
        public Dual(String type)
        {
            super(type);
        }

        @Override
        protected void PrepareListOfShapes()
        {
            this.shapes.clear();
            this.shapes.put(Direction.NORTH, SHAPE_FRAME1N);
            this.shapes.put(Direction.EAST,  SHAPE_FRAME1E);
            this.shapes.put(Direction.SOUTH, SHAPE_FRAME1S);
            this.shapes.put(Direction.WEST,  SHAPE_FRAME1W);
        }
        private static final VoxelShape SHAPE_FRAME1N = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.99D, 7.0D);
        private static final VoxelShape SHAPE_FRAME1E = Block.box(9.0D, 0.0D, 0.0D, 16.0D, 15.99D, 16.0D);
        private static final VoxelShape SHAPE_FRAME1S = Block.box(0.0D, 0.0D, 9.0D, 16.0D, 15.99D, 16.0D);
        private static final VoxelShape SHAPE_FRAME1W = Block.box(0.0D, 0.0D, 0.0D,  7.0D, 15.99D, 16.0D);
    }

    public static class TopSimple extends BookShelf
    {
        public TopSimple(String type)
        {
            super(type);
        }

        @Override
        protected int numberOfRows() { return 1; }

        @Override
        protected void PrepareListOfShapes()
        {
            this.shapes.clear();
            this.shapes.put(Direction.NORTH, SHAPE_FRAME1N);
            this.shapes.put(Direction.EAST,  SHAPE_FRAME1E);
            this.shapes.put(Direction.SOUTH, SHAPE_FRAME1S);
            this.shapes.put(Direction.WEST,  SHAPE_FRAME1W);
        }
        private static final VoxelShape SHAPE_FRAME1N = Block.box(0.0D, 7.5D, 0.0D, 16.0D, 16.0D, 7.0D);
        private static final VoxelShape SHAPE_FRAME1E = Block.box(9.0D, 7.5D, 0.0D, 16.0D, 16.0D, 16.0D);
        private static final VoxelShape SHAPE_FRAME1S = Block.box(0.0D, 7.5D, 9.0D, 16.0D, 16.0D, 16.0D);
        private static final VoxelShape SHAPE_FRAME1W = Block.box(0.0D, 7.5D, 0.0D,  7.0D, 16.0D, 16.0D);
    }

    public static class TopWithLanterns extends BookShelf
    {
        public TopWithLanterns(String type)
        {
            super(type, Properties.of().strength(2f, 3f).sound(SoundType.WOOD).ignitedByLava().mapColor(MapColor.COLOR_BROWN).pushReaction(PushReaction.DESTROY).lightLevel(TopWithLanterns::getLightLevel));
        }

        ////////////////////////

        private static int getLightLevel(BlockState state)
        {
            return state.getValue(LIGHTS_ON) ? 12 : 0;
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
        {
            super.createBlockStateDefinition(builder);
            builder.add(LIGHTS_ON);
        }

        public static final BooleanProperty LIGHTS_ON = BooleanProperty.create("is_lit"); // on/off
        ////////////////////////////

        @Override
        protected int numberOfRows() { return 1; }

        @Override
        protected void PrepareListOfShapes()
        {
            this.shapes.clear();
            this.shapes.put(Direction.NORTH, Shapes.or(SHAPE_FRAME1N, SHAPE_LAMPS1N));
            this.shapes.put(Direction.EAST,  Shapes.or(SHAPE_FRAME1E, SHAPE_LAMPS1E));
            this.shapes.put(Direction.SOUTH, Shapes.or(SHAPE_FRAME1S, SHAPE_LAMPS1S));
            this.shapes.put(Direction.WEST,  Shapes.or(SHAPE_FRAME1W, SHAPE_LAMPS1W));
        }
        private static final VoxelShape SHAPE_FRAME1N = Block.box( 0.0D, 7.5D,  0.0D, 16.0D, 16.0D,  7.0D);
        private static final VoxelShape SHAPE_FRAME1E = Block.box( 9.0D, 7.5D,  0.0D, 16.0D, 16.0D, 16.0D);
        private static final VoxelShape SHAPE_FRAME1S = Block.box( 0.0D, 7.5D,  9.0D, 16.0D, 16.0D, 16.0D);
        private static final VoxelShape SHAPE_FRAME1W = Block.box( 0.0D, 7.5D,  0.0D,  7.0D, 16.0D, 16.0D);
        private static final VoxelShape SHAPE_LAMPS1N = Block.box( 2.5D, 3.0D,  0.0D, 13.5D,  7.5D,  4.0D);
        private static final VoxelShape SHAPE_LAMPS1E = Block.box(12.0D, 3.0D,  2.5D, 16.0D,  7.5D, 13.5D);
        private static final VoxelShape SHAPE_LAMPS1S = Block.box( 2.5D, 3.0D, 12.0D, 13.5D,  7.5D, 16.0D);
        private static final VoxelShape SHAPE_LAMPS1W = Block.box( 0.0D, 3.0D,  2.5D,  4.0D,  7.5D, 13.5D);

        @Override
        public InteractionResult use(BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult)
        {
            if (hand == InteractionHand.OFF_HAND)
            {
                return InteractionResult.PASS;
            }
            if (level.isClientSide)
            {
                return InteractionResult.SUCCESS;
            }
            int slot = this.getTargetedSlot(blockHitResult);
            if (slot == -1)
            {
                level.setBlockAndUpdate(pos, blockState.setValue(LIGHTS_ON, ! blockState.getValue(LIGHTS_ON)));
                return InteractionResult.CONSUME;
            }
            return super.use(blockState, level, pos, player, hand, blockHitResult);
        }
    }
}
