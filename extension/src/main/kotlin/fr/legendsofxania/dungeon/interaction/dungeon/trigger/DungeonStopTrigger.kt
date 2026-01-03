package fr.legendsofxania.dungeon.interaction.dungeon.trigger

import com.typewritermc.engine.paper.entry.entries.EventTrigger

data object DungeonStopTrigger : EventTrigger {
    override val id = "dungeon.stop"
}