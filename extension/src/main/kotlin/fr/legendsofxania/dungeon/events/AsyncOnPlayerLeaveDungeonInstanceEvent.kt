package fr.legendsofxania.dungeon.events

import com.typewritermc.core.entries.Ref
import fr.legendsofxania.dungeon.entries.manifest.definition.DungeonDefinitionEntry
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class AsyncOnPlayerLeaveDungeonInstanceEvent(
    player: Player,
    val definition: Ref<DungeonDefinitionEntry>
) : PlayerEvent(player, true) {

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList
}