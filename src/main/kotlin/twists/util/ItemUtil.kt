package twists.util

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item

object ItemUtil {

    fun registerItem(name: String, item: (Item.Properties) -> Item): Item {
        val itemKey = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("twists", name))
        val item = item(Item.Properties().setId(itemKey))
        return Registry.register(BuiltInRegistries.ITEM, itemKey, item)
    }

}