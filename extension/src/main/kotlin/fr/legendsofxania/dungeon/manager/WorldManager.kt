package fr.legendsofxania.dungeon.manager

import com.typewritermc.engine.paper.plugin
import com.typewritermc.engine.paper.utils.Sync
import com.typewritermc.engine.paper.utils.config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.bukkit.*
import java.util.*

private val worldName: String by config(
    "dungeon.worldName",
    "dungeons",
    "The name of the world used to host the DungeonInstances."
)

private const val INSTANCE_SPACING = 500.0

object WorldManager {
    private val key = NamespacedKey(plugin, worldName)

    private val freeIndexes = PriorityQueue<Int>()
    private val activeIndexes = mutableMapOf<Location, Int>()

    private var nextIndex = 0

    fun startDungeon(): Location {
        val world = getWorld() ?: runBlocking { withContext(Dispatchers.Sync) { createWorld() } }
        ?: throw IllegalStateException("Failed to create or retrieve the dungeon world.")

        val index = if (freeIndexes.isNotEmpty()) {
            freeIndexes.poll()
        } else {
            nextIndex++
        }

        val location = Location(world, 0.0, 0.0, index * INSTANCE_SPACING)

        activeIndexes[location] = index

        return location
    }

    fun stopDungeon(location: Location) {
        val index = activeIndexes.remove(location) ?: return

        freeIndexes.offer(index)
    }

    fun getWorld(): World? {
        return plugin.server.getWorld(key)
    }

    private fun createWorld(): World? {
        return WorldCreator
            .ofKey(key)
            .type(WorldType.FLAT)
            .generatorSettings("{\"layers\":[],\"biome\":\"minecraft:plains\",\"structures\":{}}")
            .generateStructures(false)
            .createWorld()
    }
}