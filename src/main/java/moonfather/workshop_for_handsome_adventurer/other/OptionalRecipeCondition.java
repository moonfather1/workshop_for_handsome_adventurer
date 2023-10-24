package moonfather.workshop_for_handsome_adventurer.other;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import moonfather.workshop_for_handsome_adventurer.OptionsHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;

public record OptionalRecipeCondition(String flagCode) implements ICondition
{
	//private final String flagCode;

	//private OptionalRecipeCondition(String value)
	//{
	//	this.flagCode = value;
	//}



	@Override
	public boolean test(IContext context)
	{
		if (this.flagCode.equals("replace_vanilla_crafting_table"))
		{
			return OptionsHolder.COMMON.SimpleTableReplacesVanillaTable.get();
		}
		else if (this.flagCode.equals("dont_replace_vanilla_crafting_table"))
		{
			return ! OptionsHolder.COMMON.SimpleTableReplacesVanillaTable.get();
		}
		else
		{
			return false;
		}
	}

	@Override
	public Codec<? extends ICondition> codec()
	{
		return STUPID_CODEC_2;
	}

	public static final Codec<OptionalRecipeCondition> STUPID_CODEC_2 = RecordCodecBuilder.create(b -> b.group(
			Codec.STRING.fieldOf("flag_code").forGetter(OptionalRecipeCondition::flagCode)
	).apply(b, OptionalRecipeCondition::new));


	public static Codec<OptionalRecipeCondition> STUPID_CODEC = new Codec<OptionalRecipeCondition>()
	{
		@Override
		public <T> DataResult<Pair<OptionalRecipeCondition, T>> decode(DynamicOps<T> ops, T input)
		{
			String flag = ops.getStringValue(input).result().orElse("(empty)");
			OptionalRecipeCondition res = new OptionalRecipeCondition(flag);
			return DataResult.success(new Pair<>(res, input));
		}

		@Override
		public <T> DataResult<T> encode(OptionalRecipeCondition input, DynamicOps<T> ops, T prefix)
		{
			return DataResult.success(ops.createString(input.flagCode));
		}
	};


	/////////////////////////////////////////////////////

//	public static class Serializer implements IConditionSerializer<OptionalRecipeCondition>
//	{
//		private final ResourceLocation conditionId;
//
//		public Serializer(ResourceLocation id)
//		{
//			this.conditionId = id;
//		}
//
//		@Override
//		public void write(JsonObject json, OptionalRecipeCondition condition)
//		{
//			json.addProperty("flag_code", condition.flagCode);
//		}
//
//		@Override
//		public OptionalRecipeCondition read(JsonObject json)
//		{
//			return new OptionalRecipeCondition(this.conditionId, json.getAsJsonPrimitive("flag_code").getAsString());
//		}
//
//		@Override
//		public ResourceLocation getID()
//		{
//			return this.conditionId;
//		}
//	}
}
