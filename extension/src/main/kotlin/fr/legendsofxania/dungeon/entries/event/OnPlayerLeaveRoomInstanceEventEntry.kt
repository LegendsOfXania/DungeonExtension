package fr.legendsofxania.dungeon.entries.event

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.entries.Query
import com.typewritermc.core.entries.Ref
import com.typewritermc.core.entries.emptyRef
import com.typewritermc.core.extension.annotations.Entry
import com.typewritermc.core.extension.annotations.EntryListener
import com.typewritermc.core.interaction.context
import com.typewritermc.engine.paper.entry.TriggerableEntry
import com.typewritermc.engine.paper.entry.entries.EventEntry
import com.typewritermc.engine.paper.entry.triggerAllFor
import fr.legendsofxania.dungeon.entries.manifest.definition.RoomDefinitionEntry
import fr.legendsofxania.dungeon.events.AsyncPlayerLeaveRoomInstanceEvent

@Entry(
    "on_player_leave_room_instance_event",
    "Triggers when a player leaves a RoomInstance.",
    Colors.YELLOW,
    "carbon:build-image"
)
/**
 * The `On Player Leave Room Event Entry` entry is used to define an event that triggers when a player leaves a room.
 *
 * ## How could this be used?
 *
 * This could be used to execute a sequence when a player leaves a room.
 */
class OnPlayerLeaveRoomInstanceEventEntry(
    override val id: String = "",
    override val name: String = "",
    override val triggers: List<Ref<TriggerableEntry>> = emptyList(),
    val definition: Ref<RoomDefinitionEntry> = emptyRef()
) : EventEntry

@EntryListener(OnPlayerLeaveRoomInstanceEventEntry::class)
fun onPlayerLeaveRoomInstanceEventListener(
    event: AsyncPlayerLeaveRoomInstanceEvent,
    query: Query<OnPlayerLeaveRoomInstanceEventEntry>
) {
    query.findWhere { it.definition == event.definition }.triggerAllFor(event.player, context())
}