package twists.generator

import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.world.level.StructureManager
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.BiomeManager
import net.minecraft.world.level.biome.BiomeSource
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings
import net.minecraft.world.level.levelgen.RandomState
import net.minecraft.world.level.levelgen.WorldGenerationContext
import net.minecraft.world.level.levelgen.blending.Blender


class OneBlockChunkGenerator(
    biomeSource: BiomeSource,
    settings: Holder<NoiseGeneratorSettings>
) : NoiseBasedChunkGenerator(biomeSource, settings) {


//    override fun buildSurface(
//        level: WorldGenRegion?,
//        structureManager: StructureManager?,
//        random: RandomState?,
//        chunk: ChunkAccess?
//    ) {
//        super.buildSurface(level, structureManager, random, chunk)
//    }

    override fun buildSurface(
        chunk: ChunkAccess,
        context: WorldGenerationContext,
        random: RandomState,
        structureManager: StructureManager,
        biomeManager: BiomeManager,
        biomes: Registry<Biome>,
        blender: Blender
    ) {
        val noiseChunk = chunk.getOrCreateNoiseChunk({ chunkAccess ->
            super.createNoiseChunk(
                chunkAccess,
                structureManager,
                blender,
                random
            )
        })
        val noiseGeneratorSettings = this.settings.value()
        random.surfaceSystem()
            .buildSurface(
                random,
                biomeManager,
                biomes,
                noiseGeneratorSettings.useLegacyRandomSource(),
                context,
                chunk,
                noiseChunk,
                noiseGeneratorSettings.surfaceRule()
            )

    }

}