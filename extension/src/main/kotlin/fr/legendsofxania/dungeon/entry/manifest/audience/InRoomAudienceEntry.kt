package fr.legendsofxania.dungeon.entry.manifest.audience

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.entries.Ref
import com.typewritermc.core.entries.emptyRef
import com.typewritermc.core.entries.ref
import com.typewritermc.core.extension.annotations.Entry
import com.typewritermc.engine.paper.entry.entries.*
import com.typewritermc.engine.paper.interaction.interactionContext
import fr.legendsofxania.dungeon.entry.manifest.definition.RoomDefinitionEntry
import fr.legendsofxania.dungeon.event.AsyncPlayerJoinRoomInstanceEvent
import fr.legendsofxania.dungeon.event.AsyncPlayerLeaveRoomInstanceEvent
import fr.legendsofxania.dungeon.manager.InstanceManager
import fr.legendsofxania.dungeon.manager.PlayerManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Entry(
    "in_room_audience",
    "Return if a player is in a RoomInstance or not.",
    Colors.GREEN,
    "tabler:building-arch"
)
/**
 * The `In Room Audience` entry is used to check if a player is currently in a RoomInstance.
 *
 * ## How could this be used?
 *
 * This could be used to apply effects to players who are in a room.
 */
class InRoomAudienceEntry(
    override val id: String = "",
    override val name: String = "",
    override val children: List<Ref<out AudienceEntry>> = emptyList(),
    val room: Var<Ref<RoomDefinitionEntry>> = ConstVar(emptyRef()),
    override val inverted: Boolean = false
) : AudienceFilterEntry, Invertible {
    override suspend fun display(): AudienceFilter = InRoomAudienceFilter(ref(), room)
}

class InRoomAudienceFilter(
    ref: Ref<out AudienceFilterEntry>,
    val room: Var<Ref<RoomDefinitionEntry>>
) : AudienceFilter(ref), KoinComponent {
    private val instanceManager: InstanceManager by inject()

    override fun filter(player: Player): Boolean =
        PlayerManager(instanceManager).checkRoomInstance(player, room.get(player, player.interactionContext))

    @EventHandler
    fun onPlayerJoinRoomInstance(event: AsyncPlayerJoinRoomInstanceEvent) = event.player.refresh()

    @EventHandler
    fun onPlayerLeaveRoomInstance(event: AsyncPlayerLeaveRoomInstanceEvent) = event.player.refresh()
}