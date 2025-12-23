package fr.legendsofxania.dungeon.interactions.dungeon

import com.typewritermc.core.interaction.Interaction
import com.typewritermc.core.interaction.InteractionContext
import com.typewritermc.core.utils.ok
import com.typewritermc.engine.paper.entry.entries.EventTrigger
import fr.legendsofxania.dungeon.entries.action.StartDungeonInstanceActionEntry
import org.bukkit.entity.Player
import java.time.Duration


class DungeonInteraction(
    val player: Player,
    override val context: InteractionContext,
    override val priority: Int,
    val eventTriggers: List<EventTrigger>,
    val entry: StartDungeonInstanceActionEntry
) : Interaction {
    override suspend fun initialize(): Result<Unit> {

        return ok(Unit)
    }

    override suspend fun tick(deltaTime: Duration) {

        // if (shouldEnd()) DungeonStopTrigger.triggerFor(player, context)
    }

    override suspend fun teardown() {
    }

}

data class DungeonStartTrigger(
    val priority: Int,
    val eventTriggers: List<EventTrigger> = emptyList(),
    val entry: StartDungeonInstanceActionEntry
) : EventTrigger {
    override val id = "dungeon.start"
}

data object DungeonStopTrigger : EventTrigger {
    override val id = "dungeon.stop"
}