package fr.legendsofxania.dungeon.interaction.dungeon.trigger

import com.typewritermc.engine.paper.entry.entries.EventTrigger
import fr.legendsofxania.dungeon.entry.action.StartDungeonInstanceActionEntry

data class DungeonStartTrigger(
    val priority: Int,
    val eventTriggers: List<EventTrigger> = emptyList(),
    val entry: StartDungeonInstanceActionEntry
) : EventTrigger {
    override val id = "dungeon.start"
}