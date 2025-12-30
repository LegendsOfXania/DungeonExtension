package fr.legendsofxania.dungeon.entries.manifest.audience

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.entries.Ref
import com.typewritermc.core.entries.emptyRef
import com.typewritermc.core.entries.ref
import com.typewritermc.core.extension.annotations.Entry
import com.typewritermc.engine.paper.entry.entries.AudienceFilter
import com.typewritermc.engine.paper.entry.entries.AudienceFilterEntry
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.entry.entries.Invertible
import com.typewritermc.engine.paper.entry.entries.Var
import com.typewritermc.engine.paper.interaction.interactionContext
import fr.legendsofxania.dungeon.entries.manifest.definition.DungeonDefinitionEntry
import fr.legendsofxania.dungeon.events.AsyncPlayerJoinDungeonInstanceEvent
import fr.legendsofxania.dungeon.events.AsyncPlayerLeaveDungeonInstanceEvent
import fr.legendsofxania.dungeon.managers.InstanceManager
import fr.legendsofxania.dungeon.managers.PlayerManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Entry(
    "in_dungeon_audience",
    "Return if a player is in a DungeonInstance or not.",
    Colors.GREEN,
    "tabler:building"
)
/**
 * The `In Dungeon Audience` entry is used to check if a player is currently in a DungeonInstance.
 *
 * ## How could this be used?
 *
 * This could be used to apply effects to players who are in a dungeon.
 */
class InDungeonAudienceEntry(
    override val id: String = "",
    override val name: String = "",
    override val children: List<Ref<out AudienceFilterEntry>> = emptyList(),
    val dungeon: Var<Ref<DungeonDefinitionEntry>> = ConstVar(emptyRef()),
    override val inverted: Boolean = false
) : AudienceFilterEntry, Invertible {
    override suspend fun display() = InDungeonAudienceFilter(ref(), dungeon)
}

class InDungeonAudienceFilter(
    ref: Ref<out AudienceFilterEntry>,
    private val dungeon: Var<Ref<DungeonDefinitionEntry>>
) : AudienceFilter(ref), KoinComponent {
    private val instanceManager: InstanceManager by inject()

    override fun filter(player: Player): Boolean =
        PlayerManager(instanceManager).checkDungeonInstance(player, dungeon.get(player, player.interactionContext))

    @EventHandler
    fun onPlayerJoinDungeonInstance(event: AsyncPlayerJoinDungeonInstanceEvent) = event.player.refresh()

    @EventHandler
    fun onPlayerLeaveDungeonInstance(event: AsyncPlayerLeaveDungeonInstanceEvent) = event.player.refresh()
}
