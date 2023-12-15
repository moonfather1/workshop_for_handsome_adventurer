package moonfather.workshop_for_handsome_adventurer.other;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import moonfather.workshop_for_handsome_adventurer.OptionsHolder;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class OptionalRecipeCondition implements ICondition
{
	private final String flagCode;

	private OptionalRecipeCondition(String value)
	{
		this.flagCode = value;
	}



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


	/////////////////////////////////////////////////////

	@Override
	public Codec<? extends ICondition> codec()
	{
		return CODEC;
	}

	public static Codec<OptionalRecipeCondition> CODEC = RecordCodecBuilder.create(
			builder -> builder
					.group(
							Codec.STRING.fieldOf("flag_code").forGetter(orc -> orc.flagCode))
					.apply(builder, OptionalRecipeCondition::new));
}
