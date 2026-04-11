package moonfather.workshop_for_handsome_adventurer.block_entities.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.ToolRackBlockEntity;
import moonfather.workshop_for_handsome_adventurer.blocks.ToolRack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;

@ParametersAreNonnullByDefault
public class ToolRackTESR implements BlockEntityRenderer<ToolRackBlockEntity, ToolRackTESR.RackRenderState>
{
	protected static final TagKey<Item> TAG_DONT_ROTATE_ON_TOOLRACK = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MODID, "dont_rotate_on_toolrack"));
	protected static final TagKey<Item> TAG_ROTATE_180_ON_TOOLRACK = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MODID, "rotate_180_on_toolrack"));
	protected static final TagKey<Item> TAG_LARGER_ON_TOOLRACK_150 = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MODID, "larger_on_toolrack_150_percent"));
	protected static final TagKey<Item> TAG_LARGER_ON_TOOLRACK_125 = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MODID, "larger_on_toolrack_125_percent"));



	public ToolRackTESR(BlockEntityRendererProvider.Context context)
	{
		this.itemModelResolver = context.itemModelResolver();
	}
	private final ItemModelResolver itemModelResolver;


	// item to render state
	private void storeItemStackSpecifics(ItemStack itemStack, ToolRackItemRenderState renderState)
	{
		if (itemStack.isEmpty()) { return; }
//			BakedModel model = this.itemRenderer. getModel(itemStack, tile.getLevel(), null, combinedLight);
		if (itemStack.is(TAG_LARGER_ON_TOOLRACK_150)) // first this. apply the rest on top of this.
		{
			renderState.larger150 = true;
		}
		else if (itemStack.is(TAG_LARGER_ON_TOOLRACK_125))
		{
			renderState.larger125 = true;
		}

		if (itemStack.is(TAG_ROTATE_180_ON_TOOLRACK))
		{
			renderState.rotate180 = true;
		}

		if (itemStack.is(TAG_DONT_ROTATE_ON_TOOLRACK))
		{
			renderState.noDefaultRotation = true;
		}
		else if (itemStack.has(DataComponents.BLOCKS_ATTACKS))
		{
			renderState.blocksAttacks = true;
		}
		else if (itemStack.getItem().canPerformAction(itemStack, ItemAbilities.SWORD_SWEEP)
				|| itemStack.is(ItemTags.SWORDS))  // ?     || itemStack.has(DataComponents.WEAPON)
		{
			renderState.swordLike = true;
		}
		else if (itemStack.is(ItemTags.SPEARS))
		{
			renderState.spearLike = true;
		}
		else if (itemStack.getItem().getClass().getSimpleName().contains("rossbow") || itemStack.getItem() instanceof CrossbowItem)  //ModularCrossbowItem
		{
			renderState.crossbowLike = true;
		}
//			else if (model.isGui3d())
//			{
//				3d = true;
//			}
		else if (itemStack.get(DataComponents.POTION_CONTENTS) != null || itemStack.is(Items.GLASS_BOTTLE))
		{
			renderState.bottle = true;
		}
		else
		{

		}
	}

	// render state to pose stack
	protected void doSpecialPoseStackAdjustments(ToolRackItemRenderState state, PoseStack matrixStack)
	{
		matrixStack.mulPose(XMinus90);  // 1.19.4   Vector3f.XP.rotationDegrees(-90.0F)
		matrixStack.mulPose(YPlus180);  // 1.19.4   Vector3f.YP.rotationDegrees(180.0F)

		if (state.larger150) // first this. apply the rest on top of this.
		{
			// seems to be noo need for matrixStack.translate
			matrixStack.scale(1.50f, 1.10f, 1.50f);
		}
		else if (state.larger125)
		{
			// seems to be noo need for matrixStack.translate
			matrixStack.scale(1.25f, 1.10f, 1.25f);
		} // scaling is separate from main thing below.

		if (state.rotate180)
		{
			matrixStack.mulPose(ZPlus180);
		} // this rotation is separate as it's added onto any rotation below

		if (state.noDefaultRotation)
		{
			matrixStack.translate(0, 0.1, 0);
		}
		else if (state.blocksAttacks)
		{
			matrixStack.translate(-0.00, -0.10, 0.14);
			matrixStack.scale(1.75f, 1.60f, 1.75f);
			//matrixStack.translate(-0.25, 0, 0.16);
			//matrixStack.scale(2, 2, 2);
		}
		else if (state.swordLike)
		{
			// check ModularBladedItem ? stupid tetra doesn't tag swords and doesn't return true for any canPerformAction call
			// currently using separate tag for tetra swords.
			matrixStack.translate(0, -0.2, 0);
			matrixStack.mulPose(ZPlus135);  // 1.19.4      Vector3f.ZP.rotationDegrees(135.0F)
		}
		else if (state.spearLike)
		{
			matrixStack.translate(0, -0.2, 0);
			matrixStack.mulPose(ZMinus45);
		}
		else if (state.crossbowLike)
		{
			matrixStack.translate(0, -0.2, 0);
			matrixStack.mulPose(ZPlus225);  // 1.19.4      Vector3f.ZP.rotationDegrees(225.0F)
		}
//			else if (state.gui3d)
//			{
//				matrixStack.mulPose(ZMinus45);  // 1.19.4      Vector3f.ZP.rotationDegrees(-45.0F)
//			}
		else if (state.bottle)
		{
			matrixStack.translate(0, 0.1, 0);
		}
		else
		{
			matrixStack.mulPose(ZMinus45);  // 1.19.4    Vector3f.ZP.rotationDegrees(-45.0F)
		}
	}
	protected static final Quaternionf ZMinus45 = new Quaternionf().fromAxisAngleDeg(0, 0, 1, -45);
	protected static final Quaternionf ZPlus225 = new Quaternionf().fromAxisAngleDeg(0, 0, 1, 225);
	protected static final Quaternionf ZPlus135 = new Quaternionf().fromAxisAngleDeg(0, 0, 1, 135);
	protected static final Quaternionf ZPlus180 = new Quaternionf().fromAxisAngleDeg(0, 0, 1, 180);
	protected static final Quaternionf XMinus90 = new Quaternionf().fromAxisAngleDeg(1, 0, 0, -90);
	protected static final Quaternionf YPlus180 = new Quaternionf().fromAxisAngleDeg(0, 1, 0, 180);

	public static ItemStack removeEnchantmentsStatic(ItemStack stored)
	{
		if (! stored.has(DataComponents.ENCHANTMENTS))
		{
			return stored;
		}
		ItemStack result = stored.copy();
		result.remove(DataComponents.ENCHANTMENTS);
		return result;
	}
	private ItemStack removeEnchantments(ItemStack stored)
	{
		if (! stored.has(DataComponents.ENCHANTMENTS))
		{
			return stored;
		}
		if (this.cacheForRemovingEnchantments.containsKey(stored.hashCode()))
		{
			return this.cacheForRemovingEnchantments.get(stored.hashCode());
		}
		ItemStack result = stored.copy();
		result.remove(DataComponents.ENCHANTMENTS);
		this.cacheForRemovingEnchantments.put(stored.hashCode(), result);
		return result;
	}
	private final HashMap<Integer, ItemStack> cacheForRemovingEnchantments = new HashMap<>();

	// run TESR even if main block isn't visible.
	@Override
	@NotNull
	public AABB getRenderBoundingBox(ToolRackBlockEntity blockEntity)
	{
		return blockEntity.getRenderBoundingBox();
	}

	//////////////////////////////

	@NotNull
	public static class RackRenderState extends BlockEntityRenderState
	{
		public ToolRackItemRenderState[] items = new ToolRackItemRenderState[18];
		public Direction direction = Direction.EAST;
		public int numberOfItemsInOneRow;
		public int numberOfItems;
	}

	public static class ToolRackItemRenderState extends ItemStackRenderState
	{
		public boolean larger150 = false;
		public boolean larger125 = false;
		public boolean rotate180 = false;
		public boolean noDefaultRotation = false;
		public boolean bottle = false;
		public boolean swordLike = false;
		public boolean spearLike = false;
		public boolean crossbowLike = false;
		public boolean blocksAttacks = false;
	}

	@Override
	public RackRenderState createRenderState()
	{
		return new RackRenderState();
	}

	@Override
	public void submit(RackRenderState rackRenderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState)
	{
		Direction itemDirection = rackRenderState.direction.getCounterClockWise();
		int itemsPerRow = rackRenderState.numberOfItemsInOneRow;
		if (itemsPerRow == 3)  // potions
		{
			poseStack.pushPose();
			poseStack.translate(0.5 - rackRenderState.direction.getStepX() * 0.42, 0.7, 0.5 - rackRenderState.direction.getStepZ() * 0.42);
			poseStack.scale(0.3f, 0.40f, 0.3f);
			int rowHeight = 15;
			for (int row = 0; row < 3; row++)
			{
				for (int i = 0; i < itemsPerRow; i++)
				{
					if (rackRenderState.numberOfItems <= row * itemsPerRow) { break; }
					ToolRackItemRenderState itemToRender = rackRenderState.items[row * itemsPerRow + i];
					if (itemToRender != null)
					{
						poseStack.pushPose();
						double antiZFighting = row * 0.003d + i * 0.001d;
						poseStack.translate(itemDirection.getStepX() * (i - 1d) + antiZFighting, 0 - row*(rowHeight/16f+3/16f)-2/16f, itemDirection.getStepZ() * (i - 1d) + antiZFighting);
						poseStack.mulPose(rackRenderState.direction.getRotation());
						doSpecialPoseStackAdjustments(itemToRender, poseStack);
						itemToRender.submit(poseStack, nodeCollector, rackRenderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
						poseStack.popPose();
					}
				}
			}
			poseStack.popPose();
		}
		else // items
		{
			poseStack.pushPose();
			poseStack.translate(0.5 - rackRenderState.direction.getStepX() * 0.42, 0.7, 0.5 - rackRenderState.direction.getStepZ() * 0.42);
			poseStack.scale(0.5f, 0.5f, 0.5f);
			int rowHeight = (rackRenderState.numberOfItems % 4 == 0) ? 15 : 20;
			for (int row = 0; row < 3; row++)
			{
				for (int i = 0; i < itemsPerRow; i++)
				{
					if (rackRenderState.numberOfItems <= row * itemsPerRow) { break; }
					ToolRackItemRenderState itemToRender = rackRenderState.items[row * itemsPerRow + i];
					if (itemToRender != null)
					{
						poseStack.pushPose();
						double antiZFighting = row * 0.003d + i * 0.001d;
						poseStack.translate(itemDirection.getStepX() * (i - 0.5) + antiZFighting, 0 - row*(rowHeight/16f), itemDirection.getStepZ() * (i - 0.5) + antiZFighting);
						poseStack.mulPose(rackRenderState.direction.getRotation());
						doSpecialPoseStackAdjustments(itemToRender, poseStack);
						itemToRender.submit(poseStack, nodeCollector, rackRenderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
						poseStack.popPose();
					}
				}
			}
			poseStack.popPose();
		}
	}

	@Override
	public void extractRenderState(ToolRackBlockEntity blockEntity, RackRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress)
	{
		BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
		// and now fill in our stuff
		renderState.direction = blockEntity.getBlockState().getValue(ToolRack.FACING).getOpposite();
		renderState.numberOfItemsInOneRow = blockEntity.getNumberOfItemsInOneRow();
		renderState.numberOfItems = blockEntity.getNumberOfItems();
		int seedBase = Long.valueOf(blockEntity.getBlockPos().asLong()).hashCode();
		for (int i = 0; i < blockEntity.getCapacity(); i++)
		{
			ItemStack itemStack = blockEntity.GetItem(i);
			if (! itemStack.isEmpty())
			{
				ToolRackItemRenderState itemState = new ToolRackItemRenderState();
				storeItemStackSpecifics(itemStack, itemState);
				itemModelResolver.updateForTopItem(itemState, this.removeEnchantments(itemStack), ItemDisplayContext.FIXED, blockEntity.getLevel(), null, seedBase + i);
				renderState.items[i] = itemState;
			}
			else
			{
				renderState.items[i] = null;
			}
		}
		// why did i implement ItemOwner?  i had a note to definitely do it and i did it but i don't know why.
	}
}
