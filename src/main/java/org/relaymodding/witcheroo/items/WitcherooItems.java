package org.relaymodding.witcheroo.items;

import net.minecraft.world.item.Item;
import org.relaymodding.witcheroo.registries.WitcherooRegistries;

public class WitcherooItems {
	public static Item WITCH_STAFF;

	public static void loadItems() {
		WITCH_STAFF = WitcherooRegistries.WITCH_STAFF_OBJECT.get();
	}
}
