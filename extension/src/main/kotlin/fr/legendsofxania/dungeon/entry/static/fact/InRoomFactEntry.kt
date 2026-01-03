package fr.legendsofxania.dungeon.entry.static.fact

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.entries.Ref
import com.typewritermc.core.entries.emptyRef
import com.typewritermc.core.extension.annotations.Entry
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.entry.entries.GroupEntry
import com.typewritermc.engine.paper.entry.entries.ReadableFactEntry
import com.typewritermc.engine.paper.entry.entries.Var
import com.typewritermc.engine.paper.facts.FactData
import com.typewritermc.engine.paper.interaction.interactionContext
import fr.legendsofxania.dungeon.entry.manifest.definition.RoomDefinitionEntry
import fr.legendsofxania.dungeon.manager.InstanceManager
import fr.legendsofxania.dungeon.manager.PlayerManager
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Entry(
    "in_room_fact",
    "If the player is in a RoomInstance.",
    Colors.PURPLE,
    "tabler:building-arc"
)
/**
 * The `In Room Fact` entry is used to check if a player is currently in a RoomInstance.
 *
 * ## How could this be used?
 *
 * This could be used to give players certain stats or abilities while they are in a room.
 */
class InRoomFactEntry(
    override val id: String = "",
    override var name: String = "",
    override val comment: String = "",
    override val group: Ref<GroupEntry> = emptyRef(),
    val room: Var<Ref<RoomDefinitionEntry>> = ConstVar(emptyRef())
) : ReadableFactEntry, KoinComponent {
    private val instanceManager: InstanceManager by inject()

    override fun readSinglePlayer(player: Player): FactData =
        FactData(
            if (PlayerManager(instanceManager)
                    .checkRoomInstance(player, room.get(player, player.interactionContext))
            ) 1 else 0
        )
}
