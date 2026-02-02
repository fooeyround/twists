package twists.event

import net.casual.arcade.events.common.Event
import net.casual.arcade.events.common.MissingExecutorEvent
import net.casual.arcade.extensions.Extension
import net.casual.arcade.extensions.ExtensionHolder
import net.casual.arcade.extensions.ExtensionHolder.Companion.add
import net.casual.arcade.extensions.ExtensionHolder.Companion.get
import net.casual.arcade.extensions.event.ExtensionEvent
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.Entity

class MinecraftServerExtensionEvent ( val server: MinecraftServer): Event, ExtensionEvent, MissingExecutorEvent {
    override fun addExtension(extension: Extension) {
        server.addExtension(extension)
    }

    fun addExtension(provider: (MinecraftServer) -> Extension) {
        this.addExtension(provider.invoke(this.server))
    }

    companion object {
        fun MinecraftServer.addExtension(extension: Extension) {
            (this as ExtensionHolder).add(extension)
        }

        fun <T: Extension> MinecraftServer.getExtension(type: Class<T>): T {
            return (this as ExtensionHolder).get(type)
        }

        inline fun <reified T: Extension> MinecraftServer.getExtension(): T {
            return this.getExtension(T::class.java)
        }
    }
}