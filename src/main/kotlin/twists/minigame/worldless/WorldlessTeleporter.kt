package twists.minigame.worldless

import com.google.common.collect.Multimap
import com.mojang.serialization.MapCodec
import net.casual.arcade.minigame.template.teleporter.EntityTeleporter
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Relative
import net.minecraft.world.scores.PlayerTeam
import twists.extension.PlayerFallWithoutDamageExtension.Companion.takeNoDamageOnNextFall

object WorldlessTeleporter: EntityTeleporter {
    override fun teleportEntities(
        level: ServerLevel,
        entities: List<Entity>
    ) {
        for (entity in entities) {
            entity.teleportTo(level, 0.0, 200.0, 0.0, Relative.ALL, 0F, 0F, false)
            if (entity is ServerPlayer) {
                entity.takeNoDamageOnNextFall()
            }
        }
    }

    override fun teleportTeams(
        level: ServerLevel,
        teams: Multimap<PlayerTeam, Entity>
    ) {
        this.teleportEntities(level, teams.values().toList())
    }

    override fun codec(): MapCodec<out EntityTeleporter> {
        return MapCodec.unit(this)
    }


}