package fr.legendsofxania.dungeon.manager

import com.typewritermc.core.entries.Ref
import com.typewritermc.engine.paper.plugin
import fr.legendsofxania.dungeon.entry.manifest.definition.DungeonDefinitionEntry
import fr.legendsofxania.dungeon.entry.manifest.definition.RoomDefinitionEntry
import fr.legendsofxania.dungeon.interaction.dungeon.instances.DungeonInstance
import fr.legendsofxania.dungeon.interaction.dungeon.instances.RoomInstance
import fr.legendsofxania.dungeon.util.UUIDDataType
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player

class PlayerManager(
    private val instanceManager: InstanceManager
) {
    private val dungeonKey = NamespacedKey(plugin, "dungeon_instance")
    private val roomKey = NamespacedKey(plugin, "room_instance")

    fun setDungeonInstance(
        player: Player, dungeonInstance: DungeonInstance
    ) {
        player.persistentDataContainer.set(dungeonKey, UUIDDataType.INSTANCE, dungeonInstance.id)
    }

    fun getDungeonInstance(player: Player): DungeonInstance? {
        val dungeonInstanceId = player.persistentDataContainer.get(dungeonKey, UUIDDataType.INSTANCE)
            ?: return null

        return instanceManager.getDungeonInstance(dungeonInstanceId)
    }

    fun checkDungeonInstance(player: Player, dungeon: Ref<DungeonDefinitionEntry>): Boolean {
        val dungeonInstance = getDungeonInstance(player)

        return if (dungeon.isSet) dungeonInstance?.definition == dungeon
        else dungeonInstance != null
    }

    fun removeDungeonInstance(player: Player) {
        player.persistentDataContainer.remove(dungeonKey)
    }

    fun setRoomInstance(
        player: Player, roomInstance: RoomInstance
    ) {
        player.persistentDataContainer.set(roomKey, UUIDDataType.INSTANCE, roomInstance.id)
    }

    fun getRoomInstance(player: Player): RoomInstance? {
        val roomInstanceId = player.persistentDataContainer.get(roomKey, UUIDDataType.INSTANCE)
            ?: return null

        val dungeonInstance = getDungeonInstance(player) ?: return null

        return dungeonInstance.rooms[roomInstanceId]
    }

    fun checkRoomInstance(player: Player, room: Ref<RoomDefinitionEntry>): Boolean {
        val roomInstance = getRoomInstance(player)

        return if (room.isSet) roomInstance?.definition == room
        else roomInstance != null
    }

    fun removeRoomInstance(player: Player) {
        player.persistentDataContainer.remove(roomKey)
    }
}