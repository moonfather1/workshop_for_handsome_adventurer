package moonfather.workshop_for_handsome_adventurer.block_entities.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import moonfather.workshop_for_handsome_adventurer.Constants;
import moonfather.workshop_for_handsome_adventurer.block_entities.ToolRackBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolActions;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class ToolRackTESR implements BlockEntityRenderer<ToolRackBlockEntity>
{
	private ItemRenderer itemRenderer = null;
	private final BlockEntityRendererProvider.Context context;
	private final TagKey<Item> itemsThatWeShouldntRotate = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Constants.MODID, "dont_rotate_on_toolrack"));

	public ToolRackTESR(BlockEntityRendererProvider.Context context)
	{
		this.context = context;
		this.itemRenderer = Minecraft.getInstance().getItemRenderer();
	}


	@Override
	public void render(ToolRackBlockEntity tile, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
	{
		Direction direction = tile.getBlockState().getValue(HorizontalDirectionalBlock.FACING).getOpposite();
		Direction itemDirection = direction.getCounterClockWise();
		Quaternion yBlockDirection = YRot[((int) direction.toYRot()) / 90]; // so that we wouldn't create new objects here

		int itemsPerRow = tile.getNumberOfItemsInOneRow();
		if (itemsPerRow == 3)  // potions
		{
			matrixStack.pushPose();
			matrixStack.translate(0.5 - direction.getStepX() * 0.42, 0.7, 0.5 - direction.getStepZ() * 0.42);
			matrixStack.scale(0.3f, 0.40f, 0.3f);
			int rowHeight = 15;
			for (int row = 0; row < 3; row++)
			{
				for (int i = 0; i < itemsPerRow; i++)
				{
					if (tile.getCapacity() <= row * itemsPerRow) { break; }
					ItemStack itemStack = ToolRackTESR.RemoveEnchantments(tile.GetItem(row * itemsPerRow + i));
					if (!itemStack.isEmpty())
					{
						matrixStack.pushPose();
						double antiZFighting = row * 0.003d + i * 0.001d;
						matrixStack.translate(itemDirection.getStepX() * (i - 1d) + antiZFighting, 0 - row*(rowHeight/16f+3/16f)-2/16f, itemDirection.getStepZ() * (i - 1d) + antiZFighting);
						matrixStack.mulPose(yBlockDirection);
						renderItemStack(tile, itemStack, matrixStack, buffer, combinedLight, combinedOverlay);
						matrixStack.popPose();
					}
				}
			}
			matrixStack.popPose();
		}
		else // items
		{
			matrixStack.pushPose();
			matrixStack.translate(0.5 - direction.getStepX() * 0.42, 0.7, 0.5 - direction.getStepZ() * 0.42);
			matrixStack.scale(0.5f, 0.5f, 0.5f);
			int rowHeight = (tile.getCapacity() % 4 == 0) ? 15 : 20;
			for (int row = 0; row < 3; row++)
			{
				for (int i = 0; i < itemsPerRow; i++)
				{
					if (tile.getCapacity() <= row * itemsPerRow) { break; }
					ItemStack itemStack = ToolRackTESR.RemoveEnchantments(tile.GetItem(row * itemsPerRow + i));
					if (!itemStack.isEmpty())
					{
						matrixStack.pushPose();
						double antiZFighting = row * 0.003d + i * 0.001d;
						matrixStack.translate(itemDirection.getStepX() * (i - 0.5) + antiZFighting, 0 - row*(rowHeight/16f), itemDirection.getStepZ() * (i - 0.5) + antiZFighting);
						matrixStack.mulPose(yBlockDirection);
						renderItemStack(tile, itemStack, matrixStack, buffer, combinedLight, combinedOverlay);
						matrixStack.popPose();
					}
				}
			}
			matrixStack.popPose();
		}
	}
	private static final Quaternion[] YRot = new Quaternion[]
			{
					Direction.SOUTH.getRotation(), // 0
					Direction.WEST.getRotation(), // 90
					Direction.NORTH.getRotation(), // 180
					Direction.EAST.getRotation() // 270
			};



	private void renderItemStack(ToolRackBlockEntity tile, ItemStack itemStack, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		if (itemStack != null && ! itemStack.isEmpty())
		{
			int renderId = (int) tile.getBlockPos().asLong();

			if (this.itemRenderer == null)
			{
				this.itemRenderer = Minecraft.getInstance().getItemRenderer();
			}
			BakedModel model = this.itemRenderer.getModel(itemStack, tile.getLevel(), null, combinedLight);

			matrixStack.mulPose(XMinus90);
			matrixStack.mulPose(YPlus180);

			if (itemStack.getItem().canPerformAction(itemStack, ToolActions.SHIELD_BLOCK))
			{
				matrixStack.translate(-0.25, 0, 0.16);
				matrixStack.scale(2, 2, 2);
			}
			else if (itemStack.getItem().canPerformAction(itemStack, ToolActions.SWORD_SWEEP) || itemStack.getItem() instanceof SwordItem) //ModularBladedItem
			{
				matrixStack.translate(0, -0.2, 0);
				matrixStack.mulPose(ZPlus135);
			}
			else if (itemStack.getItem().getClass().getSimpleName().contains("rossbow") || itemStack.getItem() instanceof CrossbowItem)  //ModularCrossbowItem
			{
				matrixStack.translate(0, -0.2, 0);
				matrixStack.mulPose(ZPlus225);
			}
			else if (model.isGui3d())
			{
				matrixStack.mulPose(ZMinus45);
			}
			else if ((itemStack.getTag() != null && itemStack.getTag().contains("CustomPotionColor")) || itemStack.is(Items.GLASS_BOTTLE))
			{
				matrixStack.translate(0, 0.1, 0);
			}
			else if (itemStack.is(itemsThatWeShouldntRotate))
			{
				matrixStack.translate(0, 0.1, 0);
			}
			else
			{
				matrixStack.mulPose(ZMinus45);
			}

			Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, matrixStack, buffer, renderId);
		}
	}
	private static final Quaternion ZMinus45 = Vector3f.ZP.rotationDegrees(-45.0F);
	private static final Quaternion ZPlus225 = Vector3f.ZP.rotationDegrees(225.0F);
	private static final Quaternion ZPlus135 = Vector3f.ZP.rotationDegrees(135.0F);
	private static final Quaternion XMinus90 = Vector3f.XP.rotationDegrees(-90.0F);
	private static final Quaternion YPlus180 = Vector3f.YP.rotationDegrees(180.0F);


	protected static ItemStack RemoveEnchantments(ItemStack stored)
	{
		ItemStack result = stored.copy();
		if (! result.hasTag())
		{
			return result;
		}
		if (result.getTag().contains("Enchantments"))
		{
			result.getTag().remove("Enchantments");
		}
		if (result.getTag().contains("Potion"))
		{
			result.getTag().putInt("CustomPotionColor", PotionUtils.getColor(PotionUtils.getPotion(result.getTag())));
			result.getTag().remove("Potion");
		}
		return result;
	}
}
