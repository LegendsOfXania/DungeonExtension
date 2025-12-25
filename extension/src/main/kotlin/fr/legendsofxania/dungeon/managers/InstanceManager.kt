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
    val instances = mutableSetOf<DungeonInstance>()

    fun startDungeonInstance(
        definition: Ref<DungeonDefinitionEntry>,
        location: Location
    ): DungeonInstance {
        val dungeonInstance = DungeonInstance(
            UUID.randomUUID(),
            definition,
            location,
            mutableListOf()
        )

        instances.add(dungeonInstance)

        return dungeonInstance
    }

    fun stopDungeonInstance(dungeonInstance: DungeonInstance) {
        instances.remove(dungeonInstance)
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

        dungeonInstance.rooms.add(roomInstance)

        return roomInstance
    }
}