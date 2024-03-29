package moonfather.workshop_for_handsome_adventurer;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

public class OptionsHolder
{
	public static class Common
	{
		private static final boolean defaultSimpleTableReplacesVanillaTable = false;
		private static final int defaultSimpleTableNumberOfSlots = 1;
		private static final int defaultDualTableNumberOfSlots = 2;
		private static final String defaultAccessCustomizationItem = "minecraft:name_tag";

		private static final int defaultSlotRoomMultiplier = 6;
		private static final int defaultSlotRoomMaximum = 16;

		private static final boolean defaultOffhandInteractsWithToolRack = true;

		public final ConfigValue<Boolean> SimpleTableReplacesVanillaTable;
		public final ConfigValue<Integer> SimpleTableNumberOfSlots;
		public final ConfigValue<Integer> DualTableNumberOfSlots;
		public final ConfigValue<String> AccessCustomizationItem;

		public final ConfigValue<Integer> SlotRoomMultiplier;
		public final ConfigValue<Integer> SlotRoomMaximum;

		public final ConfigValue<Boolean> OffhandInteractsWithToolRack;

		public Common(ForgeConfigSpec.Builder builder)
		{
			builder.push("Tables");
			this.SimpleTableNumberOfSlots = builder.comment("Customization slots allow you to enable the table to hold items (author doesn't like that so it's optional) or access nearby chests.").worldRestart()
					.defineInRange("Simple table - number of customization slots", defaultSimpleTableNumberOfSlots, 0, 2);
			this.DualTableNumberOfSlots = builder.comment("Customization slots allow you to enable the table to hold items (author doesn't like that so it's optional) or access nearby chests, or have lanterns as part of the table.").worldRestart()
					.defineInRange("Dual table - number of customization slots", defaultDualTableNumberOfSlots, 0, 4);
			this.AccessCustomizationItem = builder.comment("Here you can set which item allows you to access nearby inventories from a workbench window. Frankly there is no correct item here - customizations aren't upgrades, they are options - like checkboxes. This may delay availability of this functionality - you can put a minecraft:amethyst_shard if you want it to be available later in the game or a good old minecraft:torch if you want it available right away.").worldRestart()
					.define("Customization - item for inventory access", defaultAccessCustomizationItem);
			this.SimpleTableReplacesVanillaTable = builder.comment("If set to false (default), simple crafting tables are craftable after you have vanilla crafting table. If set to true (not much reason not to be), this mod's crafting tables are craftable from four planks in 2x2 configuration.").worldRestart()
					.define("Simple table replaces vanilla table", defaultSimpleTableReplacesVanillaTable);
			builder.pop();
			builder.push("PotionShelf");
			this.SlotRoomMultiplier = builder.comment("This is a multiplier for the number of bottles that fit in a single potion shelf slot, multiplying normal stack maximum. Default 6 (unrelated to six slots) means each slot can fit 6 non-stackable potions or for example 24 potions that stack up to 4 in players inventory.")
											 .defineInRange("Room in one potion shelf slot (multiplier)", defaultSlotRoomMultiplier, 1, 12);
			this.SlotRoomMaximum = builder.comment("This is a total maximum of number of bottles that fit in a single potion shelf slot. Whatever above math gives you will be clipped to fit this value.")
										  .defineInRange("Maximum number of potions that fit into one shelf slot", defaultSlotRoomMaximum, 1, 64);
			builder.pop();
			builder.push("ToolRack");
			this.OffhandInteractsWithToolRack = builder.comment("If set to false, you need to move a tool from off-hand to main hand (F) before putting it onto a toolrack, it's simpler but needs extra actions. If you set this to true you can put tools from off-hand to toolrack directly and you can take items directly; quicker but there might be possible unintended interactions with the toolrack.")
					.define("Offhand interacts with tool rack directly", defaultOffhandInteractsWithToolRack);
			builder.pop();
		}
	}

	///////////////////////////////////////////////////

	public static class Client
	{
		private static final boolean defaultRenderItemsOnTable = true;
		private static final boolean defaultDetailedWailaInfoForEnchantedTools = false;
		private static final boolean defaultDetailedWailaInfoForEnchantedBooks = true;

		public final ConfigValue<Boolean> RenderItemsOnTable;
		public final ConfigValue<Boolean> DetailedWailaInfoForEnchantedTools;
		public final ConfigValue<Boolean> DetailedWailaInfoForEnchantedBooks;

		public Client(ForgeConfigSpec.Builder builder)
		{
			builder.push("Tables");
			this.RenderItemsOnTable = builder.comment("Crafting tables can permanently hold items if you put a chest into a customization slot. Here you set whether the items are rendered.")
											 .define("Render items on top of tables", defaultRenderItemsOnTable);
			builder.pop();
			builder.push("Bookshelves");
			this.DetailedWailaInfoForEnchantedBooks = builder.comment("If this option is turned you'll see a list of enchantments for your books.")
															 .define("Detailed info for enchanted books in Jade/TOP/WTHIT", defaultDetailedWailaInfoForEnchantedBooks);
			builder.pop();
			builder.push("Toolracks");
			this.DetailedWailaInfoForEnchantedTools = builder.comment("If this option is turned you'll see a list of enchantments for your tools.")
															 .define("Detailed info for enchanted tools in Jade/TOP/WTHIT", defaultDetailedWailaInfoForEnchantedTools);
			builder.pop();
		}
	}

	///////////////////////////////////////////////////

	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;

	public static final Client CLIENT;
	public static final ForgeConfigSpec CLIENT_SPEC;

	static //constructor
	{
		Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON = commonSpecPair.getLeft();
		COMMON_SPEC = commonSpecPair.getRight();

		Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
		CLIENT = clientSpecPair.getLeft();
		CLIENT_SPEC = clientSpecPair.getRight();
	}
}
