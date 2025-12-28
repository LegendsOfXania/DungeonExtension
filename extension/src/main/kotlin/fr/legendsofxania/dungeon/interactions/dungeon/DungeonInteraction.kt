package fr.legendsofxania.dungeon.interactions.dungeon

import com.typewritermc.core.interaction.Interaction
import com.typewritermc.core.interaction.InteractionContext
import com.typewritermc.core.utils.ok
import com.typewritermc.engine.paper.entry.entries.EventTrigger
import com.typewritermc.engine.paper.entry.triggerFor
import com.typewritermc.engine.paper.utils.toBukkitLocation
import fr.legendsofxania.dungeon.entries.action.StartDungeonInstanceActionEntry
import fr.legendsofxania.dungeon.events.AsyncPlayerJoinDungeonInstanceEvent
import fr.legendsofxania.dungeon.events.AsyncPlayerLeaveDungeonInstanceEvent
import fr.legendsofxania.dungeon.interactions.dungeon.trigger.DungeonStopTrigger
import fr.legendsofxania.dungeon.managers.InstanceManager
import fr.legendsofxania.dungeon.managers.PlayerManager
import fr.legendsofxania.dungeon.managers.StructureManager
import fr.legendsofxania.dungeon.managers.WorldManager
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
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

    private val dungeonEntry = entry.dungeon
    private val dungeonLocation = WorldManager.startDungeon()
    private val dungeonInstance = instanceManager.startDungeonInstance(dungeonEntry, dungeonLocation)

    override suspend fun initialize(): Result<Unit> {
        StructureManager(instanceManager).placeRooms(player, context, dungeonInstance)
        PlayerManager(instanceManager).setDungeonInstance(player, dungeonInstance)

        val world = dungeonLocation.world
            ?: error("Dungeon world not found")
        val coordinate = dungeonEntry.entry?.respawnLocation?.get(player, context)?.toBukkitLocation(world)
            ?: error("Spawn location not found for DungeonDefinitionEntry: ${dungeonEntry.id}")

        player.teleportAsync(coordinate, PlayerTeleportEvent.TeleportCause.PLUGIN)

        AsyncPlayerJoinDungeonInstanceEvent(player, dungeonEntry).callEvent()

        return ok(Unit)
    }

    override suspend fun tick(deltaTime: Duration) {
         if (PlayerManager(instanceManager).getDungeonInstance(player) == null) DungeonStopTrigger.triggerFor(player, context)
    }

    override suspend fun teardown() {
        StructureManager(instanceManager).deleteRooms(dungeonInstance)
        WorldManager.stopDungeon(dungeonLocation)
        instanceManager.stopDungeonInstance(dungeonInstance)

        AsyncPlayerLeaveDungeonInstanceEvent(player, dungeonEntry).callEvent()
    }
}