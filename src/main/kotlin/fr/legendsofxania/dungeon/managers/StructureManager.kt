package fr.legendsofxania.dungeon.managers

import com.typewritermc.core.entries.Ref
import com.typewritermc.core.interaction.InteractionContext
import com.typewritermc.engine.paper.utils.Sync
import fr.legendsofxania.dungeon.entries.manifest.definition.RoomDefinitionEntry
import fr.legendsofxania.dungeon.interactions.dungeon.instances.DungeonInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.structure.Mirror
import org.bukkit.block.structure.StructureRotation
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import java.util.Random

object StructureManager {

    suspend fun placeRooms(
        player: Player,
        context: InteractionContext,
        instance: DungeonInstance,
        ref: Ref<RoomDefinitionEntry>,
        location: Location
    ) {
        val entry = ref.entry ?: error("Room entry not found for $ref")

        placeRoom(player, context, entry, location)

        entry.children.forEach { child ->
            placeRooms(player, context, instance, child, location)
        }
    }

    private suspend fun placeRoom(
        player: Player,
        context: InteractionContext,
        entry: RoomDefinitionEntry,
        baseLocation: Location
    ) {
        val templateId = entry.template.get(player, context).id
        val structure = TemplateManager.loadTemplate(templateId)
            ?: error("Structure not found for RoomTemplate: $templateId")

        val direction = entry.direction.get(player, context)
        val roomLocation = baseLocation.clone().add(direction.getOffset(structure.size))

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
    }

    suspend fun deleteRooms(instance: DungeonInstance) {
        if (instance.rooms.isEmpty()) return

        val world = instance.location.world

        withContext(Dispatchers.Sync) {
            instance.rooms.forEach { room ->
                deleteRoom(room.boundingBox, world)
            }
        }
    }

    private fun deleteRoom(box: BoundingBox, world: World) {
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