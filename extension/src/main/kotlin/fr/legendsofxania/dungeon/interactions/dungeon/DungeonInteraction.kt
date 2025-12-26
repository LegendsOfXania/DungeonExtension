package fr.legendsofxania.dungeon.interactions.dungeon

import com.typewritermc.core.interaction.Interaction
import com.typewritermc.core.interaction.InteractionContext
import com.typewritermc.core.utils.ok
import com.typewritermc.engine.paper.entry.entries.EventTrigger
import com.typewritermc.engine.paper.entry.triggerFor
import fr.legendsofxania.dungeon.entries.action.StartDungeonInstanceActionEntry
import fr.legendsofxania.dungeon.interactions.dungeon.trigger.DungeonStopTrigger
import fr.legendsofxania.dungeon.managers.InstanceManager
import fr.legendsofxania.dungeon.managers.PlayerManager
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Duration

class DungeonInteraction(
    val player: Player,
    override val context: InteractionContext,
    override val priority: Int,
    val eventTriggers: List<EventTrigger>,
    val entry: StartDungeonInstanceActionEntry
) : Interaction, KoinComponent {
    private val instanceManager: InstanceManager by inject()

    override suspend fun initialize(): Result<Unit> {

        return ok(Unit)
    }

    override suspend fun tick(deltaTime: Duration) {

         if (PlayerManager(instanceManager).getDungeonInstance(player) == null) DungeonStopTrigger.triggerFor(player, context)
    }

    override suspend fun teardown() {

    }
}