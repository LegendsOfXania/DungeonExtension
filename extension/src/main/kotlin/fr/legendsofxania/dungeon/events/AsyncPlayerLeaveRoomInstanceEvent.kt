package fr.legendsofxania.dungeon.events

import com.typewritermc.core.entries.Ref
import fr.legendsofxania.dungeon.entries.manifest.definition.RoomDefinitionEntry
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class AsyncPlayerLeaveRoomInstanceEvent(
    player: Player,
    val definition: Ref<RoomDefinitionEntry>
) : PlayerEvent(player, true) {

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList
}