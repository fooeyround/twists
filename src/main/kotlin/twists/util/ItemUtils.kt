package twists.util

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

object ItemUtils {

    fun registerItem(name: String, item: (Item.Properties) -> Item): Item {
        val itemKey = ResourceKey.create(Registries.ITEM, twists(name))
        val item = item(Item.Properties().setId(itemKey))
        return Registry.register(BuiltInRegistries.ITEM, itemKey, item)
    }


    fun dropAllExceptSoulboundInInventory(inventory: Inventory) {
            for (i in inventory.nonEquipmentItems.indices) {
                val stack = inventory.nonEquipmentItems[i];
                if (!stack.isEmpty) {
                    inventory.player.drop(stack, true, false)
                    inventory.nonEquipmentItems[i] = ItemStack.EMPTY
                }
            }

    }

    fun randomItem(random: RandomSource): Item? {
        return try {
            BuiltInRegistries.ITEM.getRandom(random).get().value().asItem()
        } catch (_: Exception) {
            null
        }
    }

}