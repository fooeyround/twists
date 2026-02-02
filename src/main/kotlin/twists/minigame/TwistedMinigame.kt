package twists.minigame

import net.casual.arcade.minigame.Minigame
import net.minecraft.server.MinecraftServer
import java.util.UUID

abstract class TwistedMinigame(
    server: MinecraftServer,
    uuid: UUID,
): Minigame(server, uuid) {
    override val settings = TwistSettings(this)

}