package fr.legendsofxania.dungeon.managers

import com.typewritermc.core.entries.Ref
import com.typewritermc.core.extension.annotations.Singleton
import fr.legendsofxania.dungeon.entries.manifest.definition.DungeonDefinitionEntry
import fr.legendsofxania.dungeon.entries.manifest.definition.RoomDefinitionEntry
import fr.legendsofxania.dungeon.interactions.dungeon.instances.DungeonInstance
import fr.legendsofxania.dungeon.interactions.dungeon.instances.RoomInstance
import org.bukkit.Location
import org.bukkit.util.BoundingBox
import java.util.UUID

@Singleton
class InstanceManager {
    private val instances = mutableMapOf<UUID, DungeonInstance>()

    fun startDungeonInstance(
        definition: Ref<DungeonDefinitionEntry>,
        location: Location
    ): DungeonInstance {
        val dungeonInstance = DungeonInstance(
            UUID.randomUUID(),
            definition,
            location,
            mutableMapOf()
        )

        instances[dungeonInstance.id] = dungeonInstance

        return dungeonInstance
    }

    fun getDungeonInstance(id: UUID): DungeonInstance? {
        return instances[id]
    }

    fun stopDungeonInstance(dungeonInstance: DungeonInstance) {
        instances.remove(dungeonInstance.id)
    }

    fun startRoomInstance(
        dungeonInstance: DungeonInstance,
        definition: Ref<RoomDefinitionEntry>,
        boundingBox: BoundingBox
    ): RoomInstance {
        val roomInstance = RoomInstance(
            UUID.randomUUID(),
            definition,
            boundingBox
        )

        dungeonInstance.rooms[roomInstance.id] = roomInstance

        return roomInstance
    }

    fun getRoomInstance(
        dungeonInstance: DungeonInstance,
        roomId: UUID
    ): RoomInstance? {
        return dungeonInstance.rooms[roomId]
    }

    fun getRoomInstance(
        dungeonInstance: DungeonInstance,
        definition: Ref<RoomDefinitionEntry>
    ): RoomInstance? {
        return dungeonInstance.rooms.values.find { it.definition == definition }
    }
}