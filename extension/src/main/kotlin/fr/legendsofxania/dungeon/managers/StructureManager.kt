package fr.legendsofxania.dungeon.managers

import com.typewritermc.core.entries.Ref
import com.typewritermc.core.entries.ref
import com.typewritermc.engine.paper.interaction.interactionContext
import com.typewritermc.engine.paper.utils.Sync
import fr.legendsofxania.dungeon.entries.manifest.definition.RoomDefinitionEntry
import fr.legendsofxania.dungeon.interactions.dungeon.instances.DungeonInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.structure.Mirror
import org.bukkit.block.structure.StructureRotation
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import java.util.Random

class StructureManager(
    private val instanceManager: InstanceManager
) {
    suspend fun placeRooms(
        player: Player,
        instance: DungeonInstance
    ) {
        val dungeonEntry = instance.definition.entry
            ?: error("DungeonDefinitionEntry not found for ${instance.definition}")

        val rootEntry = dungeonEntry.child.entry
            ?: error("RoomInstanceEntry not found for ${dungeonEntry.child}")

        val rootLocation = placeRoom(player, instance, rootEntry, instance.location)

        rootEntry.children.forEach { child ->
            placeRoomsRecursive(player, instance, child, rootLocation)
        }
    }

    private suspend fun placeRoomsRecursive(
        player: Player,
        instance: DungeonInstance,
        ref: Ref<RoomDefinitionEntry>,
        location: Location
    ) {
        val entry = ref.entry
            ?: error("RoomDefinitionEntry not found for ref: $ref")

        val roomLocation = placeRoom(player, instance, entry, location)

        entry.children.forEach { child ->
            placeRoomsRecursive(player, instance, child, roomLocation)
        }
    }

    private suspend fun placeRoom(
        player: Player,
        dungeonInstance: DungeonInstance,
        entry: RoomDefinitionEntry,
        location: Location
    ): Location {
        val template = entry.template.get(player, player.interactionContext).entry
            ?: error("RoomTemplateEntry not found for RoomDefinitionEntry: $entry")
        val structure = TemplateManager.loadTemplate(template)
            ?: error("Structure not found for RoomTemplate: $template")

        val direction = entry.direction.get(player, player.interactionContext)
        val roomLocation = location.clone().add(direction.getOffset(structure.size))

        withContext(Dispatchers.Sync) {
            structure.place(
                roomLocation,
                true,
                StructureRotation.NONE,
                Mirror.NONE,
                0,
                1f,
                Random()
            )
        }

        val size = structure.size
        val boundingBox = BoundingBox(
            roomLocation.x,
            roomLocation.y,
            roomLocation.z,
            roomLocation.x + size.x,
            roomLocation.y + size.y,
            roomLocation.z + size.z
        )

        instanceManager.startRoomInstance(
            dungeonInstance,
            entry.ref(),
            boundingBox
        )

        return roomLocation
    }

    suspend fun deleteRooms(instance: DungeonInstance) {
        if (instance.rooms.isEmpty()) return

        withContext(Dispatchers.Sync) {
            instance.rooms.forEach { room ->
                deleteRoom(room.value.boundingBox)
            }
        }
    }

    private fun deleteRoom(box: BoundingBox) {
        val world = WorldManager.getWorld()
            ?: error("Dungeon world not found")

        val minX = box.minX.toInt()
        val maxX = box.maxX.toInt()
        val minY = box.minY.toInt().coerceIn(world.minHeight, world.maxHeight)
        val maxY = box.maxY.toInt().coerceIn(world.minHeight, world.maxHeight)
        val minZ = box.minZ.toInt()
        val maxZ = box.maxZ.toInt()


        for (x in minX..maxX) {
            for (z in minZ..maxZ) {
                val chunkX = x shr 4
                val chunkZ = z shr 4

                if (!world.isChunkLoaded(chunkX, chunkZ)) {
                    world.loadChunk(chunkX, chunkZ)
                }

                for (y in minY..maxY) {
                    world.getBlockAt(x, y, z).type = Material.AIR
                }
            }
        }
    }
}