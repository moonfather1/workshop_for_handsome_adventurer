package moonfather.workshop_for_handsome_adventurer.block_entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class ToolRackTESR implements BlockEntityRenderer<ToolRackBlockEntity>
{
	private ItemRenderer itemRenderer = null;
	private final BlockEntityRendererProvider.Context context;

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

		matrixStack.pushPose();
		matrixStack.translate(0.5 - direction.getStepX() * 0.42, 0.7, 0.5 - direction.getStepZ() * 0.42);
		matrixStack.scale(0.5f, 0.5f, 0.5f);
		for (int row = 0; row < 3; row++)
		{
			for (int i = 0; i < 2; i++)
			{
				ItemStack itemStack = this.RemoveEnchantments(tile.GetItem(row * 2 + i));
				if (!itemStack.isEmpty())
				{
					matrixStack.pushPose();
					double antiZFighting = row * 0.003d + i * 0.001d;
					matrixStack.translate(itemDirection.getStepX() * (i - 0.5) + antiZFighting, 0 - row*(20/16f), itemDirection.getStepZ() * (i - 0.5) + antiZFighting);
					matrixStack.mulPose(direction.getRotation());
					renderItemStack(tile, itemStack, matrixStack, buffer, combinedLight, combinedOverlay);
					matrixStack.popPose();
				}
			}
		}
		matrixStack.popPose();
	}



	private void renderItemStack(ToolRackBlockEntity tile, ItemStack itemStack, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		if (itemStack != null && !itemStack.isEmpty())
		{
			int renderId = (int) tile.getBlockPos().asLong();

			if (this.itemRenderer == null)
			{
				this.itemRenderer = Minecraft.getInstance().getItemRenderer();
			}
			BakedModel model = this.itemRenderer.getModel(itemStack, tile.getLevel(), null, combinedLight);

			matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));

			if (/*itemStack.getItem() instanceof ModularShieldItem*/false)
			{
				matrixStack.translate(-0.25, 0, 0.16);
				matrixStack.scale(2, 2, 2);
			}
			else if (/*itemStack.getItem() instanceof ModularBladedItem ||*/ itemStack.getItem() instanceof SwordItem)   //todo:tetra
			{
				matrixStack.translate(0, -0.2, 0);
				matrixStack.mulPose(Vector3f.ZP.rotationDegrees(135.0F));
			}
			else if (/*itemStack.getItem() instanceof ModularCrossbowItem ||*/ itemStack.getItem() instanceof CrossbowItem)
			{
				matrixStack.translate(0, -0.2, 0);
				matrixStack.mulPose(Vector3f.ZP.rotationDegrees(225.0F));
			}
			else if (model.isGui3d())
			{
				matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-45.0F));
			}
			else
			{
				matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-45.0F));
			}

			Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, matrixStack, buffer, renderId);
		}
	}



	private ItemStack RemoveEnchantments(ItemStack stored)
	{
		ItemStack result = stored.copy();
		if (!result.hasTag())
		{
			return result;
		}
		if (!result.getTag().contains("Enchantments"))
		{
			return result;
		}
		result.getTag().remove("Enchantments");
		return result;
	}
}