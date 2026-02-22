package twists.extension

import net.casual.arcade.extensions.SerializableExtension
import net.minecraft.world.ItemStackWithSlot
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import twists.util.twists

class SharedInventoryTeamExtension: SerializableExtension {
    var sharedInventory: Boolean = false
    val inventory: Inventory? = null

    override fun id() = twists("teamSharedInventorySettings")

    override fun serialize(output: ValueOutput) {
        output.putBoolean("sharedInventory", this.sharedInventory)
        this.inventory?.save(output.list("Inventory", ItemStackWithSlot.CODEC))

    }

    override fun deserialize(input: ValueInput) {
        this.sharedInventory =  input.getBooleanOr("sharedInventory", this.sharedInventory)
        this.inventory?.load(input.listOrEmpty("Inventory", ItemStackWithSlot.CODEC))


    }
}