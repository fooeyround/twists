package twists.extension

import net.casual.arcade.events.GlobalEventHandler
import net.casual.arcade.events.ListenerRegistry.Companion.register
import net.casual.arcade.extensions.SerializableExtension
import net.casual.arcade.extensions.event.TeamExtensionEvent
import net.casual.arcade.extensions.utils.getExtension
import net.minecraft.core.NonNullList
import net.minecraft.world.ItemStackWithSlot
import net.minecraft.world.entity.EntityEquipment
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.PlayerEquipment
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueInput.TypedInputList
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.level.storage.ValueOutput.TypedOutputList
import net.minecraft.world.scores.PlayerTeam
import twists.util.twists


class SharedInventoryTeamExtension(team: PlayerTeam): SerializableExtension {
    var shareLevel: ShareLevel = ShareLevel.None
    val inventory: SharedInventory = SharedInventory()

    override fun id() = twists("shared_inventory_team_extension")

    override fun serialize(output: ValueOutput) {
        output.putString("shareLevel", this.shareLevel.toString())
        output.store("equipment", PlayerEquipment.CODEC, this.inventory.equipment)
        this.inventory.save(output.list("Inventory", ItemStackWithSlot.CODEC))

    }

    override fun deserialize(input: ValueInput) {
        try {
            this.shareLevel = ShareLevel.valueOf(input.getStringOr("enabled", "None"))
        } catch (_: IllegalArgumentException) {
            this.shareLevel = ShareLevel.None
        }
        this.inventory.load(input.listOrEmpty("Inventory", ItemStackWithSlot.CODEC))
        this.inventory.equipment = input.read("equipment", EntityEquipment.CODEC).orElseGet(::EntityEquipment)
    }

    companion object {
        internal fun registerEvents() {
            GlobalEventHandler.Server.register<TeamExtensionEvent> { event ->
                event.addExtension(SharedInventoryTeamExtension(event.team))
            }
        }

        val PlayerTeam.sharedInventoryExtension: SharedInventoryTeamExtension
            get() = this.getExtension()
    }

    enum class ShareLevel {
        None,
        Inventory,
        InventoryAndEquipment,
    }

    class SharedInventory(
        val items: NonNullList<ItemStack> = NonNullList.withSize(36, ItemStack.EMPTY),
        var equipment: EntityEquipment = EntityEquipment(),
    ) {
        fun save(output: TypedOutputList<ItemStackWithSlot>) {
            for (i in 0..<this.items.size) {
                val itemStack: ItemStack = this.items[i]
                if (!itemStack.isEmpty) {
                    output.add(ItemStackWithSlot(i, itemStack))
                }
            }
        }

        fun load(input: TypedInputList<ItemStackWithSlot>) {
            this.items.clear()

            for (itemStackWithSlot in input) {
                if (itemStackWithSlot.isValidInContainer(this.items.size)) {
                    this.setItem(itemStackWithSlot.slot(), itemStackWithSlot.stack())
                }
            }
        }

        fun setItem(slot: Int, stack: ItemStack) {
            if (slot < this.items.size) {
                this.items[slot] = stack
            }

            val equipmentSlot: EquipmentSlot? = Inventory.EQUIPMENT_SLOT_MAPPING.get(slot)
            if (equipmentSlot != null) {
                this.equipment.set(equipmentSlot, stack)
            }
        }
    }





}